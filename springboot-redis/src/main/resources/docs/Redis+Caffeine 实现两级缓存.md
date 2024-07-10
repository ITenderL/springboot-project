# Redis+Caffeine 实现两级缓存

## 背景

​	事情的开始是这样的，前段时间接了个需求，给公司的商城官网提供一个查询预计送达时间的接口。接口很简单，根据请求传的城市+仓库+发货时间查询快递的预计送达时间。因为商城下单就会调用这个接口，所以对接口的性能要求还是挺高的，据老员工的说法是特别是大促的时候，访问量还是比较大的。

​	因为数据量不是很大，每天会全量推今天和明天的预计送达时间到MySQL，总数据量大约7k+。每次推完数据后会把数据全量写入到redis中，做一个缓存预热，然后设置过期时间为1天。

​	鉴于之前Redis集群出现过压力过大查询缓慢的情况，进一步保证接口的高性能和高可用，防止redis出现压力大，查询慢，缓存雪崩，缓存穿透等问题，我们最终采用了Reids + Caffeine两级缓存的策略。

### 本地缓存优缺点

优点：

1. 本地缓存，基于本地内存，查询速度是很快的。适用于：实时性要求不高，更新频率不高等场景。（我们的数据每天凌晨更新一次，总量7k左右）
2. 查询本地缓存与查询远程缓存相比可以减少网络的I/O，降低网络上的一些消耗。（我们的redis之前出现过查询缓慢的情况）

缺点：

1. Caffeine既然是本地缓存，在分布式环境的情况下就要考虑各个节点之间缓存的一致性问题，一个节点的本地缓存更新了，怎么可以同步到其他的节点。
1. Caffeine不支持持久化的存储。
1. Caffeine使用本地内存，需要合理设置大小，避免内存溢出。

## 流程图

![二级缓存查询流程图](assets\二级缓存查询流程图.png)



## 代码实现

### MySQL表

``` sql
CREATE TABLE `t_estimated_arrival_date`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `warehouse_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '货仓id',
  `warehouse` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发货仓',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '签收城市',
  `delivery_date` date NULL DEFAULT NULL COMMENT '发货时间',
  `estimated_arrival_date` date NULL DEFAULT NULL COMMENT '预计到货日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_warehouse_id_city_delivery_date`(`warehouse_id`, `city`, `delivery_date`) USING BTREE
) ENGINE = InnoDB  COMMENT = '预计到货时间表(具体到day：T, T+1,近90天到货时间众数)' ROW_FORMAT = Dynamic;

INSERT INTO `t_estimated_arrival_date` VALUES (9, '6', '湖熟正常仓', '兰州市', '2024-07-08', '2024-07-10');
INSERT INTO `t_estimated_arrival_date` VALUES (10, '6', '湖熟正常仓', '兰州市', '2024-07-09', '2024-07-11');
INSERT INTO `t_estimated_arrival_date` VALUES (11, '6', '湖熟正常仓', '兴安盟', '2024-07-08', '2024-07-11');
INSERT INTO `t_estimated_arrival_date` VALUES (12, '6', '湖熟正常仓', '兴安盟', '2024-07-09', '2024-07-12');
INSERT INTO `t_estimated_arrival_date` VALUES (13, '6', '湖熟正常仓', '其他', '2024-07-08', '2024-07-19');
INSERT INTO `t_estimated_arrival_date` VALUES (14, '6', '湖熟正常仓', '其他', '2024-07-09', '2024-07-20');
INSERT INTO `t_estimated_arrival_date` VALUES (15, '6', '湖熟正常仓', '内江市', '2024-07-08', '2024-07-10');
INSERT INTO `t_estimated_arrival_date` VALUES (16, '6', '湖熟正常仓', '内江市', '2024-07-09', '2024-07-11');
INSERT INTO `t_estimated_arrival_date` VALUES (17, '6', '湖熟正常仓', '凉山彝族自治州', '2024-07-08', '2024-07-11');
INSERT INTO `t_estimated_arrival_date` VALUES (18, '6', '湖熟正常仓', '凉山彝族自治州', '2024-07-09', '2024-07-12');
INSERT INTO `t_estimated_arrival_date` VALUES (19, '6', '湖熟正常仓', '包头市', '2024-07-08', '2024-07-11');
INSERT INTO `t_estimated_arrival_date` VALUES (20, '6', '湖熟正常仓', '包头市', '2024-07-09', '2024-07-12');
INSERT INTO `t_estimated_arrival_date` VALUES (21, '6', '湖熟正常仓', '北京城区', '2024-07-08', '2024-07-10');
INSERT INTO `t_estimated_arrival_date` VALUES (22, '6', '湖熟正常仓', '北京城区', '2024-07-09', '2024-07-11');
```



### pom.xm

``` xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <!--redis连接池-->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.28</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.3.1</version>
        </dependency>
```

### application.yml

``` yaml
server:
  port: 9001
spring:
  application:
    name: springboot-redis
  datasource:
    name: demo
    url: jdbc:mysql://localhost:3306/test?userUnicode=true&&characterEncoding=utf8&allowMultiQueries=true&useSSL=false
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: 
    password: 
  # mybatis相关配置
  mybatis-plus:
    mapper-locations: classpath:mapper/*.xml
    configuration:
      cache-enabled: true
      use-generated-keys: true
      default-executor-type: REUSE
      use-actual-param-name: true
      # 打印日志
  #    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  redis:
    host: 192.168.117.73
    port: 6379
    password: root
#  redis:
#    lettuce:
#      cluster:
#        refresh:
#          adaptive: true
#          period: 10S
#      pool:
#        max-idle: 50
#        min-idle: 8
#        max-active: 100
#        max-wait: -1
#    timeout: 100000
#    cluster:
#      nodes:
#        - 192.168.117.73:6379
logging:
  level:
    com.itender.redis.mapper: debug


```

### 配置类

- RedisConfig

``` java
/**
 * @author yuanhewei
 * @date 2024/5/31 16:18
 * @description
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        serializer.setObjectMapper(mapper);
        // 如果不序列化在key value 使用redis客户端工具 直连redis服务器 查看数据时 前面会有一个 \xac\xed\x00\x05t\x00\x05 字符串
        // StringRedisSerializer 来序列化和反序列化 String 类型 redis 的 key value
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        // StringRedisSerializer 来序列化和反序列化 hash 类型 redis 的 key value
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
```

- CaffeineConfig

``` java
/**
 * @author yuanhewei
 * @date 2024/7/9 14:16
 * @description
 */
@Configuration
public class CaffeineConfig {

    /**
     * Caffeine 配置类
     *  initialCapacity：初始缓存空间大小
     *  maximumSize：缓存的最大数量，设置这个值避免内存溢出
     *  expireAfterWrite：指定缓存的过期时间，是最后一次写操作的一个时间
     *  容量的大小要根据自己的实际应用场景设置
     *
     * @return
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 初始大小
                .initialCapacity(128)
                //最大数量
                .maximumSize(1024)
                //过期时间
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public CacheManager cacheManager(){
        CaffeineCacheManager cacheManager=new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(128)
                .maximumSize(1024)
                .expireAfterWrite(60, TimeUnit.SECONDS));
        return cacheManager;
    }
}
```

### Mapper

这里采用了Mybatis Plus

``` java
/**
 * @author yuanhewei
 * @date 2024/7/9 18:11
 * @description
 */
@Mapper
public interface EstimatedArrivalDateMapper extends BaseMapper<EstimatedArrivalDateEntity> {

} 
```

### Service

``` java
/**
 * @author yuanhewei
 * @date 2024/7/9 14:25
 * @description
 */
public interface DoubleCacheService {

    /**
     * 查询一级送达时间-常规方式
     *
     * @param request
     * @return
     */
    EstimatedArrivalDateEntity getEstimatedArrivalDateCommon(EstimatedArrivalDateEntity request);

    /**
     * 查询一级送达时间-注解方式
     *
     * @param request
     * @return
     */
    EstimatedArrivalDateEntity getEstimatedArrivalDate(EstimatedArrivalDateEntity request);
}
```

实现类

``` java
/**
 * @author yuanhewei
 * @date 2024/7/9 14:26
 * @description
 */
@Slf4j
@Service
public class DoubleCacheServiceImpl implements DoubleCacheService {

    @Resource
    private Cache<String, Object> caffeineCache;

    @Resource
    private RedisTemplate<String, Object> RedisTemplate;

    @Resource
    private EstimatedArrivalDateMapper estimatedArrivalDateMapper;

    @Override
    public EstimatedArrivalDateEntity getEstimatedArrivalDateCommon(EstimatedArrivalDateEntity request) {
        String key = request.getDeliveryDate() + RedisConstants.COLON + request.getWarehouseId() + RedisConstants.COLON + request.getCity();
        log.info("Cache key: {}", key);
        Object value = caffeineCache.getIfPresent(key);
        if (Objects.nonNull(value)) {
            log.info("get from caffeine");
            return EstimatedArrivalDateEntity.builder().estimatedArrivalDate(value.toString()).build();
        }
        value = RedisTemplate.opsForValue().get(key);
        if (Objects.nonNull(value)) {
            log.info("get from redis");
            caffeineCache.put(key, value);
            return EstimatedArrivalDateEntity.builder().estimatedArrivalDate(value.toString()).build();
        }
        log.info("get from mysql");
        DateTime deliveryDate = DateUtil.parse(request.getDeliveryDate(), "yyyy-MM-dd");
        EstimatedArrivalDateEntity estimatedArrivalDateEntity = estimatedArrivalDateMapper.selectOne(new QueryWrapper<EstimatedArrivalDateEntity>()
                .eq("delivery_date", deliveryDate)
                .eq("warehouse_id", request.getWarehouseId())
                .eq("city", request.getCity())
        );
        RedisTemplate.opsForValue().set(key, estimatedArrivalDateEntity.getEstimatedArrivalDate(), 120, TimeUnit.SECONDS);
        caffeineCache.put(key, estimatedArrivalDateEntity.getEstimatedArrivalDate());
        return EstimatedArrivalDateEntity.builder().estimatedArrivalDate(estimatedArrivalDateEntity.getEstimatedArrivalDate()).build();
    }

    @DoubleCache(cacheName = "estimatedArrivalDate", key = {"#request.deliveryDate", "#request.warehouseId", "#request.city"},
            type = DoubleCache.CacheType.FULL)
    @Override
    public EstimatedArrivalDateEntity getEstimatedArrivalDate(EstimatedArrivalDateEntity request) {
        DateTime deliveryDate = DateUtil.parse(request.getDeliveryDate(), "yyyy-MM-dd");
        EstimatedArrivalDateEntity estimatedArrivalDateEntity = estimatedArrivalDateMapper.selectOne(new QueryWrapper<EstimatedArrivalDateEntity>()
                .eq("delivery_date", deliveryDate)
                .eq("warehouse_id", request.getWarehouseId())
                .eq("city", request.getCity())
        );
        return EstimatedArrivalDateEntity.builder().estimatedArrivalDate(estimatedArrivalDateEntity.getEstimatedArrivalDate()).build();
    }
}

```

这里的代码本来是采用了常规的写法，没有采用自定义注解的方式，注解的方式是参考了后面那位大佬的文章，加以修改实现的。因为我的CacheKey可能存在多个属性值的组合。

### Annotitions

``` java
/**
 * @author yuanhewei
 * @date 2024/7/9 14:51
 * @description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DoubleCache {
    /**
     * 缓存名称
     *
     * @return
     */
    String cacheName();

    /**
     * 缓存的key，支持springEL表达式
     *
     * @return
     */
    String[] key();

    /**
     * 过期时间，单位：秒
     *
     * @return
     */
    long expireTime() default 120;

    /**
     * 缓存类型
     *
     * @return
     */
    CacheType type() default CacheType.FULL;

    enum CacheType {
        /**
         * 存取
         */
        FULL,

        /**
         * 只存
         */
        PUT,

        /**
         * 删除
         */
        DELETE
    }
}

```

### Aspect

``` java
/**
 * @author yuanhewei
 * @date 2024/7/9 14:51
 * @description
 */
@Slf4j
@Component
@Aspect
public class DoubleCacheAspect {

    @Resource
    private Cache<String, Object> caffeineCache;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Pointcut("@annotation(com.itender.redis.annotation.DoubleCache)")
    public void doubleCachePointcut() {
    }

    @Around("doubleCachePointcut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 拼接解析springEl表达式的map
        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            treeMap.put(paramNames[i], args[i]);
        }
        DoubleCache annotation = method.getAnnotation(DoubleCache.class);
        String elResult = DoubleCacheUtil.arrayParse(Lists.newArrayList(annotation.key()), treeMap);
        String realKey = annotation.cacheName() + RedisConstants.COLON + elResult;
        // 强制更新
        if (annotation.type() == DoubleCache.CacheType.PUT) {
            Object object = point.proceed();
            redisTemplate.opsForValue().set(realKey, object, annotation.expireTime(), TimeUnit.SECONDS);
            caffeineCache.put(realKey, object);
            return object;
        }
        // 删除
        else if (annotation.type() == DoubleCache.CacheType.DELETE) {
            redisTemplate.delete(realKey);
            caffeineCache.invalidate(realKey);
            return point.proceed();
        }
        // 读写，查询Caffeine
        Object caffeineCacheObj = caffeineCache.getIfPresent(realKey);
        if (Objects.nonNull(caffeineCacheObj)) {
            log.info("get data from caffeine");
            return caffeineCacheObj;
        }
        // 查询Redis
        Object redisCache = redisTemplate.opsForValue().get(realKey);
        if (Objects.nonNull(redisCache)) {
            log.info("get data from redis");
            caffeineCache.put(realKey, redisCache);
            return redisCache;
        }
        log.info("get data from database");
        Object object = point.proceed();
        if (Objects.nonNull(object)) {
            // 写入Redis
            log.info("get data from database write to cache: {}", object);
            redisTemplate.opsForValue().set(realKey, object, annotation.expireTime(), TimeUnit.SECONDS);
            // 写入Caffeine
            caffeineCache.put(realKey, object);
        }
        return object;
    }
}

```

因为注解上的配置要支持Spring的EL表达式。

``` java
public static String parse(String elString, SortedMap<String, Object> map) {
        elString = String.format("#{%s}", elString);
        // 创建表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        // 通过evaluationContext.setVariable可以在上下文中设定变量。
        EvaluationContext context = new StandardEvaluationContext();
        map.forEach(context::setVariable);
        // 解析表达式
        Expression expression = parser.parseExpression(elString, new TemplateParserContext());
        // 使用Expression.getValue()获取表达式的值，这里传入了Evaluation上下文
        return expression.getValue(context, String.class);
    }

    public static String arrayParse(List<String> elStrings, SortedMap<String, Object> map) {
        List<String> result = Lists.newArrayList();
        elStrings.forEach(elString -> {
            elString = String.format("#{%s}", elString);
            // 创建表达式解析器
            ExpressionParser parser = new SpelExpressionParser();
            // 通过evaluationContext.setVariable可以在上下文中设定变量。
            EvaluationContext context = new StandardEvaluationContext();
            map.forEach(context::setVariable);
            // 解析表达式
            Expression expression = parser.parseExpression(elString, new TemplateParserContext());
            // 使用Expression.getValue()获取表达式的值，这里传入了Evaluation上下文
            result.add(expression.getValue(context, String.class));
        });
        return String.join(RedisConstants.COLON, result);
    }
```

### Controller

``` java
/**
 * @author yuanhewei
 * @date 2024/7/9 14:14
 * @description
 */
@RestController
@RequestMapping("/doubleCache")
public class DoubleCacheController {

    @Resource
    private DoubleCacheService doubleCacheService;

    @PostMapping("/common")
    public EstimatedArrivalDateEntity getEstimatedArrivalDateCommon(@RequestBody EstimatedArrivalDateEntity estimatedArrivalDate) {
        return doubleCacheService.getEstimatedArrivalDateCommon(estimatedArrivalDate);
    }

    @PostMapping("/annotation")
    public EstimatedArrivalDateEntity getEstimatedArrivalDate(@RequestBody EstimatedArrivalDateEntity estimatedArrivalDate) {
        return doubleCacheService.getEstimatedArrivalDate(estimatedArrivalDate);
    }
}
```

代码中演示了Redis + Caffeine实现两级缓存的方式，一种是传统常规的方式，另一种是基于注解的方式实现的。具体实现可以根据自己项目中的实际场景。

最后的测试结果也是两种方式都可以实现查询先走一级缓存；一级缓存不存在查询二级缓存，然后写入一级缓存；二级缓存不存在，查询MySQL然后写入二级缓存，再写入一级缓存的目的。测试结果就不贴出来了

## 总结

本文介绍Redis+Caffeine实现两级缓存的方式。一种是常规的方式，一种的基于注解的方式。具体的实现可根据自己项目中的业务场景。

至于为什么要用Redis+Caffeine的方式，文章也提到了，目前我们Redis集群压力还算挺大的，而且接口对RT的要求也是比较高的。有一点好的就是我们的数据是每天全量推一边，总量也不大，实时性要求也不强。所以就很适合本地缓存的方式。

使用本地缓存也要注意设置容量的大小和过期时间，否则容易出现内存溢出。

其实现实中很多的场景直接使用Redis就可以搞定的，没必要硬要使用Caffeine。这里也只是简单的介绍了最简单基础的实现方式。对于其他一些复杂的场景还要根据自己具体的业务进行设计。我自己也是边学边用。如果有问题或者其他好的实现方式欢迎各位大佬评论，一起进步！！！

## 参考

https://blog.csdn.net/weixin_45334346/article/details/136310010

