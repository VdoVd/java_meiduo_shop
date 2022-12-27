package com.leyou.auth.web;

import com.leyou.auth.service.UserAuthService;
import org.apache.http.cookie.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;


    /**
     * 处理用户的登录业务，返回cookie中存储token
     * @param username
     * @param password
     * @param response
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Void> userLogin(
            @RequestParam("username")String username,
            @RequestParam("password")String password,
            HttpServletResponse response){


        this.userAuthService.userLogin(username,password,response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,HttpServletResponse response){
        //1,删除redis中的用户token对应的jti
        //2，删除cookie，覆盖

        this.userAuthService.logout(request,response);
        return ResponseEntity.ok().build();
    }
}
