package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.entity.CategoryBrand;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.CategoryBrandMapper;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.service.BrandService;
import com.leyou.item.service.CategoryBrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Autowired
    private CategoryBrandService categoryBrandService;


    @Override
    public PageDTO<BrandDTO> pageQuery(Integer page, Integer rows, String key) {

        IPage<Brand> iPage = new Page<>(page,rows);


        //动态sql，只有condition为true，自定义查询才会生效
        Boolean condition = StringUtils.isNoneBlank(key);

        //自动进行数据回填，当前页数据，总元素个数，总页数
        page(iPage,new QueryWrapper<Brand>()
                .like(condition,"name",key)
                .or()
                .eq(condition,"letter",key)
        );

        //分页查询结果封装，封装到pageDTO中
        return new PageDTO<BrandDTO>(iPage.getTotal(),iPage.getPages(),BrandDTO.convertEntityList(iPage.getRecords()));
    }

    @Override
    @Transactional
    public void addBrand(BrandDTO brandDTO) {
        Brand brand = brandDTO.toEntity(Brand.class);

        this.save(brand);
        saveCategoryBrand(brandDTO.getCategoryIds(), brand.getId());

    }

    private void saveCategoryBrand(List<Long> cids, Long brandId) {
        //中间表
        //通过cid集合构造中间表对象集合
        List<CategoryBrand> categoryBrands = cids
                .stream()
                .map(cid -> CategoryBrand.of(cid, brandId))
                .collect(Collectors.toList());

        this.categoryBrandService.saveBatch(categoryBrands);
    }

    @Override
    @Transactional
    public void updateBrand(BrandDTO brandDTO) {
        Brand brand = brandDTO.toEntity(Brand.class);

        //品牌的修改
        this.updateById(brand);

        //中间表处理

        //清理之前的业务关系
        //delete from tb_category_brand where brand_id = #{brandId}
        this.categoryBrandService.remove(new QueryWrapper<CategoryBrand>().eq("brand_id",brand.getId()));

        //重建品牌以及分类的关系
        saveCategoryBrand(brandDTO.getCategoryIds(),brand.getId());
    }

    @Override
    public BrandDTO queryBrandById(Long id) {
        return new BrandDTO(this.getById(id));
    }

    @Override
    public List<BrandDTO> listBrandByIds(List<Long> ids) {
        return BrandDTO.convertEntityList(this.listByIds(ids));
    }

    @Override
    public List<BrandDTO> listBrandByCategory(Long cid) {

        //select * from tb_category_brand where category_id = #{cid}
        List<Long> brandIds = this.categoryBrandService
                .query()
                .eq("category_id", cid).list()
                .stream()
                .map(CategoryBrand::getBrandId)
                .collect(Collectors.toList());


        return this.listBrandByIds(brandIds);
    }
}
