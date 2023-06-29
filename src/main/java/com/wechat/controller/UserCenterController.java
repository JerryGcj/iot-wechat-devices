package com.wechat.controller;

import com.wechat.config.WXConstants;
import com.wechat.model.Result;
import com.wechat.model.ScanCode;
import com.wechat.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 用户中心
 */
@CrossOrigin(origins = "*",maxAge = 3600)
@Controller
@Slf4j
@RequestMapping("")
public class UserCenterController {

    @Value("${realname.appid}")
    String appId;
    @Value("${realname.secret}")
    String secret;
    @Value("${realname.url}")
    String realNameUrl;
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
    public String userCenter(Model model, String code, String openid) {
        // 根据code获取openid
        if(StringUtils.isBlank(openid)) {
            openid = wxApiUtil.getOpenid(code);
        }
        model.addAttribute("openid", openid);

        try {
            log.info("调用后台查询套餐基本信息接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.USERCENTER_URL.getValue() + "?openId="+openid+"&operatorType=3", Result.class);
            log.info("调用后台查询套餐基本信息接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("user", response.getResult());
            } else {
                return "register";
            }
        } catch (Exception e){
            log.error("调用后台查询套餐基本信息接口异常",e);
        }
        return "userCenter";
    }

    /**
     * 跳转到联通用户中心页面
     * @param model
     * @param code
     * @param openid
     * @return
     */
    @GetMapping("unicomCenter")
    public String unicomCenter(Model model, String code, String openid, String iccid) {
        // 根据code获取openid
        if(StringUtils.isBlank(openid)) {
            openid = wxApiUtil.getOpenid(code);
        }
        model.addAttribute("openid", openid);

        try {
            log.info("调用后台查询套餐基本信息接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.USERCENTER_URL.getValue() + "?openId="+openid+"&iccid="+iccid+"&operatorType=2", Result.class);
            log.info("调用后台查询套餐基本信息接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("user", response.getResult());
            } else {
                return "unicomRegister";
            }
        } catch (Exception e){
            log.error("调用后台查询套餐基本信息接口异常",e);
        }
        return "unicomCenter";
    }

    /**
     * 跳转到钱包页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("wallet")
    public String wallet(Model model,String openid, String operatorType, String iccid) {
        model.addAttribute("openid", openid);
        model.addAttribute("operatorType", operatorType);
        try {
            log.info("调用后台查询套餐基本信息接口，请求参数：openid={}，iccid={}，operatorType={}",openid,iccid,operatorType);
            Result response = restTemplate.getForObject(WXConstants.USERCENTER_URL.getValue() + "?openId="+openid+"&iccid="+iccid+"&operatorType="+operatorType, Result.class);
            log.info("调用后台查询套餐基本信息接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("user", response.getResult());
            }
        }catch (Exception e){
            log.error("调用后台查询套餐基本信息接口异常",e);
        }
        if("1".equals(operatorType)){
            return "mobileWallet";
        }
        if("2".equals(operatorType)){
            return "wallet";
        }
        return null;
    }

    /**
     * 跳转到钱包页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("unicomWallet")
    public String unicomWallet(Model model,String openid, String operatorType, String iccid) {
        model.addAttribute("openid", openid);
        model.addAttribute("operatorType", operatorType);
        try {
            log.info("调用后台查询套餐基本信息接口，请求参数：openid={}，iccid={}，operatorType={}",openid,iccid,operatorType);
            Result response = restTemplate.getForObject(WXConstants.USERCENTER_URL.getValue() + "?openId="+openid+"&iccid="+iccid+"&operatorType="+operatorType, Result.class);
            log.info("调用后台查询套餐基本信息接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("user", response.getResult());
            }
        }catch (Exception e){
            log.error("调用后台查询套餐基本信息接口异常",e);
        }
        /*if("1".equals(operatorType)){
            return "mobileWallet";
        }
        if("2".equals(operatorType)){
            return "unicomWallet";
        }*/
        return "unicomWallet";
    }

    /**
     * 跳转到充值中心页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("voucherCenter")
    public String voucherCenter(Model model, String openid, String operatorType, String iccid) {
        model.addAttribute("openid", openid);
        try {
            log.info("调用后台查询充值商品接口，请求参数：iccid={}，operatorType={}",iccid, operatorType);
            Result response = restTemplate.getForObject(WXConstants.COMMODITYLIST_URL.getValue()+ "/" + operatorType+ "/" + iccid, Result.class);
            log.info("调用后台查询充值商品接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) response.getResult();
                List<HashMap<String, Object>> commodityList = result.stream().sorted(Comparator.comparing(o -> new BigDecimal(o.get("money").toString()))).collect(Collectors.toList());
                model.addAttribute("commodityList",commodityList);
            }
            log.info("调用后台查询套餐基本信息接口，请求参数：openid={}，iccid={}，operatorType={}",openid,iccid,operatorType);
            Result response2 = restTemplate.getForObject(WXConstants.USERCENTER_URL.getValue() + "?openId="+openid+"&iccid="+iccid+"&operatorType="+operatorType, Result.class);
            log.info("调用后台查询套餐基本信息接口，响应结果：{}", response2.toString());
            if (response2.isSuccess()) {
                model.addAttribute("user", response2.getResult());
            }
        }catch (Exception e) {
            log.error("调用后台查询充值商品接口异常",e);
        }
        if("1".equals(operatorType)){
            return "mobileVoucherCenter";
        }
        if("2".equals(operatorType)){
            return "voucherCenter";
        }
        return null;
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
        try {
            log.info("调用后台查询流量套餐商品接口，请求参数：openid={}，operatorType={}，iccid={}",openid,operatorType,iccid);
            Result response = restTemplate.getForObject(WXConstants.PACKAGELIST_URL.getValue()+"?operatorType="+operatorType+"&iccid="+iccid, Result.class);
            log.info("调用后台查询流量套餐商品接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                List<HashMap<String, String>> result = (List<HashMap<String, String>>) response.getResult();
                List<HashMap<String, String>> packageType = result.stream().filter(item -> item.get("packageType").equals("0")).collect(Collectors.toList());
                List<HashMap<String, String>> packageType1 = result.stream().filter(item -> item.get("packageType").equals("1")).collect(Collectors.toList());
                model.addAttribute("packageList",packageType);
                model.addAttribute("packageList1",packageType1);
            }
            log.info("调用后台查询套餐基本信息接口，请求参数：{}",openid);
            Result response2 = restTemplate.getForObject(WXConstants.USERCENTER_URL.getValue() + "?openId="+openid+"&operatorType="+operatorType, Result.class);
            log.info("调用后台查询套餐基本信息接口，响应结果：{}", response2.toString());
            if (response.isSuccess()) {
                model.addAttribute("user", response2.getResult());
            }
        }catch (Exception e) {
            log.error("调用后台查询流量套餐商品接口异常",e);
        }
        return "setMealCenter";
    }

    /**
     * 跳转到套餐购买页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("unicomMealCenter")
    public String unicomMealCenter(Model model,String openid, String operatorType, String iccid, String isNew) {
        model.addAttribute("openid", openid);
        model.addAttribute("isNew", isNew);
        try {
            log.info("调用后台查询流量套餐商品接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.PACKAGELIST_URL.getValue()+"?operatorType="+operatorType+"&iccid="+iccid, Result.class);
            log.info("调用后台查询流量套餐商品接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) response.getResult();
                List<HashMap<String, Object>> packageType = result.stream().sorted(Comparator.comparing(o -> new BigDecimal(o.get("salesPrice").toString()))).filter(item -> item.get("packageType").equals("0")).collect(Collectors.toList());
                List<HashMap<String, Object>> packageType1 = result.stream().sorted(Comparator.comparing(o -> new BigDecimal(o.get("salesPrice").toString()))).filter(item -> item.get("packageType").equals("1")).collect(Collectors.toList());
                model.addAttribute("packageList",packageType);
                model.addAttribute("packageList1",packageType1);
            }
            log.info("调用后台查询套餐基本信息接口，请求参数：{}",openid);
            Result response2 = restTemplate.getForObject(WXConstants.USERCENTER_URL.getValue() + "?openId="+openid+"&operatorType="+operatorType, Result.class);
            log.info("调用后台查询套餐基本信息接口，响应结果：{}", response2.toString());
            if (response.isSuccess()) {
                model.addAttribute("user", response2.getResult());
            }
        }catch (Exception e) {
            log.error("调用后台查询流量套餐商品接口异常",e);
        }
        return "unicomMealCenter";
    }

    /**
     * 跳转到我的优惠券页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("coupon")
    public String coupon(Model model, String openid) {
        model.addAttribute("openid", openid);
        return "coupon";
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
            log.info("调用后台查询充值记录接口，请求参数：openid={}，operatorType={}",openid,operatorType);
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
        return "billDetails";
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
        String operatorType = request.getParameter("operatorType");
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
        paramMap.put("operatorType", operatorType);
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
     * 查询优惠券
     * @param openid
     * @param type
     * @return
     */
    @ResponseBody
    @GetMapping("queryCoupon")
    public Result queryCoupon(String openid, String type) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("openId", openid);
        // 调用后台接口
        log.info("调用后台查询优惠券接口，请求参数：{}", openid);
        Result response = restTemplate.getForObject(WXConstants.QUERY_COUPON_URL.getValue() + "/" + openid + "/" + type, Result.class);
        log.info("调用后台查询优惠券接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok(response.getResult());
        } else {
            return Result.error(response.getMessage());
        }
    }

    /**
     * 充值下订单
     * @return
     */
    @ResponseBody
    @PostMapping("rechargeOrder")
    public Result order(HttpServletRequest request) {
        String openid = request.getParameter("openid");
        String productId = request.getParameter("productId");
        String price = request.getParameter("price");
        if (StringUtils.isBlank(openid) || StringUtils.isBlank(productId) || StringUtils.isBlank(price)) {
            return Result.error("参数不全，请输入完整参数");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appid", WXConstants.APPID.getValue());
        paramMap.put("openId", openid);
        paramMap.put("productId", productId);
        paramMap.put("money", price);
        // 调用后台接口
        log.info("调用后台充值下订单接口，请求参数：{}", paramMap);
        Result response = restTemplate.postForObject(WXConstants.SAVE_RECHARGE_ORDER_URL.getValue(), paramMap, Result.class);
        log.info("调用后台充值下订单接口，响应结果：{}", response.toString());
        if (!response.isSuccess()) {
            return Result.error(response.getMessage());
        }

        String description = price + "元充值";
        String ip = IPUtil.getIpAddress(request);
        price = new BigDecimal(price).multiply(new BigDecimal("100")).setScale(0).toString();
        // 调用JSAPI支付
        Map<String, String> resultMap = wxApiUtil.unifiedorder(openid, description, response.getResult().toString(), ip, price, WXConstants.CALLBACK.getValue());
        if (resultMap == null) {
            return Result.error("支付失败");
        }
        return Result.ok(resultMap);
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
        String productId = request.getParameter("productId");
        String price = request.getParameter("price");
        String packageName = request.getParameter("packageName");
        String operatorType = request.getParameter("operatorType");
        String createUser = request.getParameter("createUser");
        if (StringUtils.isBlank(openid) || StringUtils.isBlank(productId) || StringUtils.isBlank(price)) {
            return Result.error("参数不全，请输入完整参数");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("openId", openid);
        paramMap.put("appId", WXConstants.APPID.getValue());
        paramMap.put("mchId", WXConstants.MCHID.getValue());
        paramMap.put("operatorType", operatorType);
        paramMap.put("packageId", productId);
        paramMap.put("standardRates", price);
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
        String notifyUrl = "";
        if("2".equals(operatorType)){
            notifyUrl = WXConstants.UNICOMNOTIFYBACK.getValue();
        }
        if("3".equals(operatorType)){
            notifyUrl = WXConstants.NOTIFYBACK.getValue();
        }
        // 调用JSAPI支付
        Map<String, String> resultMap = wxApiUtil.unifiedorder(openid, description, response.getResult().toString(), ip, price, notifyUrl);
        if (resultMap == null) {
            return Result.error("支付失败");
        }
        return Result.ok(resultMap);
    }

    /**
     * 订购套餐
     * @return
     */
    @ResponseBody
    @PostMapping("packageOrder")
    public Result packageOrder(HttpServletRequest request) {
        String openid = request.getParameter("openid");
        String iccid = request.getParameter("iccid");
        String packageId = request.getParameter("packageId");
        String price = request.getParameter("price");
        String createUser = request.getParameter("createUser");
        String couponId = request.getParameter("couponId");
        String operatorType = request.getParameter("operatorType");
        if (StringUtils.isBlank(openid) || StringUtils.isBlank(packageId)) {
            return Result.error("参数不全，请输入完整参数");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("openId", openid);
        paramMap.put("iccid", iccid);
        paramMap.put("createUser", createUser);
        paramMap.put("standardRates", price);
        paramMap.put("packageId", packageId);
        paramMap.put("operatorType",operatorType);
        if (StringUtils.isNotBlank(couponId)) {
            paramMap.put("couponId", couponId);
        }
        // 调用后台接口
        log.info("调用后台订购套餐接口，请求参数：{}", paramMap);
        Result response = restTemplate.postForObject(WXConstants.MANUAL_ORDER_URL.getValue(), paramMap, Result.class);
        log.info("调用后台订购套餐接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok("购买成功");
        } else {
            return Result.error(response.getMessage());
        }
    }

    /**
     * 用户申请退款
     * @param openid
     * @return
     */
    @ResponseBody
    @PostMapping("refund")
    public Result refund(String openid,String packageId, String operatorType) {
        try {
            log.info("申请退款接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.REFUND_URL.getValue()+ "/"+openid+"/"+packageId+"/"+operatorType, Result.class);
            log.info("调用申请退款接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                return Result.ok(response.getMessage());
            } else {
                return Result.error(response.getMessage());
            }
        } catch (Exception e){
            log.error("调用申请退款接口异常",e);
            return Result.error("申请退款异常，请联系管理员");
        }
    }

    /**
     * 销户提现
     * @param openid
     * @return
     */
    @ResponseBody
    @PostMapping("closeAccount")
    public Result closeAccount(String openid, String iccid, String operatorType, String balance) {
        try {
            log.info("销户提现接口，请求参数：openid={}，iccid={}，operatorType{}，balance={}",openid,iccid,operatorType,balance);
            Result response = restTemplate.getForObject(WXConstants.CLOSE_ACCOUNT_WITHDRAWAL_URL.getValue()+ "/"+openid+ "/"+iccid+"/"+operatorType+"/"+balance, Result.class);
            log.info("销户提现接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                return Result.ok(response.getMessage());
            } else {
                return Result.error(response.getMessage());
            }
        } catch (Exception e){
            log.error("调用销户提现接口异常",e);
            return Result.error("申请异常，请联系管理员");
        }
    }

    /**
     * 预充用户申请套餐退款
     * @param openid
     * @return
     */
    @ResponseBody
    @PostMapping("newRefund")
    public Result newRefund(String openid,String packageId, String operatorType) {
        try {
            log.info("预充用户申请套餐退款接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.NEWREFUND_URL.getValue()+ "/"+openid+"/"+operatorType+"/"+packageId, Result.class);
            log.info("预充用户申请套餐退款接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                return Result.ok(response.getMessage());
            } else {
                return Result.error(response.getMessage());
            }
        } catch (Exception e){
            log.error("预充用户申请套餐退款接口异常",e);
            return Result.error("预充用户套餐申请退款异常，请联系管理员");
        }
    }

    /**
     * 用户取消退款
     * @param openid
     * @return
     */
    @ResponseBody
    @PostMapping("cancelRefund")
    public Result cancelRefund(String openid,String packageId, String operatorType) {
        try {
            log.info("取消退款接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.CANCELREFUND_URL.getValue()+ "/"+openid+"/"+packageId+"/"+operatorType, Result.class);
            log.info("调用取消退款接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                return Result.ok(response.getMessage());
            } else {
                return Result.error(response.getMessage());
            }
        } catch (Exception e){
            log.error("调用取消退款接口异常",e);
            return Result.error("取消退款异常，请联系管理员");
        }
    }

    @ResponseBody
    @GetMapping("ifCardExist")
    public Result ifCardExist(String iccid, String operatorType) {
        try {
            log.info("查询卡号是否存在接口，请求参数，iccid={}，operatorType={}",iccid,operatorType);
            Result response = restTemplate.getForObject(WXConstants.CARDEXIST_URL.getValue()+ "?iccid="+iccid+"&operatorType="+operatorType, Result.class);
            log.info("查询卡号是否存在接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                return Result.ok(response.getMessage());
            } else {
                return Result.error(response.getMessage());
            }
        } catch (Exception e){
            log.error("调用查询卡号是否存在接口异常",e);
            return Result.error("获取验证码异常，请联系管理员");
        }
    }

    /**
     * 移动实名认证
     * @param iccid
     * @param msisdn
     * @return
     */
    @GetMapping("userAuth")
    public ModelAndView userAuth(String iccid, String msisdn) {
        String signStr = "{\"appId\":\""+appId+"\",\"accessTime\":\""+System.currentTimeMillis()+"\",\"business\":\"\"}";
        String sign = AESUtil.encrypt(signStr, secret);
        //生成实名认证链接
        String url = realNameUrl + "?signature="+appId+"."+sign+"&iccid="+iccid+"&msisdn="+msisdn+"";
        System.out.println(url);
        return new ModelAndView(new RedirectView(url));
    }

    /**
     * 首页条码扫描
     * @param request
     * @return
     */
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
                String param= "grant_type=client_credential"+"&appid="+ WXConstants.APPID.getValue()+"&secret="+ WXConstants.APPSECRET.getValue();
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

        String noncestr= WXPayUtil.generateNonceStr();
        String timestamp=System.currentTimeMillis()+"";
        String signe="jsapi_ticket="+ticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url+"";
        String sign = SignatureUtil.sha1Encrypt(signe);
        scanCode.setAppId(WXConstants.APPID.getValue());
        scanCode.setTimestamp(timestamp);
        scanCode.setNonceStr(noncestr);
        scanCode.setSignature(sign);
        return scanCode;
    }

    /**
     * 获取问题标签并转到问题反馈页面
     * @param model
     * @param openid
     * @return
     */
    @GetMapping("question")
    public String question(Model model, String code, String openid) {
        if(StringUtils.isBlank(openid)) {
            openid = wxApiUtil.getOpenid(code);
        }
        try {
            log.info("调用后台查询问题标签接口，请求参数：{}",openid);
            Result response = restTemplate.getForObject(WXConstants.QUESTIONDETAILS_URL.getValue()+ "?openId="+openid, Result.class);
            log.info("调用后台查询问题标签接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("openid",openid);
                model.addAttribute("questionTagList",response.getResult());
            }
        }catch (Exception e) {
            log.error("调用后台查询问题标签接口异常",e);
        }
        return "faq";
    }

    @ResponseBody
    @PostMapping("saveProblem")
    public Result saveProblem(String openid, String advice) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("openId", openid);
        paramMap.put("content", advice);
        // 调用后台接口
        log.info("调用后台保存反馈的问题接口，请求参数：{}", paramMap);
        Result response = restTemplate.postForObject(WXConstants.SAVEPROBLEM_URL.getValue(), paramMap, Result.class);
        log.info("调用后台保存反馈的问题接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok(response.getMessage());
        } else {
            return Result.error(response.getMessage());
        }
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
    }

    /**
     * 查询所有的套餐详情
     * @param model
     * @param openid
     * @param iccid
     * @return
     */
    @GetMapping("queryFlow")
    public String queryFlow(Model model, String openid, String iccid, String operatorType) {
        try {
            log.info("调用后台查询所有套餐接口，请求参数：openid={}，iccid={}，operatorType={}",openid,iccid,operatorType);
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

    /**
     * 查询实名状态
     * @param iccid
     * @return
     */
    @ResponseBody
    @GetMapping("realNameStatus")
    public Result realNameStatus(String iccid, String operatorType) {
        // 调用后台接口
        log.info("调用后台查询实名状态接口，请求参数：{}", iccid);
        Result response = restTemplate.getForObject(WXConstants.REALNAME_URL.getValue()+"?iccid="+iccid+"&operatorType="+operatorType, Result.class);
        log.info("调用后台查询实名状态接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok(response.getResult());
        } else {
            return Result.error(response.getMessage());
        }
    }

    @GetMapping("defaultAccount")
    public String defaultAccount(Model model) {
        try {
            Result response = restTemplate.getForObject(WXConstants.DEFAULTACCOUNT_URL.getValue(), Result.class);
            log.info("调用后台获取默认公众号接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("accountInfo",response.getResult());
            }
        }catch (Exception e) {
            log.error("调用后台获取默认公众号接口异常",e);
        }
        return "qrcode";
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
        paramMap.put("operatorType", 2);
        // 调用后台接口
        log.info("调用后台用户实名登记接口，请求参数：{}", paramMap);
        Result response = restTemplate.postForObject(WXConstants.REALNAME_REGISTER_URL.getValue(), paramMap, Result.class);
        log.info("调用后台用户实名登记接口，响应结果：{}", response.toString());
        if (response.isSuccess()) {
            return Result.ok("提交成功");
        } else {
            return Result.error(response.getMessage());
        }
    }

    @GetMapping("toUnicomRegister")
    public String toUnicomRegister(Model model, String iccid, String openid) {
        model.addAttribute("iccid",iccid);
        model.addAttribute("openid",openid);
        return "realnameRegister";
    }

    @RequestMapping("unicomInternalQuery")
    public String special_register(Model model) {
        model.addAttribute("errorMsg", "");
        return "unicomInternalRegister";
    }

    @RequestMapping("unicomInternalCenter")
    public String unicomInternalCenter(Model model, String iccid) {
        model.addAttribute("iccid", iccid);
        try {
            log.info("调用后台查询套餐基本信息接口，请求参数：{}",iccid);
            Result response = restTemplate.getForObject(WXConstants.SPECIALUSER_URL.getValue() + "?iccid="+iccid+"&operatorType=2", Result.class);
            log.info("调用后台查询套餐基本信息接口，响应结果：{}", response.toString());
            if (response.isSuccess()) {
                model.addAttribute("user", response.getResult());
            } else {
                model.addAttribute("errorMsg", response.getMessage());
                return "unicomInternalRegister";
            }
        } catch (Exception e){
            log.error("调用后台查询套餐基本信息接口异常",e);
        }
        return "unicomInternalCenter";
    }
}
