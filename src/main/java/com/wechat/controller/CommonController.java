package com.wechat.controller;

import com.wechat.config.WXConstants;
import com.wechat.model.Result;
import com.wechat.utils.HttpRequest;
import com.wechat.utils.RedisUtil;
import com.wechat.utils.Str2MD5;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 公共接口
 *
 * @author wx
 * @date 2021/6/28 18:25
 */
@Slf4j
@Controller
@RequestMapping("common")
public class CommonController {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 发送短信验证码
     *
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("sendCaptcha")
    public Map sendCaptcha(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String mobile = request.getParameter("mobile");
        String ts = String.valueOf(System.currentTimeMillis());
        //根据手机号去redis取值，看看今天发了几次
        String count = "";
        try{
            count = redisUtil.get(mobile+"耀昇智慧物联").toString();
        }catch (Exception e){
            count = "";
        }
        if(StringUtils.isBlank(count)){
            redisUtil.set(mobile+"耀昇智慧物联", "1", getEndTime());
        }else{
            if(Integer.parseInt(count)==5){
                map.put("code", 1);
                map.put("msg", "今日获取验证码已达上限，请明日再试");
                return map;
            }else{
                int co = Integer.parseInt(count)+1;
                redisUtil.set(mobile+"耀昇智慧物联", String.valueOf(co), getEndTime());
            }
        }
        String sign = Str2MD5.MD532(WXConstants.USERID.getValue() + ts + WXConstants.APIKEY.getValue());
        String captcha = "";
        try{
            captcha = redisUtil.get(mobile).toString();
        }catch (Exception e){
            captcha = "";
        }
        if(StringUtils.isBlank(captcha)){
            captcha = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            redisUtil.set(mobile, captcha, 300);
        }
        String content = "【耀昇智慧物联】您的操作验证码：" + captcha + "。如非本人操作，可不用理会！（验证码5分钟内有效，请完成验证）";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", WXConstants.USERID.getValue());
        params.put("ts", ts);
        params.put("sign", sign.toLowerCase());
        params.put("mobile", mobile);
        params.put("msgcontent", content);
        try {
            log.info(mobile + "发送短信验证码请求参数：{}", params);
            String result = HttpRequest.net(WXConstants.SENDURL.getValue(), params, "POST");
            log.info(mobile + "发送短信验证码响应结果：{}", result);
            JSONObject json = JSONObject.fromObject(result);
            String code = json.getString("code");
            if ("0".equals(code)) {
                map.put("code", 0);
                map.put("msg", "验证码已发送");
            } else {
                map.put("code", 1);
                map.put("msg", "验证码发送失败，请重试");
            }
        } catch (Exception e) {
            log.error(mobile + "验证码发送异常：", e);
            map.put("code", 1);
            map.put("msg", "验证码发送失败，请重试");
        }
        return map;
    }

    @ResponseBody
    @PostMapping("broadbandCaptcha")
    public Map broadbandCaptcha(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String mobile = request.getParameter("mobile");
        String ts = String.valueOf(System.currentTimeMillis());
        String sign = Str2MD5.MD532(WXConstants.USERID.getValue() + ts + WXConstants.APIKEY.getValue());
        //根据手机号去redis取值，看看今天发了几次
        String count = "";
        try{
            count = redisUtil.get(mobile+"宽带办理").toString();
        }catch (Exception e){
            count = "";
        }
        if(StringUtils.isBlank(count)){
            redisUtil.set(mobile+"宽带办理", "1", getEndTime());
        }else{
            if(Integer.parseInt(count)==5){
                map.put("code", 1);
                map.put("msg", "今日获取验证码已达上限，请明日再试");
                return map;
            }else{
                int co = Integer.parseInt(count)+1;
                redisUtil.set(mobile+"宽带办理", String.valueOf(co), getEndTime());
            }
        }
        String captcha = "";
        try{
            captcha = redisUtil.get(mobile).toString();
        }catch (Exception e){
            captcha = "";
        }
        if(StringUtils.isBlank(captcha)){
            captcha = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            redisUtil.set(mobile, captcha, 300);
        }
        String content = "【宽带办理】尊敬的用户您好，您的短信验证码为：" + captcha + "，有效期为5分钟，请妥善保管验证码！";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", WXConstants.USERID.getValue());
        params.put("ts", ts);
        params.put("sign", sign.toLowerCase());
        params.put("mobile", mobile);
        params.put("msgcontent", content);
        try {
            log.info(mobile + "发送短信验证码请求参数：{}", params);
            String result = HttpRequest.net(WXConstants.SENDURL.getValue(), params, "POST");
            log.info(mobile + "发送短信验证码响应结果：{}", result);
            JSONObject json = JSONObject.fromObject(result);
            String code = json.getString("code");
            if ("0".equals(code)) {
                map.put("code", 0);
                map.put("msg", "验证码已发送");
            } else {
                map.put("code", 1);
                map.put("msg", "验证码发送失败，请重试");
            }
        } catch (Exception e) {
            log.error(mobile + "验证码发送异常：", e);
            map.put("code", 1);
            map.put("msg", "验证码发送失败，请重试");
        }
        return map;
    }

    @ResponseBody
    @PostMapping("liuliangCaptcha")
    public Map liuliangCaptcha(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String mobile = request.getParameter("mobile");
        String ts = String.valueOf(System.currentTimeMillis());
        String sign = Str2MD5.MD532(WXConstants.USERID.getValue() + ts + WXConstants.APIKEY.getValue());
        //根据手机号去redis取值，看看今天发了几次
        String count = "";
        try{
            count = redisUtil.get(mobile+"流量卡办理").toString();
        }catch (Exception e){
            count = "";
        }
        if(StringUtils.isBlank(count)){
            redisUtil.set(mobile+"流量卡办理", "1", getEndTime());
        }else{
            if(Integer.parseInt(count)==5){
                map.put("code", 1);
                map.put("msg", "今日获取验证码已达上限，请明日再试");
                return map;
            }else{
                int co = Integer.parseInt(count)+1;
                redisUtil.set(mobile+"流量卡办理", String.valueOf(co), getEndTime());
            }
        }
        String captcha = "";
        try{
            captcha = redisUtil.get(mobile).toString();
        }catch (Exception e){
            captcha = "";
        }
        if(StringUtils.isBlank(captcha)){
            captcha = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            redisUtil.set(mobile, captcha, 300);
        }
        String content = "【流量卡办理】尊敬的用户您好，您的短信验证码为：" + captcha + "，有效期为5分钟，请妥善保管验证码！";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", WXConstants.USERID.getValue());
        params.put("ts", ts);
        params.put("sign", sign.toLowerCase());
        params.put("mobile", mobile);
        params.put("msgcontent", content);
        try {
            log.info(mobile + "发送短信验证码请求参数：{}", params);
            String result = HttpRequest.net(WXConstants.SENDURL.getValue(), params, "POST");
            log.info(mobile + "发送短信验证码响应结果：{}", result);
            JSONObject json = JSONObject.fromObject(result);
            String code = json.getString("code");
            if ("0".equals(code)) {
                map.put("code", 0);
                map.put("msg", "验证码已发送");
            } else {
                map.put("code", 1);
                map.put("msg", "验证码发送失败，请重试");
            }
        } catch (Exception e) {
            log.error(mobile + "验证码发送异常：", e);
            map.put("code", 1);
            map.put("msg", "验证码发送失败，请重试");
        }
        return map;
    }

//    /**
//     * @description: 处理跨域问题影响， 根据传递过来的url ，使用服务器发送请求   暂时弃用（wechat项目模板 小程序api回传估计可以使用）
//     * @author: 齐振浩
//     * @date: 2023/5/25
//     * @param: sendGetUrl
//     * @return: com.wechat.model.Result
//     **/
//    @ResponseBody
//    @RequestMapping("sendGetUrl")
//    @CrossOrigin
//    public Result sendGetUrl(@RequestBody String sendGetUrl) throws UnsupportedEncodingException {
//        if (StringUtils.isEmpty(sendGetUrl)){
//            return Result.error("参数不全");
//        }
//        String newUrl = URLDecoder.decode(sendGetUrl, "utf-8");
//        newUrl = newUrl.trim().replace("\"", "");
//        Result response = restTemplate.getForObject(newUrl, Result.class);
//        return response;
//    }


    /**
     * @description: 处理跨域问题影响， 根据传递过来的url ，以及参数，请求方式 ，使用服务器发送请求
     * @author: 齐振浩
     * @date: 2023/5/25
     * @param: sendGetUrl
     * @return: com.wechat.model.Result
     **/
    @ResponseBody
    @RequestMapping("helpSendUrl")
    @CrossOrigin
    public Result sendGetUrl(@RequestBody com.alibaba.fastjson.JSONObject jsonObject) {
        try {
            log.info("++++++++++++++++++++++++++++++++++++++++");
            log.info(jsonObject.toJSONString());
            if (null == jsonObject){
                return Result.error("参数为空");
            }
            com.alibaba.fastjson.JSONObject paramsReal = jsonObject.getJSONObject("params");
            String method = paramsReal.get("method").toString();
            String url = paramsReal.get("url").toString();
            com.alibaba.fastjson.JSONObject params = paramsReal.getJSONObject("params");
            if (StringUtils.isEmpty(url) || StringUtils.isEmpty(method)){
                return Result.error("参数不全");
            }
            String endUrl = URLDecoder.decode(url, "utf-8");
            endUrl = endUrl.trim().replace("\"", "");
            if ("get".equals(method)){
                //get请求转发
                Result response = restTemplate.getForObject(endUrl, Result.class);
                return response;
            }else if ("post".equals(method)){
                //post请求转发
                Result response = restTemplate.postForObject(endUrl, params, Result.class);
                return response;
            }else {
                return Result.error("method参数有误");
            }
        }catch (Exception e){
            log.info("++++++++++++++++++++++++异常");
            log.info(e.getMessage());
            return Result.error("请求异常");
        }
    }

    //获取当前时间到今天结束时间所剩余的毫秒数：
    public static long getEndTime() {
        //获取当前时间的毫秒数
        long time = new java.util.Date().getTime();
        //获取到今天结束的毫秒数
        Calendar todayEnd = Calendar.getInstance();
        // Calendar.HOUR 12小时制。HOUR_OF_DAY 24小时制
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        long endTime = todayEnd.getTimeInMillis();
        //这里endTime-time获取的是到23：59：59：999的毫秒数。再加1才是到24点整的毫秒数
        return endTime-time+1;
    }
}
