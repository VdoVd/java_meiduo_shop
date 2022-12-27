package com.leyou.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.auth.entity.ClientInfo;

public interface ClientAuthService extends IService<ClientInfo> {
    String getKey(String clientId, String secret);
}
