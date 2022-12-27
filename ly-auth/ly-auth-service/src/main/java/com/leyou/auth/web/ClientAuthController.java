package com.leyou.auth.web;

import com.leyou.auth.service.ClientAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientAuthController {

    @Autowired
    private ClientAuthService clientAuthService;


    /**
     * 根据clientId和secret的校验结果，获取jwtUtils专用key
     * @param clientId
     * @param secret
     * @return
     */
    @GetMapping("/key")
    public ResponseEntity<String> getKey(
            @RequestParam("clientId")String clientId,
            @RequestParam("secret")String secret){

        return ResponseEntity.ok(this.clientAuthService.getKey(clientId,secret));
    }
}
