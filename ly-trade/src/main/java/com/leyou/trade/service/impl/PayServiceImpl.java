package com.leyou.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyou.common.exception.LyException;
import com.leyou.trade.entity.Order;
import com.leyou.trade.service.OrderService;
import com.leyou.trade.service.PayService;
import com.leyou.trade.utils.PayHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {


    @Autowired
    private OrderService orderService;

    @Autowired
    private PayHelper payHelper;

    @Override
    public String generatePayUrl(Long id) {

        Order order = this.orderService.getById(id);

        //查询当前订单对应的订单，详情，显示第一个商品即可

        //三个参数，订单编号，总价，所购买商品描述
        return this.payHelper.getPayUrl(id,/*order.getActualFee()*/1L, "乐优商城商品");
    }

    @Override
    @Transactional
    public void handleNotify(Map<String, String> paramMap) {

        //响应数据校验，1，数据签名，2，验证统一状态，以及业务状态SUCCESS
        this.payHelper.checkResponseSignature(paramMap);
        this.payHelper.checkReturnCode(paramMap);
        this.payHelper.checkResultCode(paramMap);


        String out_trade_no = paramMap.get("out_trade_no");
        String total_fee = paramMap.get("total_fee");

        if (null == out_trade_no || null == total_fee) {
            throw new LyException(400, "支付通知有误，已报警");
        }

        Long orderId = Long.valueOf(out_trade_no);
        Long totalFee = Long.valueOf(total_fee);

        Order order = this.orderService.getById(orderId);

        if (totalFee.longValue() != /*order.getActualFee().longValue()*/1L) {
            throw new LyException(400, "支付通知有误，已报警");
        }


        //TODO 校验通过了，要做的时，改变订单的支付状态

        order.setStatus(2);
        //设置支付时间
        order.setPayTime(new Date());

        //加入乐观锁校验，update tb_order set status = 2 where order_id = #{orderId} and status = #{status}
        this.orderService.update(order,new QueryWrapper<Order>().eq("status",1).eq("order_id",order.getOrderId()));
    }
}
