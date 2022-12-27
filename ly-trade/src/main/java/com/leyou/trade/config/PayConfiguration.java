package com.leyou.trade.config;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfigImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayConfiguration {

    @Bean
    //属性读取，读取到后根据属性名自动反射调用对应对象（wxPayConfigImpl）的set方法
    @ConfigurationProperties(prefix = "ly.pay.wx")
    public WXPayConfigImpl payConfig(){
        return new WXPayConfigImpl();
    }


    /**
     * 注册WXPay对象
     * @param payConfig 支付相关配置
     * @return WXPay对象
     * @throws Exception 连结WX失败时用到
     */
    @Bean
    public WXPay wxPay(WXPayConfigImpl payConfig) throws Exception {
        return new WXPay(payConfig, payConfig.getNotifyUrl());
    }
}