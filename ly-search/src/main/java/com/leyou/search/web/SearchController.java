package com.leyou.search.web;

import com.leyou.search.dto.SearchRequest;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import com.leyou.starter.elastic.entity.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 根据输入的key进行自动补全提示
     * @param key
     * @return
     */
    @GetMapping("/suggestion")
    public Mono<List<String>> getSuggest(@RequestParam("key")String key){

        return this.searchService.getSuggest(key);
    }

    /**
     * 查询业务
     * @param searchRequest
     * @return
     */
    @PostMapping("/list")
    public Mono<PageInfo<Goods>> listData(@RequestBody SearchRequest searchRequest){

        return this.searchService.listData(searchRequest);
    }


    /**
     * 查询业务
     * @param searchRequest
     * @return
     */
    @PostMapping("/filter")
    public Mono<Map<String,List<?>>> listFilter(@RequestBody SearchRequest searchRequest){

        return this.searchService.listFilter(searchRequest);
    }
}
