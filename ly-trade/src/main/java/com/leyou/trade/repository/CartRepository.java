package com.leyou.trade.repository;

import com.leyou.trade.entity.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface CartRepository extends MongoRepository<CartItem,String> {
    List<CartItem> findByUserId(Long userId);
}
