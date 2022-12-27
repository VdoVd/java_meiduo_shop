package com.leyou.auth.dto;

import lombok.Data;


@Data
public class Payload {
    //jti 就是token的唯一id标识
    private String jti;

    //实际存放的用户的信息
    private UserDetail userDetail;
}
