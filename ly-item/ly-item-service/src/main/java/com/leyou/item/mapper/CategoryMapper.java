package com.leyou.item.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {
    @Select("select tc.* from tb_category  tc inner join tb_category_brand cb on tc.id = cb.category_id where cb.brand_id = #{bid};")
    List<Category> queryCategoryByBrand(@Param("bid") Long bid);
}
