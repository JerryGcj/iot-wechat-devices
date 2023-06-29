package com.wechat.controller;

import com.wechat.config.WXConstants;
import com.wechat.model.Result;
import com.wechat.utils.RedisUtil;
import com.wechat.utils.WxApiUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 推广员和门店中心
 */
@CrossOrigin(origins = "*",maxAge = 3600)
@Controller
@Slf4j
@RequestMapping("")
public class PromoterCenterController {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private WxApiUtil wxApiUtil;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 跳转到推广员店铺中心页面
     * 推广员和店铺进入同一个页面
     * @param model
     * @param code
     * @param openid
     * @return
     */
    @GetMapping("promoterCenter")
    public String promoterCenter(Model model, String code, String openid) {
        if(StringUtils.isBlank(openid)) {
            openid = wxApiUtil.getOpenid(code);
        }
        model.addAttribute("openid", openid);
        try {
            log.info("调用后台根据openid获取用户信息接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.QUERY_USER_BY_OPENID_URL.getValue() + "?openId="+openid, Result.class);
            log.info("调用后台根据openid获取用户信息接口，响应结果：{}", response.toString());
            if (response.isSuccess() && response.getResult() != null) {
                model.addAttribute("user", response.getResult());
            } else {
                return "login";
            }
            log.info("调用后台查询已结算及未结算金额接口，请求参数：{}",openid);
            Result response2 = restTemplate.getForObject(WXConstants.MONEY_URL.getValue() + "?openId="+openid, Result.class);
            log.info("用后台查询已结算及未结算金额接口，响应结果：{}", response2.toString());
            if (response2.isSuccess()) {
                model.addAttribute("money", response2.getResult());
            }
        } catch (Exception e){
            log.error("用后台查询已结算及未结算金额接口异常",e);
        }
        return "promoterCenter";
    }

    /**
     * 跳转添加店铺页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("addStore")
    public String add(Model model, String openid) {
        model.addAttribute("openid", openid);
        return "addStore";
    }

    /**
     * 跳转店铺列表
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("storeList")
    public String list(Model model, String openid) {
        model.addAttribute("openid", openid);
        try {
            log.info("调用后台查询店铺列表接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.STORELIST_URL.getValue() + "/"+openid, Result.class);
            log.info("调用后台查询店铺列表接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("storeList", response.getResult());
            }
        }catch (Exception e){
            log.error("用后台查询店铺列表接口异常",e);
        }
        return "storeList";
    }

    /**
     * 添加店铺
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("addStore")
    public Result add(HttpServletRequest request) {
        String openid = request.getParameter("openid");
        String username = request.getParameter("username");
        String phone = request.getParameter("phone");
        String storeName = request.getParameter("storeName");
        String address = request.getParameter("address");
        if (StringUtils.isBlank(openid) || StringUtils.isBlank(username) || StringUtils.isBlank(phone) || StringUtils.isBlank(storeName) || StringUtils.isBlank(address)) {
            return Result.error("参数不全，请输入完整参数");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("openid", openid);
        paramMap.put("realname", username);
        paramMap.put("phone", phone);
        paramMap.put("userCompany", storeName);
        paramMap.put("storeAddress", address);
        // 调用后台接口
        log.info("调用后台添加店铺接口，请求参数：{}", paramMap);
        Result response = restTemplate.postForObject(WXConstants.SAVE_STORE_URL.getValue(), paramMap, Result.class);
        log.info("调用后台添加店铺接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok("提交成功");
        } else {
            return Result.error(response.getMessage());
        }
    }

    /**
     * 提现
     * @param model
     * @param openid
     * @return
     */
    @ResponseBody
    @PostMapping("withdrawal")
    public Result withdrawal(Model model, String openid,String money) {
        JSONObject jsonObject = JSONObject.fromObject(money);
        if (jsonObject.getString("noSettle").equals("0.0")){
            return Result.error("您没余额可以提现");
        }
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("bankAccount",openid);
        stringStringHashMap.put("timeBegin",jsonObject.getString("timeBegin"));
        stringStringHashMap.put("timeEnd",jsonObject.getString("timeEnd"));
        stringStringHashMap.put("money",jsonObject.getString("noSettle"));
        try {
            log.info("调用提现接口，请求参数：{}",openid);
            Result response = restTemplate.postForObject(WXConstants.WITHDRAWAL_URL.getValue(),stringStringHashMap, Result.class);
            log.info("调用提现接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                return Result.ok(response.getMessage());
            } else {
                return Result.error(response.getMessage());
            }
        } catch (Exception e){
            log.error("调用提现接口异常",e);
            return Result.error("提现异常，请联系管理员");
        }
    }

    /**
     * 根据手机号查询用户是否存在
     * @param phone
     * @return
     */
    @ResponseBody
    @GetMapping("queryUserByPhone")
    public Result queryUserByPhone(String phone) {
        // 调用后台接口
        log.info("调用后台根据手机号查询用户是否存在接口，请求参数：{}", phone);
        Result response = restTemplate.getForObject(WXConstants.QUERY_USER_BY_PHONE_URL.getValue() + "?phone=" + phone, Result.class);
        log.info("调用后台根据手机号查询用户是否存在接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok(response.getResult());
        } else {
            return Result.error(response.getMessage());
        }
    }

    /**
     * 用户登录
     * @param openid
     * @param phone
     * @param captchaSms
     * @return
     */
    @ResponseBody
    @PostMapping("login")
    public Result login(String openid, String phone, String captchaSms) {
        if (StringUtils.isBlank(openid) || StringUtils.isBlank(phone) || StringUtils.isBlank(captchaSms)) {
            return Result.error("参数不全，请输入完整参数");
        }

        String authCode = "";
        try{
            authCode = String.valueOf(redisUtil.get(phone));
        }catch (Exception e) {
            authCode = "";
        }
        if(StringUtils.isEmpty(authCode)) {
            return Result.error("验证码已失效，请重新获取");
        }
        if(!captchaSms.equals(authCode)) {
            return Result.error("验证码错误");
        }

        // 调用后台接口
        log.info("调用后台更新用户openid接口，请求参数：{}", openid + "," + phone);
        Result response = restTemplate.getForObject(WXConstants.UPDATE_OPENID_URL.getValue() + "?openId=" + openid + "&phone=" + phone, Result.class);
        log.info("调用后台更新用户openid接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok("登录成功");
        } else {
            return Result.error(response.getMessage());
        }
    }

    /**
     * 跳转到卡分配页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("cardDistribution")
    public String cardDistribution(Model model, String openid) {
        model.addAttribute("openid", openid);
        try {
            log.info("调用后台查询店铺列表接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.STORELIST_URL.getValue() + "/"+openid, Result.class);
            log.info("调用后台查询店铺列表接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("storeList", response.getResult());
            }
        }catch (Exception e){
            log.error("用后台查询店铺列表接口异常",e);
        }
        return "cardDistribution";
    }

    /**
     * 保存卡分配信息
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("saveCardDistribution")
    public Result saveCardDistribution(HttpServletRequest request) {
        String openid = request.getParameter("openid");
        String iccidStart = request.getParameter("iccidStart");
        String iccidEnd = request.getParameter("iccidEnd");
        String storeId = request.getParameter("storeId");
        String operatorType = request.getParameter("operatorType");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("start", iccidStart);
        paramMap.put("end", iccidEnd);
        paramMap.put("lowerAgentId", storeId);
        paramMap.put("operatorType", operatorType);
        // 调用后台接口
        log.info("调用后台卡分配接口，请求参数：{}", paramMap);
        Result response = restTemplate.postForObject(WXConstants.CARDDISTRIBUTION.getValue(), paramMap, Result.class);
        log.info("调用后台卡分配接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok(response.getMessage());
        } else {
            return Result.error(response.getMessage());
        }
    }

    /**
     * 跳转添加日志页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("addJobLog")
    public String addJobLog(Model model, String openid) {
        model.addAttribute("openid", openid);
        return "addJobLog";
    }

    @ResponseBody
    @PostMapping("addJobLog")
    public Result addJobLog(@RequestParam("photo") MultipartFile multipartFile, String openid, String content) {
        if (StringUtils.isBlank(openid) || StringUtils.isBlank(content)) {
            return Result.error("参数不全，请输入完整参数");
        }
        // 设置提交方式都是表单提交
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        // 设置表单信息
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("openId", openid);
        paramMap.add("content", content);
        try {
            paramMap.add("photo", new ByteArrayResource(multipartFile.getBytes()) {
                @Override
                public String getFilename() {
                    return multipartFile.getOriginalFilename();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = new HttpEntity(paramMap, headers);
        // 调用后台接口
        log.info("调用后台添加工作日志接口，请求参数：{}", openid);
        Result response = restTemplate.postForObject(WXConstants.ADD_JOB_LOG_URL.getValue(), httpEntity, Result.class);
        log.info("调用后台添加工作日志接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok(response.getResult());
        } else {
            return Result.error(response.getMessage());
        }
    }
}
