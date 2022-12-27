package com.leyou.gateway.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/hystrix/fallback")
    public ResponseEntity<String> fallback(){

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("网关超时");
    }
}
