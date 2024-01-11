package com.itender.elasticsearch.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.ParsedMax;
import org.elasticsearch.search.aggregations.metrics.ParsedMin;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: ITender
 * @Date: 2022/05/01/ 10:19
 * @Description:
 */
public class EsDocumentSearch {

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public EsDocumentSearch(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 查询文档-查询所有
     *
     * @throws IOException
     */
    public void matchAllDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("【查询文档】结果为：" + hit.getSourceAsString());
        }
    }

    /**
     * 查询文档-termQuery所有
     *
     * @throws IOException
     */
    public void termQueryDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("age", 26));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("【查询文档】结果为：" + hit.getSourceAsString());
        }
    }

    /**
     * 查询文档-分页查询
     *
     * @throws IOException
     */
    public void pageQueryDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 分页查询
        sourceBuilder.query(QueryBuilders.matchAllQuery()).from(0).size(2);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("【分页查询文档】结果为：" + hit.getSourceAsString());
        }
    }

    /**
     * 查询文档-排序
     *
     * @throws IOException
     */
    public void sortQueryDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 分页查询
        sourceBuilder.query(QueryBuilders.matchAllQuery()).sort("age", SortOrder.DESC);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("【查询文档排序】结果为：" + hit.getSourceAsString());
        }
    }


    /**
     * 查询文档-字段过滤
     *
     * @throws IOException
     */
    public void filterDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        String[] include = {"name"};
        String[] exclude = {"sex"};
        sourceBuilder.query(QueryBuilders.matchAllQuery())
                .fetchSource(include, exclude);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("【查询文档字段过滤】结果为：" + hit.getSourceAsString());
        }
    }


    /**
     * 查询文档-多条件组合查询
     *
     * @throws IOException
     */
    public void boolQueryDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // must必须,&&
        boolQueryBuilder.must(QueryBuilders.termQuery("age", 26));
        // should or ||
        boolQueryBuilder.should(QueryBuilders.termQuery("sex", "男"));
        boolQueryBuilder.should(QueryBuilders.termQuery("sex", "女"));
        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("【查询文档多条件】结果为：" + hit.getSourceAsString());
        }
    }


    /**
     * 查询文档-多条件组合查询-范围
     *
     * @throws IOException
     */
    public void rangeQueryDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age").gte(25).lte(40);
        sourceBuilder.query(rangeQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("【查询文档范围】结果为：" + hit.getSourceAsString());
        }
    }


    /**
     * 查询文档-多条件组合查询-模糊查询
     *
     * @throws IOException
     */
    public void fuzzyQueryDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("name", "小").fuzziness(Fuzziness.ONE);
        sourceBuilder.query(fuzzyQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println("【查询文档模糊】结果为：" + hit.getSourceAsString());
        }
    }


    /**
     * 查询文档-多条件组合查询-高亮查询
     *
     * @throws IOException
     */
    public void highLightQueryDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "张三");
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name").requireFieldMatch(false).field("description").preTags("<span style='color:red;'>").postTags("</span>");
        sourceBuilder.query(matchQueryBuilder).highlighter(highlightBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.out.println(hit.getSourceAsString());
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("name")) {
                System.out.println("【查询文档高亮】结果为：" + highlightFields.get("name").fragments()[0]);
            }
        }
    }


    /**
     * 聚合操作
     *
     * @throws IOException
     */
    public void aggregationQueryDocument() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery())
                .aggregation(AggregationBuilders.sum("age_sum").field("age"))
                .aggregation(AggregationBuilders.max("age_max").field("age"))
                .aggregation(AggregationBuilders.min("age_min").field("age"))
                .aggregation(AggregationBuilders.avg("age_avg").field("age"))
                .size(0);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedSum sum = aggregations.get("age_sum");
        ParsedMax max = aggregations.get("age_max");
        ParsedMin min = aggregations.get("age_min");
        ParsedAvg avg = aggregations.get("age_avg");
        System.out.println(sum.getValue());
        System.out.println(max.getValue());
        System.out.println(min.getValue());
        System.out.println(avg.getValue());
    }
}
