package com.leyou.search.service;

import com.leyou.item.dto.SpuDTO;
import com.leyou.search.pojo.Goods;

public interface IndexService {

    void loadData();

    Goods buildGoods(SpuDTO spuDTO);

    void deleteById(Long id);

    void createIndexById(Long id);
}
