package com.itender.elasticsearch.config;

import cn.hutool.core.util.StrUtil;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class EsConfig {

    @Value("${elasticsearch.uris}")
    private String uris;

    /**
     * 高版本客户端
     *
     * @return
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        String[] split = uris.split(",");
        HttpHost[] httpHostArray = new HttpHost[split.length];
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            httpHostArray[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "http");
        }
        // 创建RestHighLevelClient客户端
        return new RestHighLevelClient(RestClient.builder(httpHostArray));
    }

    @Bean
    public RestHighLevelClient getRestHighLevelClient() {
        HttpHost[] httpHosts = Arrays.stream(uris.split(","))
                .filter(StrUtil::isNotBlank)
                .map(url -> new HttpHost(url.split(":")[0], Integer.parseInt(url.split(":")[1]), "http"))
                .toArray(HttpHost[]::new);
        // ConnectTimeout: 设置连接超时时间，单位毫秒。SocketTimeout：请求获取数据的超时时间，单位毫秒。
        RestClientBuilder restClientBuilder = RestClient
                .builder(httpHosts)
                .setRequestConfigCallback(builder ->
                        builder.setConnectTimeout(5000)
                                .setSocketTimeout(60000)
                );
        return new RestHighLevelClient(restClientBuilder);
    }
}