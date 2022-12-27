package com.leyou.user.web;

import com.leyou.user.dto.AddressDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
public class AddressController {

    /**
     * 根据用户id以及地址id查询用户地址
     * @param userId
     * @param id
     * @return
     */
    @GetMapping
    public ResponseEntity<AddressDTO> queryUserAddress(
            @RequestParam("userId")Long userId,
            @RequestParam("id")Long id){

        AddressDTO address = new AddressDTO();
        address.setId(id);
        address.setUserId(userId);
        address.setStreet("航头镇航头路18号传智播客 3号楼");
        address.setCity("上海");
        address.setDistrict("浦东新区");
        address.setAddressee("社会我拓哥");
        address.setPhone("15800000000");
        address.setProvince("上海");
        address.setPostcode("210000");
        address.setIsDefault(true);
        return ResponseEntity.ok(address);
    }
}
