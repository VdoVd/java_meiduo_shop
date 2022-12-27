package com.leyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetail;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.MQConstants.ExchangeConstants.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.VERIFY_CODE_KEY;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Boolean checkData(String data, Integer type) {

        //type，取值只能是1或2，data一定不能为空，当type为2，data表示手机号，必须符合格式要求
        if ((type != 1 && type != 2) || StringUtils.isEmpty(data) || (type == 2 && !RegexUtils.isPhone(data))) {
            throw new LyException(400, "请求参数错误");
        }

        String colName = null;
        switch (type) {
            case 1:
                colName = "username";
                break;
            case 2:
                colName = "phone";
                break;
        }

        //根据指定的列统计展示，如果有对应的列信息，查到一行==1返回true表示存在
        return this.query().eq(colName, data).count() == 1;
    }

    @Autowired
    private AmqpTemplate amqpTemplate;

    private final String KEY_PREFIX = "ly:sms:phone:";

    @Override
    public void sendVerifyCode(String phone) {

        if (!RegexUtils.isPhone(phone)) {
            throw new LyException(400, "手机号格式错误");
        }

        //1，手机号，2，验证码
        Map<String, String> msg = new HashMap<>();

        String code = RandomStringUtils.randomNumeric(5);

        msg.put("phone", phone);
        msg.put("code", code);

        this.amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME, VERIFY_CODE_KEY, msg);

        //存入，redis，存5min
        this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
    }


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createUser(User user, String code) {
        if (StringUtils.isEmpty(code)) {
            throw new LyException(400, "验证码不能为空");
        }

        String key = KEY_PREFIX + user.getPhone();

        //key不存在或者验证码不匹配
        if (!redisTemplate.hasKey(key) || !code.equals(redisTemplate.opsForValue().get(key))) {
            throw new LyException(400, "验证码失效");
        }

        //对密码进行加密，md5加盐加密，盐值在算法中，每一次的盐值都不相同
        user.setPassword(passwordEncoder.encode(user.getPassword()));


        //先根据密文推算出盐值，然后明文+盐值，加密，再次比较新密文和密文
        //passwordEncoder.matches("明文","密文");


        this.save(user);
    }

    @Override
    public UserDTO queryUserByNameAndPass(String username, String password) {
        User user = this.query()
                .eq("username", username).one();

        if (null == user) {
            throw new LyException(400, "用户名不存在");
        }

        //验密
        if (!passwordEncoder.matches(password,user.getPassword())){
            throw new LyException(400, "密码输入错误");
        }

        return new UserDTO(user);
    }

}
