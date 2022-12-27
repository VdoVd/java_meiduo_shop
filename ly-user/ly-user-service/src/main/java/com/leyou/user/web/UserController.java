package com.leyou.user.web;

import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.interceptors.LoginInterceptor;
import com.leyou.auth.utils.UserContext;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/info")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 检查用户输入的用户名或手机号是否唯一
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/exists/{data}/{type}")
    public ResponseEntity<Boolean> checkData(
            @PathVariable("data")String data,
            @PathVariable("type")Integer type){

        return ResponseEntity.ok(this.userService.checkData(data,type));
    }

    /**
     * 发送短信
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone")String phone){

        this.userService.sendVerifyCode(phone);
        return ResponseEntity.ok().build();
    }

    /**
     * 用户注册
     * @param user
     * @param code
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> createUser(
           @Valid User user,
            @RequestParam(value = "code")String code){

        this.userService.createUser(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户名密码查询用户信息
     * @param username
     * @param password
     * @return
     */
    @GetMapping
    public ResponseEntity<UserDTO> queryUserByNameAndPass(
            @RequestParam("username")String username,
            @RequestParam("password")String password){
        return ResponseEntity.ok(this.userService.queryUserByNameAndPass(username,password));
    }


    /**
     * 校验token，返回UserDetail
     * @return
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetail> showUser(){
        return ResponseEntity.ok(UserContext.getUser());
    }
}
