package com.leyou.auth.web;

import com.leyou.auth.dto.AliOssSignatureDTO;
import com.leyou.auth.service.AliAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ali")
public class AliAuthController {

    @Autowired
    private AliAuthService aliAuthService;

    /**
     * 获取文件上传的必要参数信息
     * @return
     */
    @GetMapping("/oss/signature")
    public ResponseEntity<AliOssSignatureDTO> signature(){

        return ResponseEntity.ok(this.aliAuthService.signature());
    }
}
