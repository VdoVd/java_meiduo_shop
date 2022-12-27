package com.leyou.trade.web;

import com.leyou.trade.dto.ResultDTO;
import com.leyou.trade.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayService payService;

    /**
     * 根据订单id生成订单对应的付款链接
     */
    @GetMapping("/url/{id}")
    public ResponseEntity<String> generatePayUrl(@PathVariable("id")Long id){

        return ResponseEntity.ok(this.payService.generatePayUrl(id));
    }


    /**
     * 微信回调
     * @param paramMap
     * 默认接收参数为json，需要把参数从其他类型转换为json
     * produces 默认返回json，声明返回xml，
     * @return
     */
    @PostMapping(value = "/wx/notify",produces = "application/xml")
    public ResponseEntity<ResultDTO> handleNotify(@RequestBody Map<String,String> paramMap){

        this.payService.handleNotify(paramMap);

        return ResponseEntity.ok(new ResultDTO());
    }
}
