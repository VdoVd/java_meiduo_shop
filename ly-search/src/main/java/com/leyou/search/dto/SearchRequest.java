package com.leyou.search.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SearchRequest {
    private Boolean desc;
    private String key;
    private Integer page = 1;
    private String sortBy;
    private Map<String,String> filters;
    //SIZE，定值，除了最后一页之外其他页都展示20
    private final Integer SIZE = 20;
}
