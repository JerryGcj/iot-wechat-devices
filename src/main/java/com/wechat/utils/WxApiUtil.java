package com.wechat.utils;

import com.wechat.config.WXConstants;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号接口
 *
 * @author ywy
 * @date 2021/6/28 17:03
 */
@Slf4j
@Component
public class WxApiUtil {
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private static final String JSAPI_PAY_URL = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    String appPre = "appInfo:";

    /**
     * 根据code获取openid
     * @param code
     * @return
     */
    public String getOpenid(String code) {
        String openid = "";
        try {
            String param= "appid="+ WXConstants.APPID.getValue() +"&secret="+WXConstants.APPSECRET.getValue()+"&code="+code+"&grant_type=authorization_code";
            log.info("网页授权access_token请求参数：{}", param);
            String response = restTemplate.getForObject(ACCESS_TOKEN_URL + "?" + param, String.class);
            log.info("网页授权access_token响应结果：{}", response);
            JSONObject json = JSONObject.fromObject(response);
            openid = json.getString("openid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return openid;
    }


    /**
     * 根据code 和 state 获取openid
     * @param code
     * @return
     */
    public Map<String, String> getOpenidByCodeAndState(String code, String state) {
//        Map<String, Map<String, String>> gzhInfoMap = new HashMap<>();
//        //申小卡
//        Map<String, String> mapSXK = new HashMap<>();
//        mapSXK.put("appId", "wx39cfbf20f4aa3345");
//        mapSXK.put("appSecret", "c6c5281223634bd63d3fc5c37d40e2a1");
//        gzhInfoMap.put("wsfupt", mapSXK);
//        //咔咔好卡
//        Map<String, String> mapWSYYT = new HashMap<>();
//        mapWSYYT.put("appId", "wx1286277dcef4e989");
//        mapWSYYT.put("appSecret", "32ebec0ec01fe867492654b97c9c55b4");
//        gzhInfoMap.put("wsyyt", mapWSYYT);
//        //种草卡
//        Map<String, String> mapZCK = new HashMap<>();
//        mapZCK.put("appId", "wxad40e492a7702e04");
//        mapZCK.put("appSecret", "3c0baaa4ef5eb5ccb9b554cb0ba64f4b");
//        gzhInfoMap.put("zck", mapZCK);
//        //办张卡卡
//        Map<String, String> mapBZKK = new HashMap<>();
//        mapBZKK.put("appId", "wx15bc98c429e43e0a");
//        mapBZKK.put("appSecret", "bcce6a6858359118c96fc184fb1fc9a8");
//        gzhInfoMap.put("bzkk", mapBZKK);
//        //卡得乐
//        Map<String, String> mapKDL = new HashMap<>();
//        mapKDL.put("appId", "wxe61f0c358134623f");
//        mapKDL.put("appSecret", "889890ce498e22d3bbbe2a46c0c44127");
//        gzhInfoMap.put("kdl", mapKDL);
//        //大众臻品
//        Map<String, String> mapDZZP = new HashMap<>();
//        mapDZZP.put("appId", "wx6c330aa2e8f9ac4d");
//        mapDZZP.put("appSecret", "c346c7426bad5cdc6d4b2c21b1470d83");
//        gzhInfoMap.put("dzzp", mapDZZP);
//        //卡宝呀
//        Map<String, String> mapKBY = new HashMap<>();
//        mapKBY.put("appId", "wxd2a93520f430620d");
//        mapKBY.put("appSecret", "ff66f64b7c4512c4a7b36541c4684326");
//        gzhInfoMap.put("kby", mapKBY);

        Map entries = null;
        String openid = "";
        try {
            entries = redisTemplate.boundHashOps(appPre + state).entries();
            String param= "appid="+ entries.get("appId").toString() +"&secret="+ entries.get("appSecret").toString() +"&code="+code+"&grant_type=authorization_code";
            log.info("网页授权access_token请求参数：{}", param);
            String response = restTemplate.getForObject(ACCESS_TOKEN_URL + "?" + param, String.class);
            log.info("网页授权access_token响应结果：{}", response);
            JSONObject json = JSONObject.fromObject(response);
            openid = json.getString("openid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> mapp = new HashMap<>();
        mapp.put("openId", openid);
        mapp.put("appId", entries.get("appId").toString());
        return mapp;
    }


    /**
     * 根据code 和 state 获取用户信息  暂时弃用
     * @param code
     * @return
     */
    public Map getUserInfoByCodeAndState(String code, String state) {
        Map<String, Map<String, String>> gzhInfoMap = new HashMap<>();
        //申小卡
        Map<String, String> mapSXK = new HashMap<>();
        mapSXK.put("appId", "wx39cfbf20f4aa3345");
        mapSXK.put("appSecret", "c6c5281223634bd63d3fc5c37d40e2a1");
        gzhInfoMap.put("wsfupt", mapSXK);
        //咔咔好卡
        Map<String, String> mapWSYYT = new HashMap<>();
        mapWSYYT.put("appId", "wx1286277dcef4e989");
        mapWSYYT.put("appSecret", "32ebec0ec01fe867492654b97c9c55b4");
        gzhInfoMap.put("wsyyt", mapWSYYT);
        //种草卡
        Map<String, String> mapZCK = new HashMap<>();
        mapZCK.put("appId", "wxad40e492a7702e04");
        mapZCK.put("appSecret", "3c0baaa4ef5eb5ccb9b554cb0ba64f4b");
        gzhInfoMap.put("zck", mapZCK);
        //办张卡卡
        Map<String, String> mapBZKK = new HashMap<>();
        mapBZKK.put("appId", "wx15bc98c429e43e0a");
        mapBZKK.put("appSecret", "bcce6a6858359118c96fc184fb1fc9a8");
        gzhInfoMap.put("bzkk", mapBZKK);

        String openid = "";
        String access_token = "";
        String jb = null;
        try {
            String param= "appid="+ gzhInfoMap.get(state).get("appId") +"&secret="+gzhInfoMap.get(state).get("appSecret")+"&code="+code+"&grant_type=authorization_code";
            log.info("网页授权access_token请求参数：{}", param);
            String response = restTemplate.getForObject(ACCESS_TOKEN_URL + "?" + param, String.class);
            log.info("网页授权access_token响应结果：{}", response);
            JSONObject json = JSONObject.fromObject(response);
            openid = json.getString("openid");
            access_token = json.getString("access_token");

            String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+ access_token +"&openid="+ openid +"&lang=zh_CN";
            String response2 = restTemplate.getForObject(url, String.class);
            String responsEnd = new String(response2.getBytes("ISO-8859-1"), "UTF-8");
            log.info("网页授权获取用户信息响应结果：{}", responsEnd);
            jb =responsEnd;
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, Object> mapp = new HashMap<>();
        mapp.put("openId", openid);
        mapp.put("appId", gzhInfoMap.get(state).get("appId"));
        mapp.put("userInfo", jb);
        return mapp;
    }


    /**
     * JSAPI支付统一下单
     * @param openid
     * @param productDesc
     * @param tradeNo
     * @param ip
     * @param price
     * @return
     */
    public Map<String, String> unifiedorder( String openid, String productDesc,String tradeNo, String ip, String price, String url) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", WXConstants.APPID.getValue());
        paramMap.put("body", productDesc);
        paramMap.put("mch_id", WXConstants.MCHID.getValue());
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("openid", openid);
        paramMap.put("out_trade_no", tradeNo);
        paramMap.put("spbill_create_ip", ip);
        paramMap.put("total_fee", price);
        paramMap.put("trade_type", "JSAPI");
        paramMap.put("notify_url",url);
        String requestXml = "";
        try {
            String sign = WXPayUtil.generateSignature(paramMap, WXConstants.KEY.getValue());
            paramMap.put("sign", sign);
            requestXml = WXPayUtil.mapToXml(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            log.info("微信JSAPI支付统一下单请求参数：{}", requestXml);
            String responseXml  = HttpRequest.sendPost(WXConstants.UNIFIEDORDERURL.getValue(), requestXml);
            log.info("微信JSAPI支付统一下单响应结果：{}", responseXml);
            Map<String, String> responseMap = WXPayUtil.xmlToMap(responseXml);
            if ("SUCCESS".equals(responseMap.get("return_code")) && "SUCCESS".equals(responseMap.get("result_code"))) {
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("appId", WXConstants.APPID.getValue());
                resultMap.put("timeStamp", String.valueOf(WXPayUtil.getCurrentTimestamp()));
                resultMap.put("nonceStr", WXPayUtil.generateNonceStr());
                resultMap.put("signType", "MD5");
                resultMap.put("package", "prepay_id=" + responseMap.get("prepay_id"));
                String paySign = WXPayUtil.generateSignature(resultMap, WXConstants.KEY.getValue());
                resultMap.put("paySign", paySign);
                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
