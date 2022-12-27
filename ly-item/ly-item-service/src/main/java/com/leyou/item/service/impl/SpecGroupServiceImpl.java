package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SpecGroupServiceImpl extends ServiceImpl<SpecGroupMapper, SpecGroup> implements SpecGroupService {

    @Autowired
    private SpecParamService paramService;

    @Override
    public List<SpecGroupDTO> listGroupByCategory(Long cid) {

        //select * from tb_spec_group where category_id = #{cid}
        return SpecGroupDTO.convertEntityList(this.query().eq("category_id", cid).list());
    }

    @Override
    public List<SpecGroupDTO> querySpecByCategory(Long cid) {

        List<SpecGroupDTO> specGroupDTOS = this.listGroupByCategory(cid);

        //specGroupDTOS.forEach(specGroupDTO -> specGroupDTO.setParams(this.paramService.listParam(specGroupDTO.getId(),cid)));


        //根据分类id一次性查询当前分类对应的所有的规格参数集合信息
        List<SpecParamDTO> specParamDTOS = this.paramService.listParam(null, cid, null);

        //map的key为groupId，value为同key的param的集合
        Map<Long, List<SpecParamDTO>> specParamMap = specParamDTOS.stream().collect(Collectors.groupingBy(specParamDTO -> specParamDTO.getGroupId()));


        //把规格参数填充到specGroup中，根据group的id进行辨识
        specGroupDTOS.forEach(specGroupDTO -> specGroupDTO.setParams(specParamMap.get(specGroupDTO.getId())));

        return specGroupDTOS;
    }
}