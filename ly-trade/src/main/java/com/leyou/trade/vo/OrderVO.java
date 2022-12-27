package com.leyou.trade.vo;

import lombok.Data;

import java.util.Map;

@Data
public class OrderVO {
    private Long addressId;
    private Integer paymentType;
    private Map<Long,Integer> carts;
}
