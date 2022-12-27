package com.leyou.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserAuthService {
    void userLogin(String username, String password, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
