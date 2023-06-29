package com.wechat.wxpay;


import com.ijpay.alipay.AliPayApi;
import com.wechat.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
class WxpayApplicationTests {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisTemplate redisTemplate;
    /** 商户号 */
    public static String merchantId = "1613718558";
    /** 商户API私钥路径 */
    public static String privateKeyPath = "/Users/xiaojian/work/apiclient_key.pem";
    /** 商户证书序列号 */
    public static String merchantSerialNumber = "5E4197B10177EAB2724AEDA75AFD06A1B3D8032A";
    /** 商户APIV3密钥 */
    public static String apiV3key = "O8GNjioSp8GiCJ0xRwmCglydaIeIh5K2";
    @Test
    void contextLoads() {
        Set<String> keys = redisTemplate.keys("*operator");
        System.out.println("zongshu："+keys.size());
        redisTemplate.delete(keys);
    }


    @Test
    void test() {
    }

    public static void main(String[] args) {

    }
}
