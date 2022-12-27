package com.leyou.trade.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfigImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class PayHelper {

    private final WXPay wxPay;
    private final WXPayConfigImpl payConfig;

    public PayHelper(WXPay wxPay, WXPayConfigImpl payConfig) {
        this.wxPay = wxPay;
        this.payConfig = payConfig;
    }

    public String getPayUrl(Long orderId, Long totalFee, String desc){
        // 1.准备请求参数：
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", desc);
        data.put("out_trade_no", orderId.toString());
        data.put("total_fee", totalFee.toString());
        data.put("spbill_create_ip", payConfig.getSpbillCreateIp());
        data.put("trade_type", payConfig.getTradeType());  // 此处指定为扫码支付

        try {
            // 2.下单
            Map<String, String> resp = wxPay.unifiedOrder(data);

            // 3.通信校验
            checkReturnCode(resp);
            // 4.业务校验
            checkResultCode(resp);
            // 5.签名校验
            checkResponseSignature(resp);

            // 6.获取支付链接
            String url = resp.get("code_url");
            if(StringUtils.isBlank(url)){
                // url为空
                throw new RuntimeException("支付链接为空！");
            }
            return url;
        } catch (Exception e) {
            log.error("微信支付统一下单失败，原因：", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void checkResponseSignature(Map<String, String> resp){
        try {
            boolean isValid = wxPay.isResponseSignatureValid(resp);
            if(!isValid){
                // 签名无效
                throw new RuntimeException("签名错误！");
            }
        } catch (Exception e) {
            throw new RuntimeException("签名错误！", e);
        }
    }

    public void checkResultCode(Map<String, String> resp) {
        String resultCode = resp.get("result_code");
        if("FAIL".equals(resultCode)){
            // 失败
            throw new RuntimeException(resp.get("err_code_des"));
        }
    }

    public void checkReturnCode(Map<String, String> resp) {
        String returnCode = resp.get("return_code");
        if("FAIL".equals(returnCode)){
            // 失败
            throw new RuntimeException("通信失败！");
        }
    }
}
