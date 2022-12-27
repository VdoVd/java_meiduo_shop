package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.Sku;
import org.apache.ibatis.annotations.Update;

import java.util.Map;


public interface SkuMapper extends BaseMapper<Sku> {

    @Update("update tb_sku set stock = stock - #{num},sold = sold + #{num} where id = #{skuId}")
    Integer minusStock(Map<String,Object> param);


    @Update("update tb_sku set stock = stock + #{num},sold = sold - #{num} where id = #{skuId}")
    Integer plusStock(Map<String,Object> param);
}