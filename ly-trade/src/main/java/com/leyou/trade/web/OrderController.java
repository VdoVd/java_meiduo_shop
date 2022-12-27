package com.leyou.trade.web;

import com.leyou.trade.entity.Order;
import com.leyou.trade.service.OrderService;
import com.leyou.trade.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 下单
     *
     * @param orderVO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(
            @RequestBody OrderVO orderVO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.createOrder(orderVO));
    }

    /**
     * 根据id查询对应的订单信息
     * @param orderId
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id")Long orderId){

        return ResponseEntity.ok(this.orderService.getById(orderId));
    }


    /**
     * 获取订单状态
     * @param id
     * @return
     */
    @GetMapping("/status/{id}")
    public ResponseEntity<Integer>  queryOrderStatusById(@PathVariable("id")Long id){

        return ResponseEntity.ok(this.orderService.getById(id).getStatus());
    }
}
