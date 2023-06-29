package com.wechat.controller;

import com.wechat.config.WXConstants;
import com.wechat.model.Result;
import com.wechat.model.ScanCode;
import com.wechat.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("mobile")
public class MobileController {

    @Autowired
    private WxApiUtil wxApiUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RestTemplate restTemplate;


    /**
     * 跳转到用户中心页面
     * @param model
     * @param code
     * @param openid
     * @return
     */
    @GetMapping("userCenter")
    public String userCenter(Model model, String code, String openid, Boolean flag, String iccid) {
        // 根据code获取openid
        if(StringUtils.isBlank(openid)) {
            openid = wxApiUtil.getOpenid(code);
        }
        model.addAttribute("openid", openid);
        model.addAttribute("flag", flag);
        try {
            log.info("调用后台查询套餐基本信息接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.USERCENTER_URL.getValue() + "?openId="+openid+"&iccid="+iccid+"&operatorType=1", Result.class);
            log.info("调用后台查询套餐基本信息接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("user", response.getResult());
            } else {
                return "mobileRegister";
            }
        } catch (Exception e){
            log.error("调用后台查询套餐基本信息接口异常",e);
        }
        return "mobileCenter";
    }

    /**
     * 绑定新卡
     * @param model
     * @param openid
     * @return
     */
    @RequestMapping("bindingCard")
    public String bindingCard(Model model, String openid) {
        model.addAttribute("openid", openid);
        return "mobileRegister";
    }

    /**
     * 跳转到套餐购买页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("setMealCenter")
    public String setMealCenter(Model model,String openid, String operatorType, String iccid) {
        model.addAttribute("openid", openid);
        model.addAttribute("iccid", iccid);
        try {
            log.info("调用后台查询流量套餐商品接口，请求参数：openid={},iccid={},operatorType={}",openid,iccid,operatorType);
            Result response = restTemplate.getForObject(WXConstants.PACKAGELIST_URL.getValue()+"?iccid="+iccid+"&operatorType="+operatorType, Result.class);
            log.info("调用后台查询流量套餐商品接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) response.getResult();
                List<HashMap<String, Object>> packageType = result.stream().sorted(Comparator.comparing(o -> new BigDecimal(o.get("salesPrice").toString()))).filter(item -> item.get("packageType").equals("0")).collect(Collectors.toList());
                //List<HashMap<String, String>> quarterPackage = result.stream().filter(item -> item.get("packageType").equals("0")&&item.get("packageName").startsWith("尊享季度卡")).collect(Collectors.toList());
                //List<HashMap<String, String>> halfYearPackage = result.stream().filter(item -> item.get("packageType").equals("0")&&item.get("packageName").startsWith("尊享半年卡")).collect(Collectors.toList());
                //List<HashMap<String, String>> yearPackage = result.stream().filter(item -> item.get("packageType").equals("0")&&item.get("packageName").startsWith("尊享年卡")).collect(Collectors.toList());
                List<HashMap<String, Object>> packageType1 = result.stream().sorted(Comparator.comparing(o -> new BigDecimal(o.get("salesPrice").toString()))).filter(item -> item.get("packageType").equals("1")).collect(Collectors.toList());
                model.addAttribute("packageList",packageType);
                //model.addAttribute("quarterPackage",quarterPackage);
                //model.addAttribute("halfYearPackage",halfYearPackage);
                //model.addAttribute("yearPackage",yearPackage);
                model.addAttribute("packageList1",packageType1);
            }
            log.info("调用后台查询套餐基本信息接口，请求参数：openid={},iccid={},operatorType={}",openid,iccid,operatorType);
            Result response2 = restTemplate.getForObject(WXConstants.USERCENTER_URL.getValue() + "?openId="+openid+"&iccid="+iccid+"&operatorType=1", Result.class);
            log.info("调用后台查询套餐基本信息接口，响应结果：{}", response2.toString());
            if (response.isSuccess()) {
                model.addAttribute("user", response2.getResult());
            }
        }catch (Exception e) {
            log.error("调用后台查询流量套餐商品接口异常",e);
        }
        return "mobileMealCenter";
    }

    /**
     * 跳转到充值记录页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("rechargeRecord")
    public String rechargeRecord(Model model, String openid, String operatorType) {
        try {
            log.info("调用后台查询充值记录接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.RECHARGERECORD_URL.getValue()+ "?openId="+openid+"&operatorType="+operatorType, Result.class);
            log.info("调用后台查询充值记录接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("rechargeRecordList",response.getResult());
            }
        }catch (Exception e) {
            log.error("调用后台查询充值记录接口异常",e);
        }
        return "unicomRechargeRecord";
    }

    /**
     * 跳转到消费记录页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("billDetails")
    public String billDetails(Model model, String openid, String iccid, String operatorType) {
        try {
            log.info("调用后台查询消费记录接口，请求参数：openid={}，iccid={}，operatorType={}",openid,iccid,operatorType);
            Result response = restTemplate.getForObject(WXConstants.BILLDETAILS_URL.getValue()+ "?openId="+openid+"&iccid="+iccid+"&operatorType="+operatorType, Result.class);
            log.info("调用后台查询消费记录接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("billDetailsList",response.getResult());
            }
        }catch (Exception e) {
            log.error("调用后台查询消费记录接口异常",e);
        }
        return "mobileBillDetails";
    }

    /**
     * 用户登记
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("register")
    public Result register(HttpServletRequest request) {
        String openid = request.getParameter("openid");
        String cardId = request.getParameter("cardId");
        String phone = request.getParameter("phone");
        String captchaSms = request.getParameter("captchaSms");
        String remark = request.getParameter("remark");
        if (StringUtils.isBlank(openid) || StringUtils.isBlank(cardId) || StringUtils.isBlank(phone) || StringUtils.isBlank(captchaSms)) {
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

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("iccid", cardId);
        paramMap.put("openId", openid);
        paramMap.put("mobile", phone);
        paramMap.put("operatorType", 1);
        paramMap.put("type", "0");
        paramMap.put("remark", remark);
        // 调用后台接口
        log.info("调用后台用户注册接口，请求参数：{}", paramMap);
        Result response = restTemplate.postForObject(WXConstants.USER_REGISTER_URL.getValue(), paramMap, Result.class);
        log.info("调用后台用户注册接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok("提交成功");
        } else {
            return Result.error(response.getMessage());
        }
    }

    /**
     * 用户实名登记
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("realnameRegister")
    public Result realnameRegister(HttpServletRequest request) {
        String openid = request.getParameter("openid");
        String cardId = request.getParameter("cardId");
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String identityCode = request.getParameter("identityCode");
        String captchaSms = request.getParameter("captchaSms");
        if (StringUtils.isBlank(openid) || StringUtils.isBlank(name) || StringUtils.isBlank(identityCode) || StringUtils.isBlank(cardId) || StringUtils.isBlank(phone) || StringUtils.isBlank(captchaSms)) {
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
        identityCode = identityCode.replaceAll("(\\d{6})\\d{8}(\\w{4})","$1****$2");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("iccid", cardId);
        paramMap.put("name", name);
        paramMap.put("idCardNumber", identityCode);
        paramMap.put("openId", openid);
        paramMap.put("mobile", phone);
        paramMap.put("operatorType", 1);
        // 调用后台接口
        log.info("调用后台用户实名登记接口，请求参数：{}", paramMap);
        Result response = restTemplate.postForObject(WXConstants.REALNAME_REGISTER_URL.getValue(), paramMap, Result.class);
        log.info("调用后台用户实名登记接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok(response.getMessage());
        } else {
            return Result.error(response.getMessage());
        }
    }

    /**
     * 直接购买套餐
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("directPurchase")
    public Result directPurchase(HttpServletRequest request) {
        String openid = request.getParameter("openid");
        String iccid = request.getParameter("iccid");
        String productId = request.getParameter("productId");
        String price = request.getParameter("price");
        String packageName = request.getParameter("packageName");
        String createUser = request.getParameter("createUser");
        if (StringUtils.isBlank(openid) || StringUtils.isBlank(productId) || StringUtils.isBlank(price)) {
            return Result.error("参数不全，请输入完整参数");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("openId", openid);
        paramMap.put("iccid", iccid);
        paramMap.put("appId", WXConstants.APPID.getValue());
        paramMap.put("mchId", WXConstants.MCHID.getValue());
        paramMap.put("operatorType", "1");
        paramMap.put("packageId", productId);
        paramMap.put("salesPrice", price);
        paramMap.put("createUser", createUser);
        // 调用后台接口
        log.info("调用后台充值下订单接口，请求参数：{}", paramMap);
        Result response = restTemplate.postForObject(WXConstants.SAVE_ORDER_URL.getValue(), paramMap, Result.class);
        log.info("调用后台充值下订单接口，响应结果：{}", response.toString());
        if (!response.isSuccess()) {
            return Result.error(response.getMessage());
        }

        String description = packageName + "充值";
        String ip = IPUtil.getIpAddress(request);
        price = new BigDecimal(price).multiply(new BigDecimal("100")).setScale(0).toString();
        // 调用JSAPI支付
        Map<String, String> resultMap = wxApiUtil.unifiedorder(openid, description, response.getMessage(), ip, price, WXConstants.NOTIFYBACK.getValue());
        if (resultMap == null) {
            return Result.error("支付失败");
        }
        return Result.ok(resultMap);
    }

    /**
     * 查询是否有成功的订单
     * @param iccid
     * @return
     */
    @ResponseBody
    @GetMapping("succOrder")
    public Result succOrder(String iccid, String operatorType) {
        // 调用后台接口
        log.info("调用后台查询是否有成功的订单接口，请求参数：{}", iccid);
        Result response = restTemplate.getForObject(WXConstants.SUCCORDER_URL.getValue()+"?iccid="+iccid+"&operatorType="+operatorType, Result.class);
        log.info("调用后台查询是否有成功的订单接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok(response.getMessage());
        } else {
            return Result.error(response.getMessage());
        }
        /*model.addAttribute("iccid",iccid);
        model.addAttribute("openid",openid);
        return "realnameRegister";*/
    }

    @GetMapping("queryFlow")
    public String queryFlow(Model model, String openid, String iccid, String operatorType) {
        try {
            log.info("调用后台查询所有套餐接口，请求参数：openid={}，iccid={}",openid, iccid);
            Result response = restTemplate.getForObject(WXConstants.QUERYFLOW_URL.getValue()+ "?openid="+openid+"&iccid="+iccid+"&operatorType="+operatorType, Result.class);
            log.info("调用后台查询所有套餐接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("iccid",iccid);
                model.addAttribute("openid",openid);
                model.addAttribute("packageList",response.getResult());
            }
        }catch (Exception e) {
            log.error("调用后台查询所有套餐接口异常",e);
        }
        return "packageDetails";
    }

    @ResponseBody
    @GetMapping("realNameStatus")
    public Result realNameStatus(String iccid, String operatorType) {
        // 调用后台接口
        log.info("调用后台查询实名状态接口，请求参数：iccid={}，operatorType={}", iccid,operatorType);
        Result response = restTemplate.getForObject(WXConstants.REALNAME_URL.getValue()+"?iccid="+iccid+"&operatorType="+operatorType+"&type=front", Result.class);
        log.info("调用后台查询实名状态接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok(response.getResult());
        } else {
            return Result.error(response.getMessage());
        }
    }

    @RequestMapping("success")
    public String succ(Model model,String openid, String iccid) {
        model.addAttribute("iccid",iccid);
        model.addAttribute("openid",openid);
        // 调用后台接口
        log.info("调用后台查询实名状态接口，请求参数：{}", iccid);
        Result response = restTemplate.getForObject(WXConstants.REALNAME_URL.getValue()+"?iccid="+iccid+"&operatorType=1&type=backed", Result.class);
        log.info("调用后台查询实名状态接口，响应结果：{}", response.toString());
        if (!response.isSuccess()) {
            model.addAttribute("status","0");
        }else{
            Map<String,Object> map = (Map) response.getResult();
            String status = map.get("isRealNameAuthentication").toString();
            model.addAttribute("status",status);
        }
        return "mobileSuccess";
    }

    /**
     * 转到用户认证页面
     * @param iccid
     * @param model
     * @return
     */
    @GetMapping("user_auth")
    public ModelAndView auth(String openid, String iccid, Model model) {
        model.addAttribute("iccid", iccid);
        model.addAttribute("openid", openid);
        return new ModelAndView("mobileRealnameRegister");
    }

    /**
     * 卡备注
     * @param model
     * @param openid
     * @param iccid
     * @param remark
     * @return
     */
    @ResponseBody
    @PostMapping("remarkCard")
    public Result remarkCard(Model model,String openid, String iccid, String remark) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("openId", openid);
        paramMap.put("iccid", iccid);
        paramMap.put("remark", remark);
        model.addAttribute("openid", openid);
        log.info("调用后台卡备注接口，请求参数：{}", paramMap);
        Result response = restTemplate.postForObject(WXConstants.REMARK_URL.getValue(), paramMap, Result.class);
        log.info("调用后台卡备注接口，响应结果：{}", response.toString());
        return Result.ok(response.getMessage());
    }

    /**
     * 备注成功转到主页
     * @param model
     * @param openid
     * @param iccid
     * @return
     */
    @RequestMapping("home")
    public String home(Model model,String openid, String iccid) {
        model.addAttribute("openid", openid);
        model.addAttribute("iccid", iccid);
        Result response = restTemplate.getForObject(WXConstants.USERCENTER_URL.getValue() + "?openId="+openid+"&iccid="+iccid+"&operatorType=1", Result.class);
        log.info("调用后台查询套餐基本信息接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            model.addAttribute("user", response.getResult());
        }
        return "mobileCenter";
    }

    @RequestMapping("iccidList")
    public String iccidList(Model model,String openid, String operatorType) {
        model.addAttribute("openid", openid);
        Result response = restTemplate.getForObject(WXConstants.QUERYBYOPENID_URL.getValue() + "?openid="+openid+"&operatorType="+operatorType, Result.class);
        log.info("根据openid查询绑定的卡接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            model.addAttribute("cardList", response.getResult());
        }
        return "mobileIccidList";
    }

    @ResponseBody
    @GetMapping("getByOpenid")
    public Result getByOpenid(Model model,String openid, String operatorType) {
        Result response = restTemplate.getForObject(WXConstants.QUERYBYOPENID_URL.getValue() + "?openid="+openid+"&operatorType="+operatorType, Result.class);
        log.info("根据openid查询绑定的卡接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok(response.getResult());
        } else {
            return Result.error(response.getMessage());
        }
    }

    /**
     * 老用户中心
     * @param model
     * @param code
     * @param state
     * @return
     */
    @GetMapping("oldUserCenter")
    public String oldUserCenter(Model model, String code, String state) {
        String openid = "";
        // 根据code获取openid
        if(StringUtils.isBlank(openid)) {
            openid = wxApiUtil.getOpenid(code);
        }
        model.addAttribute("openid", openid);
        model.addAttribute("newIccid", state);
        model.addAttribute("flag", true);
        return "lxf/oldRegister";
    }

    /**
     * 获取验证码
     * @param request
     * @return
     */
    @PostMapping("acquireAuthCode")
    @ResponseBody
    public Map acquireAuthCode(HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        String mobile = request.getParameter("mobile");
        String ts = String.valueOf(System.currentTimeMillis());
        String md5 = Str2MD5.MD532(WXConstants.USERID.getValue()+ ts + WXConstants.APIKEY.getValue());
        String authCode = String.valueOf((int) ((Math.random()*9+1)*100000));
        redisUtil.set(mobile, authCode, 300);
        String content = "【耀昇智慧物联】您正在进行身份认证，验证码："+authCode+"。如非本人操作，可不用理会！（验证码5分钟内有效，请完成验证）";
        Map<String, String> params = new  HashMap<String, String>();
        params.put("userid", WXConstants.USERID.getValue());
        params.put("ts",ts);
        params.put("sign",md5.toLowerCase());
        params.put("mobile", mobile);
        params.put("msgcontent", content);
        try {
            log.info(mobile+"获取验证码请求{}", params);
            String result = HttpRequest.net(WXConstants.SENDURL.getValue(), params, "POST");
            log.info(mobile+"获取验证码响应结果{}", result);
            JSONObject json = JSONObject.fromObject(result);
            String code = json.getString("code");
            if("0".equals(code)){
                map.put("code", 0);
                map.put("msg", "验证码已下发");
            }else{
                map.put("code", 1);
                map.put("msg", "验证码获取失败，请重试");
            }
        } catch (Exception e) {
            log.error(mobile+"获取验证码请求异常：", e);
            map.put("code", 1);
            map.put("msg", "验证码获取失败，请重试");
        }
        return map;
    }

    /*@PostMapping("upload")
    @ResponseBody
    public Map upload(HttpServletRequest request){
        Map<String, Object> map = new HashMap<String, Object>();
        try{
            String iccid = request.getParameter("iccid");
            String realIccid = request.getParameter("realIccid");
            String name = request.getParameter("name");
            String identityCode = request.getParameter("identityCode");
            String mobile = request.getParameter("mobile");
            String authCode = request.getParameter("authCode");
            String auth_code = "";
            try{
                auth_code = (String) redisUtil.get(mobile);
            }catch (Exception e){
                auth_code = "";
            }
            if(StringUtils.isEmpty(auth_code)){
                map.put("code", 1);
                map.put("msg", "验证码已失效，请重新获取");
                return map;
            }
            if(!authCode.equals(auth_code)){
                map.put("code", 1);
                map.put("msg", "验证码错误");
                return map;
            }
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("iccid", realIccid);
            paramMap.put("virtualIccid", iccid);
            paramMap.put("idCardNumber", identityCode);
            paramMap.put("name", name);
            paramMap.put("mobile", mobile);
            paramMap.put("operatorType", 1);
            JSONObject params = JSONObject.fromObject(paramMap);
            log.info("【"+realIccid+"】用户认证请求参数，request={}", params);
            String result = "";
            try{
                result = HttpRequestor.post(WXConstants.USERAUTH.getValue(), params.toString(), "UTF-8");
                log.info("【"+realIccid+"】用户认证请求返回，response={}", result);
            }catch (Exception e){
                log.error("【"+realIccid+"】用户认证请求异常，", e);
                map.put("code", 1);
                map.put("msg", "提交失败，请重试");
                return map;
            }
            if(StringUtils.isNotBlank(result)){
                JSONObject jb = JSONObject.fromObject(result);
                int code = jb.getInt("code");
                boolean succ = jb.getBoolean("success");
                if(succ&&code==200){
                    map.put("code", 0);
                    map.put("msg", "提交成功");
                    return map;
                }else{
                    map.put("code", 1);
                    map.put("msg", "提交失败，请重试");
                    return map;
                }
            }
        }catch (Exception e) {
            log.error("用户认证处理异常，", e);
            map.put("code", 1);
            map.put("msg", "提交失败，请重试");
            return map;
        }
        return null;
    }*/

    /**
     * 转到复制卡号页面
     * @param model
     * @return
     */
    @GetMapping("cope")
    public ModelAndView cope(Model model,String iccid, String openid) {
        log.info("调用boss获取实名链接接口，请求参数：iccid={}，openid={}", iccid,openid);
        Result response = restTemplate.getForObject(WXConstants.REAL_NAME_URL.getValue() + "?iccid="+iccid, Result.class);
        log.info("调用boss获取实名链接接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return new ModelAndView(new RedirectView(response.getMessage()));
        }
        return null;
    }

    /**
     * 跳转到移动实名提示页
     * @param model
     * @return
     */
    @GetMapping("realName")
    public ModelAndView realName(Model model) {
        return new ModelAndView(new RedirectView("https://ec.iot.10086.cn/rnforweb/notice/index.html"));
    }

    @GetMapping("getConfig")
    @ResponseBody
    public ScanCode getConfig(HttpServletRequest request) {
        String url = request.getParameter("url");
        ScanCode scanCode = new ScanCode();
        String accessToken = "";
        String ticket = "";
        try{
            ticket = (String) redisUtil.get("scanQRCode_ticket");
        }catch (Exception e){
            ticket = "";
        }
        if(StringUtils.isBlank(ticket)){
            try{
                accessToken = (String) redisUtil.get("scanQRCode_token");
            }catch (Exception e){
                accessToken = "";
            }
            if(StringUtils.isBlank(accessToken)){
                //向微信服务器发送get请求获取token
                String param= "grant_type=client_credential"+"&appid="+WXConstants.APPID.getValue()+"&secret="+WXConstants.APPSECRET.getValue();
                String token = HttpRequest.sendGet(WXConstants.SAOMATOKENURL.getValue(), param);
                JSONObject jsonObject = JSONObject.fromObject(token);
                accessToken = jsonObject.getString("access_token");
                int expires_in = jsonObject.getInt("expires_in");
                redisUtil.set("scanQRCode_token", accessToken, expires_in);
            }
            //access_token 采用http GET方式请求获得jsapi_ticket
            String jsapiTicketParam= "access_token="+accessToken+"&type=jsapi";
            String jsapi_ticket = HttpRequest.sendGet(WXConstants.JSAPITICKETURL.getValue(), jsapiTicketParam);
            JSONObject jsonObject1 = JSONObject.fromObject(jsapi_ticket);
            ticket = jsonObject1.getString("ticket");
            int expires_in = jsonObject1.getInt("expires_in");
            redisUtil.set("scanQRCode_ticket", ticket, expires_in);
        }

        String noncestr=WXPayUtil.generateNonceStr();
        String timestamp=System.currentTimeMillis()+"";
        String signe="jsapi_ticket="+ticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url+"";
        String sign = SignatureUtil.sha1Encrypt(signe);
        scanCode.setAppId(WXConstants.APPID.getValue());
        scanCode.setTimestamp(timestamp);
        scanCode.setNonceStr(noncestr);
        scanCode.setSignature(sign);
        return scanCode;
    }

}
