package com.leyou.item.web;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.service.GoodsService;
import com.leyou.item.service.SkuService;
import com.leyou.item.service.SpuDetailService;
import com.leyou.item.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;


    /**
     * spu的分页查询
     * @param page
     * @param rows
     * @param cid
     * @param bid
     * @param saleable
     * @param id
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageDTO<SpuDTO>> spuPageQuery(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "categoryId", required = false) Long cid,
            @RequestParam(value = "brandId", required =false) Long bid,
            @RequestParam(value = "saleable", required = false)Boolean saleable,
            @RequestParam(value = "id", required = false)Long id) {

        return ResponseEntity.ok(this.goodsService.spuPageQuery(page, rows,cid,bid,saleable,id));
    }

    /**
     * 商品新增
     * @param spuDTO
     * @return
     */
    @PostMapping("/spu")
    public ResponseEntity<Void> addGoods(
            @RequestBody SpuDTO spuDTO){

        this.goodsService.addGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据spuId修改spu以及sku的上下架状态
     * @param id
     * @param saleable
     * @return
     */
    @PutMapping("/saleable")
    public ResponseEntity<Void> modifySaleable(
            @RequestParam("id")Long id,
            @RequestParam("saleable")Boolean saleable){

        this.goodsService.modifySaleable(id,saleable);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据商品id查询spu，sku，spuDetail
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpuDTO> queryGoodsById(@PathVariable("id")Long id){

        return ResponseEntity.ok(this.goodsService.queryGoodsById(id));
    }

    /**
     * 商品修改
     * @return
     */
    @PutMapping("/spu")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){

        this.goodsService.updateGoods(spuDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据sku的id集合查询对应的sku集合
     * @param ids
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<SkuDTO>> listSkuByIds(@RequestParam("ids")List<Long> ids){

        return ResponseEntity.ok(this.goodsService.listSkuByIds(ids));
    }


    /**
     * 根据spuId查询对应的sku集合
     * @param id
     * @return
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<SkuDTO>> listSkuBySpu(
            @RequestParam("id")Long id){

        return ResponseEntity.ok(this.goodsService.listSkuBySpu(id));
    }

    /**
     * 根据spuId查询对应的spuDetail信息
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> querySpuDetailById(@RequestParam("id")Long spuId){

        return ResponseEntity.ok(this.goodsService.querySpuDetailById(spuId));
    }


    /**
     * 根据spuId查询对应的spu信息
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> querySpuById(
            @PathVariable("id")Long id){

        return ResponseEntity.ok(this.goodsService.querySpuById(id));
    }


    /**
     * 根据商品id查询当前商品对应的规格参数[可搜索]，并且要查询到其对应的值
     * @param id
     * @param searching
     * @return
     */
    @GetMapping("/spec/value")
    public ResponseEntity<List<SpecParamDTO>> querySpecParamValue(
            @RequestParam("id")Long id,
            @RequestParam(value = "searching",required = false)Boolean searching){

        return ResponseEntity.ok(this.goodsService.querySpecParamValue(id,searching));
    }


    /**
     * 扣减商品库存
     * @param cartsMap
     * @return
     */
    @PutMapping("/sku/minus/stock")
    public ResponseEntity<Void> minusStock(@RequestBody Map<Long,Integer> cartsMap){

        this.goodsService.minusStock(cartsMap);
        return ResponseEntity.ok().build();
    }


    /**
     * 加商品库存
     * @param detailsMap
     * @return
     */
    @PutMapping("/sku/plus/stock")
    public ResponseEntity<Void> plusStock(@RequestBody Map<Long,Integer> detailsMap){

        this.goodsService.plusStock(detailsMap);
        return ResponseEntity.ok().build();
    }
}
