package com.itender.elasticsearch;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @Author: ITender
 * @Date: 2022/05/01/ 10:35
 * @Description:
 */
@SpringBootTest
public class EsIndexOperationsTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     */
    @Test
    public void createIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("user");
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println("【索引操作-创建】是否成功：" + createIndexResponse.isAcknowledged());
    }

    /**
     * 查询索引
     */
    @Test
    public void searchIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest("user");
        GetIndexResponse getIndexResponse = restHighLevelClient.indices().get(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println("【索引操作-查询】别名：" + getIndexResponse.getAliases());
        System.out.println("【索引操作-查询】索引：" + getIndexResponse.getIndices());
        System.out.println("【索引操作-查询】设置信息：" + getIndexResponse.getSettings());
    }


    /**
     * 删除索引
     */
    @Test
    public void deleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("user");
        AcknowledgedResponse response = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println("【索引操作-删除】是否删除成功：" + response);
    }
}
