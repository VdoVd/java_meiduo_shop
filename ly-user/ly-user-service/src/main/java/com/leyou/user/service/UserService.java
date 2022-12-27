package com.leyou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.auth.dto.UserDetail;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;

public interface UserService extends IService<User> {
    Boolean checkData(String data, Integer type);

    void sendVerifyCode(String phone);

    void createUser(User user, String code);

    UserDTO queryUserByNameAndPass(String username, String password);

}
