package com.leyou.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.trade.entity.OrderDetail;
import com.leyou.trade.mapper.OrderDetailMapper;
import com.leyou.trade.service.OrderDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

    @Override
    @Transactional
    public void batchSaveOrderDetail(List<OrderDetail> orderDetails) {
        this.saveBatch(orderDetails);
    }
}
