package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecParam;

import java.util.List;


public interface SpecParamService extends IService<SpecParam> {
    List<SpecParamDTO> listParam(Long groupId, Long categoryId,Boolean searching);
}