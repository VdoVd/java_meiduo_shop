package com.leyou.search.web;

import com.leyou.search.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/init")
    public ResponseEntity<String> initIndexes(){

        this.indexService.loadData();

        return ResponseEntity.ok("数据导入完成");
    }
}
