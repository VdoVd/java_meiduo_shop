package com.leyou.item.client;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("item-service")
public interface ItemClient {


    /**
     * spu的分页查询
     *
     * @param page
     * @param rows
     * @param cid
     * @param bid
     * @param saleable
     * @param id
     * @return
     */
    @GetMapping("/goods/spu/page")
    PageDTO<SpuDTO> spuPageQuery(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "categoryId", required = false) Long cid,
            @RequestParam(value = "brandId", required = false) Long bid,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "id", required = false) Long id);


    /**
     * 根据品牌id查询对应的品牌对象
     *
     * @param id
     * @return
     */
    @GetMapping("/brand/{id}")
    BrandDTO queryBrandById(@PathVariable("id") Long id);


    /**
     * 根据id查询对应的分类对象
     *
     * @param id
     * @return
     */
    @GetMapping("/category/{id}")
    CategoryDTO queryCategoryById(@PathVariable("id") Long id);


    /**
     * 根据spuId查询对应的sku集合
     *
     * @param id
     * @return
     */
    @GetMapping("/goods/sku/of/spu")
    List<SkuDTO> listSkuBySpu(@RequestParam("id") Long id);


    /**
     * 根据商品id以及是否可搜索，列出当前商品所有的可搜索规格参数以及其对应的值
     *
     * @param id
     * @param searching
     * @return
     */
    @GetMapping("/goods/spec/value")
    List<SpecParamDTO> querySpecParamValue(
            @RequestParam("id") Long id,
            @RequestParam(value = "searching", required = false) Boolean searching);


    /**
     * 根据id的集合查询对应的品牌集合
     *
     * @param ids
     * @return
     */
    @GetMapping("/brand/list")
    List<BrandDTO> listBrandByIds(@RequestParam("ids") List<Long> ids);


    /**
     * 根据商品分类id的集合查询对应的分类集合但是前端参数，字符串
     *
     * @param ids
     * @return
     */
    @GetMapping("/category/list")
    List<CategoryDTO> listCategoryByIds(@RequestParam("ids") List<Long> ids);


    /**
     * 根据spuId查询对应的spu信息
     *
     * @param id
     * @return
     */
    @GetMapping("/goods/spu/{id}")
    SpuDTO querySpuById(@PathVariable("id") Long id);

    /**
     * 根据spuId查询对应的spuDetail信息
     *
     * @param spuId
     * @return
     */
    @GetMapping("/goods/spu/detail")
    SpuDetailDTO querySpuDetailById(@RequestParam("id") Long spuId);


    /**
     * 根据分类id查询对应的规格参数组以及组内参数
     *
     * @param cid
     * @return
     */
    @GetMapping("/spec/list")
    List<SpecGroupDTO> querySpecByCategory(@RequestParam("id") Long cid);


    /**
     * 根据sku的id集合查询对应的sku集合
     *
     * @param ids
     * @return
     */
    @GetMapping("/goods/sku/list")
    List<SkuDTO> listSkuByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 扣减商品库存
     *
     * @param cartsMap
     * @return
     */
    @PutMapping("/goods/sku/minus/stock")
    Void minusStock(@RequestBody Map<Long, Integer> cartsMap);

    /**
     * 加商品库存
     *
     * @param detailsMap
     * @return
     */
    @PutMapping("/goods/sku/plus/stock")
    Void plusStock(@RequestBody Map<Long, Integer> detailsMap);


}
