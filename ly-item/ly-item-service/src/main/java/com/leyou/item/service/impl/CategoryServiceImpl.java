package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.entity.CategoryBrand;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.service.CategoryBrandService;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryBrandService categoryBrandService;

    @Override
    public List<CategoryDTO> listCategoryByPid(Long pid) {

        //select * from tb_category where parent_id = #{pid}
        //entity===>dto
        return  CategoryDTO.convertEntityList(this.query().eq("parent_id", pid).list());
    }

    @Override
    public CategoryDTO queryCategoryById(Long id) {

        return new CategoryDTO(getById(id));
    }

    @Override
    public List<CategoryDTO> listCategoryByIds(List<Long> ids) {

        return CategoryDTO.convertEntityList(listByIds(ids));
    }

    @Override
    public List<CategoryDTO> queryCategoryByBrand(Long bid) {

        //根据品牌id查询对应的分类id的集合
        List<Long> categoryIds = this.categoryBrandService
                .query()
                .eq("brand_id", bid)
                .list()
                .stream()
                .map(CategoryBrand::getCategoryId)
                .collect(Collectors.toList());


        return this.listCategoryByIds(categoryIds);
    }
}
