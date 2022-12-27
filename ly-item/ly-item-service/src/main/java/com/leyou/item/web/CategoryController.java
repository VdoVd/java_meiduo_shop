package com.leyou.item.web;

import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 根据父id查询对应的分类集合
     * @param pid
     * @return
     */
    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> listCategoryByPid(
            @RequestParam("pid")Long pid){

        return ResponseEntity.ok(this.categoryService.listCategoryByPid(pid));
    }


    /**
     * 根据id查询对应的分类对象
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> queryCategoryById(
            @PathVariable("id")Long id){

        return ResponseEntity.ok(this.categoryService.queryCategoryById(id));
    }


    /**
     * 根据商品分类id的集合查询对应的分类集合但是前端参数，字符串
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> listCategoryByIds(
            @RequestParam("ids")List<Long> ids){

        return ResponseEntity.ok(this.categoryService.listCategoryByIds(ids));
    }

    /**
     * 根据品牌id查询对应的分类集合
     * @param bid
     * @return
     */
    @GetMapping("/of/brand")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByBrand(
            @RequestParam("id")Long bid){

        return ResponseEntity.ok(this.categoryService.queryCategoryByBrand(bid));
    }
}
