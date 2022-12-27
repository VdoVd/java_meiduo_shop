package com.leyou.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.auth.entity.ClientInfo;
import com.leyou.auth.mapper.ClientInfoMapper;
import com.leyou.auth.service.ClientAuthService;
import com.leyou.common.exception.LyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientAuthServiceImpl extends ServiceImpl<ClientInfoMapper, ClientInfo> implements ClientAuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${ly.jwt.key}")
    private String key;

    @Override
    public String getKey(String clientId, String secret) {

        ClientInfo clientInfo = this.query().eq("client_id", clientId).one();

        //如果传入的clientId有误，clientInfo查询为空，或者secret校验失败，都返回403
        if (null==clientInfo || !passwordEncoder.matches(secret,clientInfo.getSecret())){
            throw new LyException(403,"非法客户端请求");
        }


        //查数据库
        return key;
    }
}
