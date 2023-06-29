package com.wechat.utils;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Frequently {

    private static Map<String,Long[]> countMap = new ConcurrentHashMap<>();

    /**
     * 访问是否频繁
     * @param userName 用户名
     * @param seconds 秒
     * @param frequency 频次
     * @return boolean true/false
     */
    public static boolean isLimit(String userName, int seconds, int frequency){
        boolean b=false;
        Long[] key = countMap.get(userName);
        if(key != null){//不是第一次访问
            long time  = System.currentTimeMillis()/1000 - key[0];
            if(time<seconds){//多少时间内
                long count = key[1];
                if(count >= frequency){
                    b=true;
                }else{
                    countMap.putIfAbsent(userName, new Long[]{key[0],key[1]+1});
                }
            }else{
                countMap.putIfAbsent(userName, new Long[]{System.currentTimeMillis()/1000, 1L});
            }
        }else{//第一次访问
            countMap.putIfAbsent(userName, new Long[]{System.currentTimeMillis()/1000, 1L});
        }
        return b;
    }

    public static int count(String request){
        int count = 0;
        try{
            JSONObject jsonObject = JSONObject.fromObject(request);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for(int i=0;i<jsonArray.size();i++){
                JSONObject js = jsonArray.getJSONObject(i);
                JSONArray jar = js.getJSONArray("chargeList");
                count += jar.size();
            }
        }catch (Exception e){
            log.error(request+"解析提交的订单数量异常{}", e);
        }

        return count;
    }
}
