package com.leyou.item.web;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecGroupService groupService;

    @Autowired
    private SpecParamService paramService;


    /**
     * 根据分类id查询对应的规格参数组集合
     * @param cid
     * @return
     */
    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> listGroupByCategory(@RequestParam("id")Long cid){

        return ResponseEntity.ok(this.groupService.listGroupByCategory(cid));
    }

    /**
     * 根据条件查询规格参数，
     * @param groupId 规格参数组的id
     * @param categoryId 分类id
     * @param searching 是否可用于搜索过滤
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> listParam(
            @RequestParam(value = "groupId",required = false) Long groupId,
            @RequestParam(value = "categoryId",required = false) Long categoryId,
            @RequestParam(value = "searching",required = false) Boolean searching){


        return ResponseEntity.ok(this.paramService.listParam(groupId,categoryId,searching));
    }

    /**
     * 根据分类id查询对应的规格参数组以及组内参数
     * @param cid
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<SpecGroupDTO>> querySpecByCategory(
            @RequestParam("id")Long cid){

        return ResponseEntity.ok(this.groupService.querySpecByCategory(cid));
    }
}
