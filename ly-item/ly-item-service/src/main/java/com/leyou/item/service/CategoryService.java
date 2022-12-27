package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<CategoryDTO> listCategoryByPid(Long pid);

    CategoryDTO queryCategoryById(Long id);

    List<CategoryDTO> listCategoryByIds(List<Long> ids);

    List<CategoryDTO> queryCategoryByBrand(Long bid);
}
