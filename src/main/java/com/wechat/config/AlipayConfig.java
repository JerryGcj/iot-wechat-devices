package com.wechat.config;

import com.alipay.api.AlipayApiException;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {

    /**
     * 应用appid
     */
    @Value("${alipay.appId}")
    private String appId;
    /**
     * 应用私钥
     */
    @Value("${alipay.privateKey}")
    private String privateKey;
    /**
     * 支付宝公钥
     */
    @Value("${alipay.publicKey}")
    private String publicKey;
    /**
     * 应用公钥证书路径
     */
    @Value("${alipay.appCertPath}")
    private String appCertPath;
    /**
     * 支付宝公钥证书路径
     */
    @Value("${alipay.alipayCertPath}")
    private String alipayCertPath;
    /**
     * 支付宝根证书路径
     */
    @Value("${alipay.alipayRootCertPath}")
    private String alipayRootCertPath;
    /**
     * 支付宝支付网关
     */
    @Value("${alipay.serverUrl}")
    private String serverUrl;


    @Bean
    public void config() throws AlipayApiException {
        AliPayApiConfig aliPayApiConfig;
        try {
            aliPayApiConfig = AliPayApiConfigKit.getApiConfig(appId);
        } catch (Exception e) {
            aliPayApiConfig = AliPayApiConfig.builder()
                    .setAppId(appId)
                    .setAliPayPublicKey(publicKey)
                    .setAppCertPath(appCertPath)
                    .setAliPayCertPath(alipayCertPath)
                    .setAliPayRootCertPath(alipayRootCertPath)
                    .setCharset("UTF-8")
                    .setPrivateKey(privateKey)
                    .setServiceUrl(serverUrl)
                    .setSignType("RSA2")
                    // 普通公钥方式
                    //.build();
                    // 证书模式
                    .buildByCert();

        }
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
    }
}
