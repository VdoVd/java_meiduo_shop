package com.leyou.sms.listener;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.leyou.common.constants.MQConstants.ExchangeConstants.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.QueueConstants.SMS_VERIFY_CODE_QUEUE;
import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.VERIFY_CODE_KEY;

@Component
@Slf4j
public class MsgListener {

    //队列和交换机的绑定关系
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = SMS_VERIFY_CODE_QUEUE),
            exchange = @Exchange(value = SMS_EXCHANGE_NAME,type = "topic"),
            key = VERIFY_CODE_KEY
    ))
    public void sendVerifyCode(Map<String,String> msg){

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4GDjw6DXzgk7LJE3uYCy", "yjpP76u2FmF3goCrpodiEY5xGPoAmS");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", msg.get("phone"));
        request.putQueryParameter("SignName", "乐优商城");
        request.putQueryParameter("TemplateCode", "SMS_143719983");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+msg.get("code")+"\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }
}
