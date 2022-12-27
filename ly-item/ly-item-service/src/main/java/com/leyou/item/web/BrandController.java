package com.leyou.item.web;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;


    /**
     * 品牌的分页查询
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<PageDTO<BrandDTO>> pageQuery(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "key",required = false)String key){

        return ResponseEntity.ok(this.brandService.pageQuery(page,rows,key));
    }

    /**
     * 品牌新增
     * @param brandDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addBrand(BrandDTO brandDTO){

        this.brandService.addBrand(brandDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 品牌修改
     * @param brandDTO
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(BrandDTO brandDTO){

        this.brandService.updateBrand(brandDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据品牌id查询对应的品牌对象
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> queryBrandById(
            @PathVariable("id")Long id){
        return ResponseEntity.ok(this.brandService.queryBrandById(id));
    }

    /**
     * 根据id的集合查询对应的品牌集合
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> listBrandByIds(
            @RequestParam("ids")List<Long> ids){

        return ResponseEntity.ok(this.brandService.listBrandByIds(ids));
    }

    /**
     * 根据分类查询对应的品牌集合
     * @param cid
     * @return
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<BrandDTO>> listBrandByCategory(
            @RequestParam("id")Long cid){
        return ResponseEntity.ok(this.brandService.listBrandByCategory(cid));
    }
}
