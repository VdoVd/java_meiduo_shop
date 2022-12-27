package com.leyou.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class UserDetail {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
}