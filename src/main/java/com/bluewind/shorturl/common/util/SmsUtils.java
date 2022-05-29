package com.bluewind.shorturl.common.util;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author liuxingyu01
 * @date 2022-05-29 18:12
 * @description
 **/
@Component
public class SmsUtils {
    final static Logger logger = LoggerFactory.getLogger(SmsUtils.class);

    /**
     * AccessKey ID
     */
    @Value("${sms.accessKeyId}")
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    @Value("${sms.accessKeySecret}")
    private String accessKeySecret;


    /**
     * 生成随机数（6位数字）
     * @return
     */
    public int intRandom() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }


    /**
     * 获取发送阿里云短信的Client
     * @return
     * @throws Exception
     * @return
     */
    public Client getSmsClient() throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }


    /**
     * 发送短信
     * @param phoneNumber 手机号
     * @throws Exception
     * @return
     */
    public String sendMessage(String phoneNumber) throws Exception {
        String randomNumber = String.valueOf(intRandom());
        Client client = getSmsClient();
        // 签名一定要合法，否则会发送失败
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumber) // 待发送的手机号
                .setSignName("JackLin的博客") // 短信标题，这个不能改，改了会报’错误信息：该账号下找不到对应签名‘
                .setTemplateCode("SMS_175405719") // 短信模板
                .setTemplateParam("{\"code\":\"" + randomNumber + "\"}"); // 随机码
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse sendResp = client.sendSms(sendSmsRequest);
        String code = sendResp.body.code;
        if (!com.aliyun.teautil.Common.equalString(code, "OK")) {
            logger.info("SmsUtils - sendMessage - 错误信息：{}", sendResp.body.message);
            return "";
        } else {
            return randomNumber;
        }
    }


}
