package com.leyou.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.auth.utils.UserContext;
import com.leyou.common.exception.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SkuDTO;
import com.leyou.trade.entity.Order;
import com.leyou.trade.entity.OrderDetail;
import com.leyou.trade.entity.OrderLogistics;
import com.leyou.trade.mapper.OrderMapper;
import com.leyou.trade.service.OrderDetailService;
import com.leyou.trade.service.OrderLogisticsService;
import com.leyou.trade.service.OrderService;
import com.leyou.trade.vo.OrderVO;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.AddressDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.leyou.common.constants.MQConstants.ExchangeConstants.ORDER_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.EVICT_ORDER_KEY;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderDetailService detailService;

    @Autowired
    private OrderLogisticsService logisticsService;

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private AmqpTemplate amqpTemplate;


    /**
     * 下单，
     * 处理订单，
     * 处理订单详情
     * 处理收货信息
     *
     * @param orderVO
     */
    @Transactional
    @Override
    public Long createOrder(OrderVO orderVO) {


        //直接减,数据库增删改，锁，10人同时，iphone12,减到0为止。

        //map的key为sku的id，value就是当前sku对应的被购买的数量
        Map<Long, Integer> carts = orderVO.getCarts();

        if (CollectionUtils.isEmpty(carts) || orderVO.getAddressId() == null || orderVO.getPaymentType() == null) {
            throw new LyException(400, "用户输入信息有误");
        }


        //减库存,两个参数分别为skuId，以及要扣减的数量
        this.itemClient.minusStock(carts);


        //sku的集合
        List<SkuDTO> skuDTOS = this.itemClient.listSkuByIds(new ArrayList<>(carts.keySet()));


        //orderId会自动生成（mybatis-plus，使用idWorker）
        Order order = new Order();


        long total = 0;

        for (SkuDTO skuDTO : skuDTOS) {
            total += skuDTO.getPrice() * carts.get(skuDTO.getId());
        }

        //总价  sum(单价*数量)
        order.setTotalFee(total);
        order.setActualFee(total);
        order.setPaymentType(orderVO.getPaymentType());
        //全场包邮
        order.setPostFee(0L);

        order.setUserId(UserContext.getUser().getId());

        order.setStatus(1);

        //保存订单,并主键回显
        save(order);


        //根据sku的集合封装orderDetails
        List<OrderDetail> orderDetails = skuDTOS.stream().map(skuDTO -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(order.getOrderId());
            orderDetail.setSkuId(skuDTO.getId());
            orderDetail.setNum(carts.get(skuDTO.getId()));
            orderDetail.setTitle(skuDTO.getTitle());
            orderDetail.setSpec(skuDTO.getSpecialSpec());
            orderDetail.setPrice(skuDTO.getPrice());
            orderDetail.setImage(StringUtils.substringBefore(skuDTO.getImages(), ","));
            return orderDetail;
        }).collect(Collectors.toList());


        //批量保存orderDetails
        this.detailService.saveBatch(orderDetails);


        //根据用户id以及地址id查询对应的地址信息
        AddressDTO addressDTO = this.userClient.queryUserAddress(UserContext.getUser().getId(), orderVO.getAddressId());

        OrderLogistics orderLogistics = new OrderLogistics();

        BeanUtils.copyProperties(addressDTO, orderLogistics);

        //做主键id设置
        orderLogistics.setOrderId(order.getOrderId());

        //保存物流地址
        this.logisticsService.save(orderLogistics);


        //发送当前订单id去mq的【延迟队列】
        amqpTemplate.convertAndSend(ORDER_EXCHANGE_NAME, EVICT_ORDER_KEY, order.getOrderId());

        //throw new LyException(400,"故意捣乱");

        return order.getOrderId();
    }

    @Override
    @Transactional
    public void evictOrderIfNecessary(Long orderId) {
        Order order = this.getById(orderId);
        //只有此时订单状态还是1，才着手清理
        if (1 == order.getStatus()) {

            /**
             * 1,关闭订单，
             * 2，订单涉及的商品库存进行还原
             */

            //本订单所涉及到的所有的商品及数量信息，key为skuId，value为对应的数量
            Map<Long, Integer> detailMap = new HashMap<>();

            List<OrderDetail> orderDetails = this
                    .detailService
                    .query()
                    .eq("order_id", orderId)
                    .list();

            //从订单详情中获取商品内容封装
            orderDetails.forEach(orderDetail ->
                    detailMap.put(orderDetail.getSkuId(), orderDetail.getNum()));


            //加库存
            this.itemClient.plusStock(detailMap);

            order.setStatus(5);
            order.setCloseTime(new Date());

            this.updateById(order);
        }
    }
}
