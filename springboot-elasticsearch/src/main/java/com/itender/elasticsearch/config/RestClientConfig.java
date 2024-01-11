package com.itender.elasticsearch.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * @author: ITender
 * @date: 2022/03/13/ 17:41
 * @description: elasticsearch configuration
 */
public class RestClientConfig extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.uris}")
    private String elasticsearchHost;

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(elasticsearchHost)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}
