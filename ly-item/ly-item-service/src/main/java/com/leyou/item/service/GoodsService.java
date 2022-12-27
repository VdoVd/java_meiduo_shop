package com.leyou.item.service;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;

import java.util.List;
import java.util.Map;

public interface GoodsService {
    PageDTO<SpuDTO> spuPageQuery(Integer page, Integer rows, Long cid, Long bid, Boolean saleable, Long id);

    void addGoods(SpuDTO spuDTO);

    void modifySaleable(Long id, Boolean saleable);

    SpuDTO queryGoodsById(Long id);

    void updateGoods(SpuDTO spuDTO);

    List<SkuDTO> listSkuByIds(List<Long> ids);

    List<SkuDTO> listSkuBySpu(Long id);

    SpuDetailDTO querySpuDetailById(Long spuId);

    SpuDTO querySpuById(Long id);

    List<SpecParamDTO> querySpecParamValue(Long id, Boolean searching);

    void minusStock(Map<Long, Integer> cartsMap);

    void plusStock(Map<Long, Integer> detailsMap);
}
