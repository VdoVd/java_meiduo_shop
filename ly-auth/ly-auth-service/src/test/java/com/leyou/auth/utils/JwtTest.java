package com.leyou.auth.utils;

import com.leyou.LyAuthApplication;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.JwtUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void test() throws InterruptedException {
        // 生成jwt
        String jwt = jwtUtils.createJwt(UserDetail.of(10086L, "Heima"));
        System.out.println("jwt = " + jwt);

        jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMTYxMDBlMTZlZWQ0YjI5OGZjZGZkMDQwZGFlMTdhMiIsInVzZXIiOiJ7XCJpZFwiOjMxLFwidXNlcm5hbWVcIjpcImhlaW1hMTE2XCJ9In0.NfaIPNSInTlocHufzy4jXDOD27W04OOBXndb2ypQHGQ";

        // 解析jwt
        Payload payload = jwtUtils.parseJwt(jwt);
        System.out.println("payload = " + payload);
    }
}
