package com.leyou.auth.service.impl;

import com.leyou.auth.constants.JwtConstants;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.service.UserAuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.UserDTO;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.leyou.auth.constants.JwtConstants.COOKIE_NAME;
import static com.leyou.auth.constants.JwtConstants.KEY_PREFIX;

@Service
@Slf4j
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void userLogin(String username, String password, HttpServletResponse response) {

        //请求user服务获取用户信息
        UserDTO userDTO = null;
        try {
            userDTO = this.userClient.queryUserByNameAndPass(username, password);
        } catch (FeignException e) {
            throw new LyException(e.status(), e.getMessage());
        }

        if (null == userDTO) {
            throw new LyException(401, "用户名或密码错误");
        }


        UserDetail userDetail = new UserDetail();

        userDetail.setId(userDTO.getId());
        userDetail.setUsername(userDTO.getUsername());

        //token
        String token = jwtUtils.createJwt(userDetail);

        Cookie cookie = new Cookie("LY_TOKEN", token);

        //cookie适配的域名，子域名也有效，item.leyou.com
        cookie.setDomain("leyou.com");

        //cookie的生效路径
        cookie.setPath("/");

        //拒绝脚本访问
        cookie.setHttpOnly(true);


        response.addCookie(cookie);

    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = CookieUtils.getCookieValue(request, COOKIE_NAME);
            Payload payload = jwtUtils.parseJwt(token);
            String key = KEY_PREFIX + payload.getUserDetail().getId();
            this.redisTemplate.delete(key);
            log.info("用户退出成功");
        } catch (Exception e) {
        }

        //重新生成cookie

        Cookie cookie = new Cookie(COOKIE_NAME,"");
        cookie.setDomain("leyou.com");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        //默认-1，浏览器关闭时删除，0，立即失效删除
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}
