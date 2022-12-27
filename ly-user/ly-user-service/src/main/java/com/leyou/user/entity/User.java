package com.leyou.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leyou.common.constants.RegexPatterns;
import com.leyou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_user")
public class User extends BaseEntity {
    @TableId
    private Long id;

    @NotNull(message = "用户名不能为空")
    @Length(min = 4,max = 30,message = "用户名长度限定为4-30")
    private String username;

    @NotNull(message = "密码不能为空")
    @Length(min = 4,max = 30,message = "密码长度限定为4-30")
    private String password;

    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = RegexPatterns.PHONE_REGEX,message = "手机号格式不匹配")
    private String phone;
}