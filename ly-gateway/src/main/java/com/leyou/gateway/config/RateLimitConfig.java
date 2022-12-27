package com.leyou.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Configuration
@Slf4j
public class RateLimitConfig {
    //定义一个KeyResolver
    @Bean
    public KeyResolver ipKeyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                String hostName = exchange.getRequest().getRemoteAddress().getHostName();
                log.info("当前请求者的host地址：{}",hostName);
                return Mono.just(hostName);
            }
        };
        // JDK8 的Lambda写法：
        // return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }
}