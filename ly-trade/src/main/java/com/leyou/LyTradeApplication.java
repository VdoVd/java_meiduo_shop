package com.leyou;

import com.leyou.auth.annotation.EnableJwtVerification;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication

//开启请求拦截器获取用户信息
@EnableJwtVerification
@EnableFeignClients
@MapperScan("com.leyou.trade.mapper")
public class LyTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyTradeApplication.class);
    }
}
