package com.leyou.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.trade.entity.OrderDetail;
import com.leyou.trade.entity.OrderLogistics;

import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {

    void batchSaveOrderDetail(List<OrderDetail> orderDetails);
}
