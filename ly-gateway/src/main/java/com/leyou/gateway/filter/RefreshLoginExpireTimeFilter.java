package com.leyou.gateway.filter;

import com.leyou.auth.constants.JwtConstants;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import static com.leyou.auth.constants.JwtConstants.KEY_PREFIX;


/**
 * 只刷新登录状态时对应的过期时间，如果没有登录，或者token有问题，直接放行，即便登录了，刷新完时间也要放行
 * 放行的本质就是说本过滤器执行完成了，执行下一个
 */
@Component
@Order
public class RefreshLoginExpireTimeFilter implements GlobalFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        //gateway中特有的方式，MultiValueMap，
        HttpCookie cookie  = request.getCookies().toSingleValueMap().get(JwtConstants.COOKIE_NAME);

        //没有登录cookie情况
        if (cookie == null) {
            //放行
            return chain.filter(exchange);
        }

        //登录之后cookie中的token有误，登录失效了，redis中的信息，不存在了

        //如果用户已经登录，则重置redis对应的过期时间，
        //如果没有登录，直接放行

        try {
            String token = cookie.getValue();

            Payload payload = jwtUtils.parseJwt(token);

            //能解析说明前后台一致，直接重置redis的时间
            String key = KEY_PREFIX+payload.getUserDetail().getId();

            //重置redis中的key的过期时间为30min
            this.redisTemplate.expire(key,30, TimeUnit.MINUTES);

            return chain.filter(exchange);
        } catch (Exception e) {
            return chain.filter(exchange);
        }

    }


}
