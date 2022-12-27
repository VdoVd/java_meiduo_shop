package com.leyou.user.client;

import com.leyou.user.dto.AddressDTO;
import com.leyou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserClient {
    /**
     * 根据用户名密码查询用户信息
     *
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/info")
    UserDTO queryUserByNameAndPass(
            @RequestParam("username") String username,
            @RequestParam("password") String password);


    /**
     * 根据用户id以及地址id查询用户地址
     *
     * @param userId
     * @param id
     * @return
     */
    @GetMapping("/address")
    AddressDTO queryUserAddress(
            @RequestParam("userId") Long userId,
            @RequestParam("id") Long id);
}
