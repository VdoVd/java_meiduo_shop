package com.leyou;

import com.leyou.auth.annotation.EnableJwtVerification;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.leyou.user.mapper")
@EnableFeignClients
//用户拦截器开关，
@EnableJwtVerification
public class LyUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyUserApplication.class);
    }
}
