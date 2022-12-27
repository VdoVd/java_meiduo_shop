package com.leyou.user.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leyou.common.dto.BaseDTO;
import com.leyou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class AddressDTO extends BaseDTO {


    /**
     * 收件人
     */
    private String addressee;


    /**
     * 区
     */
    private String district;


    /**
     * 地址自己的id
     */
    private Long id;


    /**
     * 是否为默认值
     */
    private Boolean isDefault;


    /**
     * 手机号
     */
    private String phone;


    /**
     * 邮编
     */
    private String postcode;

    /**
     * 省
     */
    private String province;


    /**
     * 街道
     */
    private String street;


    /**
     * 市
     */
    private String city;


    /**
     * 地址对应的用户的id
     */
    private Long userId;


}