package com.leyou.trade.service.impl;

import com.leyou.auth.utils.UserContext;
import com.leyou.trade.entity.CartItem;
import com.leyou.trade.repository.CartRepository;
import com.leyou.trade.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public void addCart(CartItem cartItem) {

        String id = generateId(cartItem.getSkuId());

        Optional<CartItem> optional = this.cartRepository.findById(id);

        if (optional.isPresent()) {

            CartItem oldItem = optional.get();

            oldItem.setNum(cartItem.getNum() + oldItem.getNum());
            this.cartRepository.save(oldItem);
        } else {

            cartItem.setId(id);
            cartItem.setUserId(UserContext.getUser().getId());

            this.cartRepository.save(cartItem);
        }
    }

    @Override
    public List<CartItem> listCartItems() {

        //只能查询当前用户的
        return cartRepository.findByUserId(UserContext.getUser().getId());
    }

    @Override
    public String generateId(Long skuId) {

        //"u"+31+"s"+123456
        return String.format("u%ds%d", UserContext.getUser().getId(), skuId);
    }

    @Override
    public void addCarts(List<CartItem> cartItems) {
        cartItems.forEach(this::addCart);
    }
}
