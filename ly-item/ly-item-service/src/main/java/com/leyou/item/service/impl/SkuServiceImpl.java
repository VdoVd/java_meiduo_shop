package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.entity.Sku;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.service.SkuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {

    @Override
    @Transactional
    public void minusStock(Map<Long, Integer> cartsMap) {
        executeBatch(sqlSession -> {
            //循环想sqlSession中添加statement，myBatis，map传参
            cartsMap.entrySet().forEach(cart->{

                Map<String,Object> paramMap = new HashMap<>();
                paramMap.put("skuId",cart.getKey());
                paramMap.put("num",cart.getValue());

                sqlSession.update("com.leyou.item.mapper.SkuMapper.minusStock",paramMap);

            });

            //一次性刷入所有的statement达到批量执行的目的
            sqlSession.flushStatements();

        });

    }

    @Override
    @Transactional
    public void plusStock(Map<Long, Integer> detailsMap) {
        executeBatch(sqlSession -> {
            //循环想sqlSession中添加statement，myBatis，map传参
            detailsMap.entrySet().forEach(cart->{

                Map<String,Object> paramMap = new HashMap<>();
                paramMap.put("skuId",cart.getKey());
                paramMap.put("num",cart.getValue());

                sqlSession.update("com.leyou.item.mapper.SkuMapper.plusStock",paramMap);

            });

            //一次性刷入所有的statement达到批量执行的目的
            sqlSession.flushStatements();

        });
    }
}
