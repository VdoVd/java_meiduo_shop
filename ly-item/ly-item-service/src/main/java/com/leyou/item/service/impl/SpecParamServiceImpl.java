package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.service.SpecParamService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SpecParamServiceImpl extends ServiceImpl<SpecParamMapper, SpecParam> implements SpecParamService {
    @Override
    public List<SpecParamDTO> listParam(Long groupId, Long categoryId,Boolean searching) {

        //select * from tb_spec_param [ where group_id = #{group_id} and category_id = #{category_id} and searching = #{searching}]
        List<SpecParam> specParams = this.query()
                .eq(null!=groupId,"group_id", groupId)
                .eq(null!=categoryId,"category_id",categoryId)
                .eq(null!=searching,"searching",searching)
                .list();
        return SpecParamDTO.convertEntityList(specParams);
    }
}