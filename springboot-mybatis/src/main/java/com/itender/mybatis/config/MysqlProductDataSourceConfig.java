package com.itender.mybatis.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author yuanhewei
 * @date 2024/1/20 11:18
 * @description mysql数据源配置
 */
// @Configuration
// @MapperScan(basePackages = "cn.sigo.idata.platform.tag.mapper.productMysql", sqlSessionFactoryRef = "productMysqlSessionFactory")
public class MysqlProductDataSourceConfig {

    //
    // /**
    //  * 配置第一个数据源
    //  *
    //  * @return datasource
    //  */
    // @Bean(name = "productMysqlDataSource")
    //     @ConfigurationProperties(prefix = "spring.datasource.product-mysql")
    // public DataSource productMysqlDataSource() {
    //     return DataSourceBuilder.create().type(DruidDataSource.class).build();
    // }
    //
    // @Bean(name = "productMysqlTransactionManager")
    // public DataSourceTransactionManager dbTransactionManager() {
    //     return new DataSourceTransactionManager(productMysqlDataSource());
    // }
    //
    // /**
    //  * 配置第一个sqlSessionFactory
    //  *
    //  * @param dataSource datasource
    //  * @return sqlSessionFactory
    //  * @throws Exception
    //  */
    // @Bean(name = "productMysqlSessionFactory")
    // public SqlSessionFactory productMySqlSessionFactory(@Qualifier("productMysqlDataSource") DataSource dataSource) throws Exception {
    //     final SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    //     bean.setDataSource(dataSource);
    //     bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/productMysql/*.xml"));
    //     // 设置驼峰命名
    //     bean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
    //     return bean.getObject();
    // }
    //

}
