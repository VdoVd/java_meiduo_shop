package com.leyou.auth.config;

import com.leyou.auth.client.AuthClient;
import com.leyou.auth.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;


@Slf4j
@Configuration
//条件注解,控制配置类是否生效
@ConditionalOnProperty(prefix = "ly.auth", name = {"clientId", "secret"})
@EnableConfigurationProperties(ClientProperties.class)
public class AuthConfiguration {

    private AuthClient authClient;
    private ClientProperties properties;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public AuthConfiguration(AuthClient authClient, ClientProperties properties) {
        this.authClient = authClient;
        this.properties = properties;
    }

    @Bean
    @Primary
    public JwtUtils jwtUtils(){
        try {
            // 查询秘钥
            String key = authClient.getKey(properties.getClientId(), properties.getSecret());
            // 创建JwtUtils
            JwtUtils jwtUtils = new JwtUtils(key,redisTemplate);
            log.info("秘钥加载成功。");
            return jwtUtils;
        } catch (Exception e) {
            log.error("初始化JwtUtils失败，{}", e.getMessage());
            throw e;
        }
    }
}