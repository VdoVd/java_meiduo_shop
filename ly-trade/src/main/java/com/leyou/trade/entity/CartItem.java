package com.leyou.trade.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leyou.auth.utils.UserContext;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data

//SPEL,引用bean，并且调用bean的方法，动态获取到集合的名称
@Document(value = "#{@collectionName.getCollName()}")
public class CartItem {

    @Id
    @JsonIgnore
    private String id; //就是userId以及skuId的联合结果（联合主键，mongoDB不支持，format：u31s123456）

    private Long skuId;
    @JsonIgnore
    private Long userId;
    private String image;
    private Integer num;
    private Long price;
    private String spec;
    private String title;
}
