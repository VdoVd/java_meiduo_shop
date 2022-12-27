package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;

import java.util.List;

public interface BrandService extends IService<Brand> {
    PageDTO<BrandDTO> pageQuery(Integer page, Integer rows, String key);

    void addBrand(BrandDTO brandDTO);

    void updateBrand(BrandDTO brandDTO);

    BrandDTO queryBrandById(Long id);

    List<BrandDTO> listBrandByIds(List<Long> ids);

    List<BrandDTO> listBrandByCategory(Long cid);
}
