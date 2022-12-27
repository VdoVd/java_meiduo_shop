package com.leyou.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.auth.constants.JwtConstants;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.common.exception.LyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.leyou.auth.constants.JwtConstants.KEY_PREFIX;


public class JwtUtils {

    /**
     * JWT解析器
     */
    private final JwtParser jwtParser;
    /**
     * 秘钥
     */
    private final SecretKey secretKey;

    private StringRedisTemplate redisTemplate;

    private final static ObjectMapper mapper = new ObjectMapper();

    public JwtUtils(String key, StringRedisTemplate redisTemplate) {
        // 生成秘钥
        secretKey = Keys.hmacShaKeyFor(key.getBytes(Charset.forName("UTF-8")));
        // JWT解析器

        //加密时如果传入的key是heima，解密时，生成对象jwtParser使用的key必须也是heima
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();

        this.redisTemplate = redisTemplate;
    }


    /**
     * 生成jwt(token)，自己指定的JTI
     *
     * @param userDetails 用户信息
     * @return JWT
     */
    public String createJwt(UserDetail userDetails) {

        String jti = createJti();
        try {
            // 生成token
            String token = Jwts.builder().signWith(secretKey)
                    .setId(jti)
                    .claim("user", mapper.writeValueAsString(userDetails))
                    .compact();
            //登录，生成token要把当前token的jti存入redis，存30min（连续登录，退出登录，10次，redis中存几个jti？）
            this.redisTemplate.opsForValue().set(KEY_PREFIX + userDetails.getId(), jti, 30, TimeUnit.MINUTES);
            return token;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析jwt，并将用户信息转为指定的Clazz类型
     *
     * @param jwt token
     * @return 载荷，包含JTI和用户信息
     */
    public Payload parseJwt(String jwt) {
        try {
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(jwt);
            Claims claims = claimsJws.getBody();

            Payload payload = new Payload();
            //token的唯一id
            payload.setJti(claims.getId());
            //token】中实际存放的用户信息
            payload.setUserDetail(mapper.readValue(claims.get("user", String.class), UserDetail.class));

            String key = KEY_PREFIX + payload.getUserDetail().getId();

            //redis有key，并且token解析的jti和redis存储的jti一致
            if (this.redisTemplate.hasKey(key) && claims.getId().equals(redisTemplate.opsForValue().get(key))) {
                return payload;
            }
            throw new LyException(401, "用户未登录，或token非法");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createJti() {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }
}