package com.leyou.search.service.impl;

import com.leyou.common.exception.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import com.leyou.starter.elastic.entity.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ItemClient itemClient;


    @Override
    public Mono<List<String>> getSuggest(String key) {

        if (StringUtils.isEmpty(key)) {
            throw new LyException(400, "查询关键字不能为空");
        }

        goodsRepository.queryById(10086L);

        return goodsRepository.suggestBySingleField("suggestion", key);

    }

    @Override
    public Mono<PageInfo<Goods>> listData(SearchRequest searchRequest) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();


        //查询条件
        sourceBuilder.query(getQueryBuilder(searchRequest));

        //分页条件
        sourceBuilder.from((searchRequest.getPage() - 1) * searchRequest.getSIZE());
        sourceBuilder.size(searchRequest.getSIZE());

        sourceBuilder.fetchSource(new String[]{"id","title","image","categoryId","brandId","specs","prices","sold","updateTime"},null);

        //排序条件,如果sortBy不为空，则根据sortBy作为排序字段排序,升序还是降序取决于desc的值如果为true则降序，
        if (StringUtils.isNotEmpty(searchRequest.getSortBy())) {
            sourceBuilder.sort(SortBuilders.fieldSort(searchRequest.getSortBy())
                    .order(searchRequest.getDesc() ? SortOrder.DESC : SortOrder.ASC));
        }

        //高亮条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<am>");
        highlightBuilder.postTags("</am>");
        highlightBuilder.field("title");
        sourceBuilder.highlighter(highlightBuilder);


        return this.goodsRepository.queryBySourceBuilderForPageHighlight(sourceBuilder);
    }

    //构建查询条件的方法
    private QueryBuilder getQueryBuilder(SearchRequest searchRequest) {

        String key = searchRequest.getKey();

        if (StringUtils.isEmpty(key)) {
            throw new LyException(400, "查询条件不能为空");
        }

        //创建boolean查询条件构建对象
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();

        //添加查询条件
        queryBuilder.must(QueryBuilders.matchQuery("title", key).operator(Operator.AND));


        //获取所有的过滤条件
        Map<String, String> filters = searchRequest.getFilters();

        //如果过滤条件不为空
        if (!CollectionUtils.isEmpty(filters)){

            filters.entrySet().forEach(entry->{
                String filterKey = entry.getKey();
                String filterValue = entry.getValue();

                if ("分类".equals(filterKey)){
                    queryBuilder.filter(QueryBuilders.termQuery("categoryId",filterValue));
                }else if ("品牌".equals(filterKey)){
                    queryBuilder.filter(QueryBuilders.termQuery("brandId",filterValue));
                }else{
                    //其他过滤参数，要使用nested查询
                    BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                    //filterKey对应的属性为 specs.name
                    nestedBoolQuery.must(QueryBuilders.matchQuery("specs.name",filterKey));

                    //filtersValue对应的属性为 specs.value
                    nestedBoolQuery.must(QueryBuilders.matchQuery("specs.value",filterValue));
                    //添加一个nested查询，nested内部为布尔查询，
                    queryBuilder.filter(QueryBuilders.nestedQuery("specs",nestedBoolQuery, ScoreMode.None));
                }

            });

        }
        return queryBuilder;
    }

    @Override
    public Mono<Map<String, List<?>>> listFilter(SearchRequest searchRequest) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //获取查询条件
        sourceBuilder.query(getQueryBuilder(searchRequest));

        //只查询不展示
        sourceBuilder.from(0);
        sourceBuilder.size(0);


        //添加聚合条件，并设置分桶的最大展示数量
        sourceBuilder.aggregation(AggregationBuilders.terms("brandAgg").field("brandId").size(50));

        sourceBuilder.aggregation(AggregationBuilders.terms("categoryAgg").field("categoryId").size(50));


        //添加其他条件，对其他可搜索过滤条件进行聚合
        sourceBuilder.aggregation(
                AggregationBuilders.nested("specAgg", "specs")
                        .subAggregation(AggregationBuilders.terms("nameAgg").field("specs.name").size(50)
                                .subAggregation(AggregationBuilders.terms("valueAgg").field("specs.value").size(50))));


        //把Mono<Aggregations>===>Mono<Map<String,List<?>>>
        return this.goodsRepository.aggregationBySourceBuilder(sourceBuilder)
                .map(aggregations -> {

                    Map<String, List<?>> resultMap = new LinkedHashMap<>();


                    //根据聚合名称取值
                    Terms categoryAgg = aggregations.get("categoryAgg");

                    //获取到分类id的集合
                    List<Long> categoryIds = categoryAgg
                            .getBuckets()
                            .stream()
                            .map(bucket -> ((Terms.Bucket) bucket)
                                    .getKeyAsNumber()
                                    .longValue())
                            .collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(categoryIds)) {
                        resultMap.put("分类", this.itemClient.listCategoryByIds(categoryIds));
                        ;
                    }


                    Terms brandAgg = aggregations.get("brandAgg");

                    //品牌id集合
                    List<Long> brandIds = brandAgg
                            .getBuckets()
                            .stream()
                            .map(bucket -> ((Terms.Bucket) bucket)
                                    .getKeyAsNumber()
                                    .longValue())
                            .collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(brandIds)) {
                        resultMap.put("品牌", this.itemClient.listBrandByIds(brandIds));
                    }

                    //解析过滤参数的聚合结果
                    Nested specAgg = aggregations.get("specAgg");

                    Terms nameAgg = specAgg.getAggregations().get("nameAgg");

                    nameAgg.getBuckets().forEach(bucket->{

                        //获取到过滤参数的名称
                        String key = ((Terms.Bucket) bucket).getKeyAsString();

                        Terms valueAgg = ((Terms.Bucket) bucket).getAggregations().get("valueAgg");

                        //valueList就是每个规格属性对应的值的集合
                        List<String> valueList = valueAgg.getBuckets().stream().map(bkt -> bkt.getKeyAsString()).collect(Collectors.toList());

                        resultMap.put(key, valueList);
                    });


                    return resultMap;

                });

    }
}
