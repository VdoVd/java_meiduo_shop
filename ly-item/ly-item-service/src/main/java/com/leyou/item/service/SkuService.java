package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.entity.Sku;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


public interface SkuService extends IService<Sku> {
    void minusStock(Map<Long, Integer> cartsMap);

    void plusStock(Map<Long, Integer> detailsMap);
}
