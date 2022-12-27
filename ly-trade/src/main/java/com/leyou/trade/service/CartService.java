package com.leyou.trade.service;


import com.leyou.trade.entity.CartItem;

import java.util.List;

public interface CartService {
    void addCart(CartItem cartItem);

    List<CartItem> listCartItems();

    String generateId(Long skuId);


    void addCarts(List<CartItem> cartItems);
}
