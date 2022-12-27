package com.leyou.trade.web;

import com.leyou.trade.entity.CartItem;
import com.leyou.trade.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    /**
     * 加入购物车
     * @param cartItem
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(
            @RequestBody CartItem cartItem){

        this.cartService.addCart(cartItem);

        return ResponseEntity.ok().build();
    }


    /**
     * 合并购物车（批量加入）
     * @param cartItems
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<Void> addCarts(
            @RequestBody List<CartItem> cartItems){

        this.cartService.addCarts(cartItems);

        return ResponseEntity.ok().build();
    }

    /**
     * 查看购物车内容
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<CartItem>> listCartItems(){
        return ResponseEntity.ok(this.cartService.listCartItems());
    }
}
