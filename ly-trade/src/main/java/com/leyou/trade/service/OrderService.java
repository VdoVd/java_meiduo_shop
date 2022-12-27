package com.leyou.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.trade.entity.Order;
import com.leyou.trade.vo.OrderVO;

public interface OrderService extends IService<Order> {
    Long createOrder(OrderVO orderVO);

    void evictOrderIfNecessary(Long orderId);
}
