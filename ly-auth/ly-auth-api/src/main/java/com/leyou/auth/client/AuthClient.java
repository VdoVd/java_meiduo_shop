package com.leyou.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("auth-service")
public interface AuthClient {

    /**
     * 根据clientId和secret的校验结果，获取jwtUtils专用key
     * @param clientId
     * @param secret
     * @return
     */
    @GetMapping("/client/key")
    String getKey(
            @RequestParam("clientId")String clientId,
            @RequestParam("secret")String secret);
}
