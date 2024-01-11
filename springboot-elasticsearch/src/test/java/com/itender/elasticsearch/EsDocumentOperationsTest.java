package com.itender.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itender.elasticsearch.entity.User;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @Author: ITender
 * @Date: 2022/05/01/ 11:01
 * @Description:
 */
@SpringBootTest
public class EsDocumentOperationsTest {
    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public EsDocumentOperationsTest(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 新增文档
     *
     * @throws IOException
     */
    @Test
    public void insertDocument() throws IOException {
        IndexRequest indexRequest = new IndexRequest("user").id("1001");
        User user = new User();
        user.setName("张三").setAge(25).setSex("男");
        indexRequest.source(MAPPER.writeValueAsString(user), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("【添加文档】结果：" + response.getResult());
    }

    /**
     * 更新文档
     *
     * @throws IOException
     */
    @Test
    public void updateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest().index("user").id("1001");
        updateRequest.doc(XContentType.JSON, "sex", "女");
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println("【文档更新】结果：" + updateResponse.getResult());
    }

    /**
     * 查询文档
     *
     * @throws IOException
     */
    @Test
    public void searchDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("name", "张三"));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("【查询文档】结果为：" + hit.getSourceAsString());
        }
    }

    /**
     * 删除文档
     *
     * @throws IOException
     */
    @Test
    public void deleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index("user").id("1001");
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("【删除文档】结果：" + deleteResponse.getResult());
    }

    /**
     * 批量插入
     *
     * @throws IOException
     */
    @Test
    public void batchInsertDocument() throws IOException {
        BulkRequest bulkRequest = new BulkRequest("user");
        bulkRequest.add(new IndexRequest("user").id("1001").source(MAPPER.writeValueAsString(new User("张三", "男", 22)), XContentType.JSON));
        bulkRequest.add(new IndexRequest("user").id("1002").source(MAPPER.writeValueAsString(new User("李四", "女", 30)), XContentType.JSON));
        bulkRequest.add(new IndexRequest("user").id("1003").source(MAPPER.writeValueAsString(new User("王五", "男", 26)), XContentType.JSON));
        bulkRequest.add(new IndexRequest("user").id("1004").source(MAPPER.writeValueAsString(new User("赵六", "女", 25)), XContentType.JSON));
        bulkRequest.add(new IndexRequest("user").id("1005").source(MAPPER.writeValueAsString(new User("王五", "男", 45)), XContentType.JSON));
        bulkRequest.add(new IndexRequest("user").id("1006").source(MAPPER.writeValueAsString(new User("拉拉啊", "女", 26)), XContentType.JSON));
        bulkRequest.add(new IndexRequest("user").id("1007").source(MAPPER.writeValueAsString(new User("哈哈哈", "女", 38)), XContentType.JSON));
        bulkRequest.add(new IndexRequest("user").id("1008").source(MAPPER.writeValueAsString(new User("小兰", "男", 29)), XContentType.JSON));
        bulkRequest.add(new IndexRequest("user").id("1009").source(MAPPER.writeValueAsString(new User("小红", "男", 16)), XContentType.JSON));
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("【批量新增文档】结果：" + bulkResponse.getItems());
        System.out.println("【批量新增文档】结果：" + bulkResponse.getTook());
    }

    /**
     * 批量删除
     *
     * @throws IOException
     */
    @Test
    public void batchDeleteDocument() throws IOException {
        BulkRequest bulkRequest = new BulkRequest("user");
        bulkRequest.add(new DeleteRequest("user").id("1001"));
        bulkRequest.add(new DeleteRequest("user").id("1002"));
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("【批量删除文档】结果：" + bulkResponse.getItems());
        System.out.println("【批量删除文档】结果：" + bulkResponse.getTook());
    }
}
