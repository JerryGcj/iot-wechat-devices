package com.wechat.config;

public enum WXConstants {
    /**
     * 微信公众号Appid
     */
    APPID("wxc159f4c6309aad03"),

    /**
     * 微信公众号AppSecret
     */
    APPSECRET("924023aea676c5227121880ce75ad968"),

    /**
     * 微信支付密钥
     */
    KEY("CREsavRYAWSLSeXmK6YSy3e62J165wCv"),
    /**
     * 网页授权access_token地址
     */
    OPENIDURL("https://api.weixin.qq.com/sns/oauth2/access_token"),
    /**
     * 商户号
     */
    MCHID("1613718558"),

    /**
     * 统一下单地址
     */
    UNIFIEDORDERURL("https://api.mch.weixin.qq.com/pay/unifiedorder"),

    /**
     * 支付结果回调地址
     */
    CALLBACK("http://ufi.106rcs.cn/douyin/mobileApi/callback/notify"),

    NOTIFYBACK("http://ufi.106rcs.cn/douyin/mobileApi/callback/handlerNotify"),

    UNICOMNOTIFYBACK("http://ufi.106rcs.cn/douyin/mobileApi/callback/unicomNotify"),

    /**
     * 短信下发地址
     */
    SENDURL("http://47.99.227.82:8081/api/sms/send"),

    /**
     * 短信用户名
     */
    USERID("352253"),

    /**
     * 短信密钥
     */
    APIKEY("887dabbcc5564bdfae373168c99ddcb8"),

    /**
     * jsapi_ticketRUL
     */
    JSAPITICKETURL("https://api.weixin.qq.com/cgi-bin/ticket/getticket"),

    /**
     * 调用扫描url
     *
     */
    SAOMATOKENURL("https://api.weixin.qq.com/cgi-bin/token"),

    /**
     * 基础接口地址
     */
    BASE_URL("http://ufi.106rcs.cn/douyin/mobileApi/v1"),
    /**
     * 基础接口地址
     */
    //BASE_URL("http://127.0.0.1:8083/douyin/mobileApi/v1"),

    /**
     * 后台用户注册接口地址
     */
    USER_REGISTER_URL(BASE_URL.getValue() + "/userRegister"),

    /**
     * 后台查询优惠券接口地址
     */
    QUERY_COUPON_URL(BASE_URL.getValue() + "/getAllCoupon"),

    /**
     * 后台添加店铺接口地址
     */
    SAVE_STORE_URL(BASE_URL.getValue() + "/saveStoreInfo"),
    /**
     * 用户中心
     */
    USERCENTER_URL(BASE_URL.getValue() + "/queryActiveFlow"),

    SPECIALUSER_URL(BASE_URL.getValue() + "/querySpecialActiveFlow"),
    /**
     * 充值商品
     */
    COMMODITYLIST_URL(BASE_URL.getValue() + "/getProductList"),

    /**
     * 充值下订单接口地址
     */
    SAVE_RECHARGE_ORDER_URL(BASE_URL.getValue() + "/saveRechargeOrder"),

    /**
     * 直接买套餐下订单接口地址
     */
    SAVE_ORDER_URL(BASE_URL.getValue() + "/saveOrder"),

    /**
     * 充值下订单接口地址
     */
    ORDER_PACKAGE_URL(BASE_URL.getValue() + "/placeOrder"),

    MANUAL_ORDER_URL(BASE_URL.getValue() + "/manualPlaceOrder"),
    /**
     * 流量套餐
     */
    PACKAGELIST_URL(BASE_URL.getValue() + "/getPackageList"),
    /**
     * 充值记录
     */
    RECHARGERECORD_URL(BASE_URL.getValue() + "/queryRechargeRecord"),
    /**
     * 充值记录
     */
    BILLDETAILS_URL(BASE_URL.getValue() + "/queryConsumeRecord"),

    CARDORDER_URL(BASE_URL.getValue() + "/queryUnicomRecord"),
    /**
     * 提现金额未提现金额
     */
    MONEY_URL(BASE_URL.getValue() +"/queryMoney"),
    /**
     * 提现
     */
    WITHDRAWAL_URL(BASE_URL.getValue() +"/withdrawal"),
    /**
     * 商铺列表
     */
    STORELIST_URL(BASE_URL.getValue() +"/getStoreByOpenId"),

    /**
     * 根据openid获取用户信息
     */
    QUERY_USER_BY_OPENID_URL(BASE_URL.getValue() +"/queryByopenId"),

    /**
     * 根据手机号查询用户是否存在
     */
    QUERY_USER_BY_PHONE_URL(BASE_URL.getValue() +"/queryByPhone"),

    /**
     * 更新用户openid
     */
    UPDATE_OPENID_URL(BASE_URL.getValue() +"/updateOpenid"),

    /**
     * 用户申请退款
     */
    REFUND_URL(BASE_URL.getValue() +"/refundByOpenId"),

    CARDEXIST_URL(BASE_URL.getValue() +"/ifCardExist"),

    CLOSE_ACCOUNT_WITHDRAWAL_URL(BASE_URL.getValue() +"/balanceRefundByOpenId"),

    NEWREFUND_URL(BASE_URL.getValue() +"/unActivePackageRefundByOpenId"),

    /**
     * 用户取消退款
     */
    CANCELREFUND_URL(BASE_URL.getValue() +"/cancelRefund"),

    /**
     * 卡分配
     */
    CARDDISTRIBUTION(BASE_URL.getValue() +"/cardDistribution"),

    /**
     * 添加工作日志接口地址
     */
    ADD_JOB_LOG_URL(BASE_URL.getValue() + "/addJobLog"),

    /**
     * 获取问题标签接口地址
     */
    QUESTIONDETAILS_URL(BASE_URL.getValue() + "/questionTag"),

    /**
     * 保存反馈的问题接口地址
     */
    SAVEPROBLEM_URL(BASE_URL.getValue() + "/saveProblem"),

    /**
     * 查询是否有成功的订单接口
     */
    SUCCORDER_URL(BASE_URL.getValue() + "/succOrder"),

    /**
     * 查询所有套餐
     */
    QUERYFLOW_URL(BASE_URL.getValue() + "/queryFlow"),

    /**
     *用户实名登记接口地址
     */
    REALNAME_REGISTER_URL(BASE_URL.getValue() + "/realnameRegister"),

    /**
     * 卡备注
     */
    REMARK_URL(BASE_URL.getValue() + "/remarkCard"),

    QUERYBYOPENID_URL(BASE_URL.getValue() + "/getByopenId"),

    IOT_CARD_ORDER_URL("http://ufi.106rcs.cn/douyin/cardorder/iotCardOrder/order"),
    /**
     * 广电卡查询号码
     */
    GD_QUERY_ACCESS_NUMBER_URL("http://ufi.106rcs.cn/douyin/gd/mobileApi/getAccessNumber"),
    /**
     * 广电查询套餐
     */
    GD_QUERY_PACKAGE_LIST_URL("http://ufi.106rcs.cn/douyin/gd/mobileApi/getPackageList"),
    /**
     * 广电校验一证五号，地址，占号，创建订单
     */
    GD_Valid_Occupy_Submit_Order_URL("http://ufi.106rcs.cn/douyin/gd/mobileApi/gdValidAndOccupyAndSubmitOrder"),

    /**
     * 广电支付宝支付下单
     */
    GD_ALIPAY_ORDER_URL("http://ufi.106rcs.cn/douyin/gd/mobileApi/alipaySubmitOrder"),

    GD_QUERY_ORDER_URL("http://ufi.106rcs.cn/douyin/gd/mobileApi/queryOrder"),
    /**
     * 查询实名状态
     */
    REALNAME_URL(BASE_URL.getValue() + "/realNameStatus"),

    /** 微信公众号用户saveOrUpdate
     **/
    GZH_USER_SAVE_OR_UPDATE_URL("http://ufi.106rcs.cn/douyin/gzhuser/gzhUser/saveOrUpdate"),

    ELECTRON_CHANNEL_ORDER_REGULAR_URL("http://ufi.106rcs.cn/douyin/electronregular/electronChannelOrderRegular/order"),

    ELECTRON_CHANNEL_DY_ORDER_REGULAR_URL("http://ufi.106rcs.cn/douyin/electronregular/electronChannelOrderRegular/dYOrder"),

    ELECTRON_CHANNEL_BROADBAND_URL("http://ufi.106rcs.cn/douyin/electronregular/electronChannelOrderRegular/broadband"),

    ELECTRON_CHANNEL_ORDER_REGULAR_AUTH_URL("http://ufi.106rcs.cn/douyin/electronregular/electronChannelOrderRegular/authOrder"),
    ELECTRON_CHANNEL_ORDER_QUERY_URL("http://ufi.106rcs.cn/douyin/electronregular/electronChannelOrderRegular/queryOrder"),
    /**查询广电号码*/
    ELECTRON_CHANNEL_GD_QUERY_NUMBER("http://ufi.106rcs.cn/douyin/electronregular/electronChannelOrderRegular/queryGdNumber"),
    ELECTRON_CARD_ORDER_QUERY_URL("http://ufi.106rcs.cn/douyin/electronextension/electronCardExtension/query"),
    REAL_NAME_URL("http://ufi.106rcs.cn/douyin/realname/realNameSystem/queryUrl"),
    /**
     * 电信卡提单支付结果回调地址
     */
    ELECTRON_CALLBACK("http://ufi.106rcs.cn/douyin/electronOrderPayCallback/notify"),

    UNICOM_CALLBACK("http://ufi.106rcs.cn/douyin/iotCardOrderCallback/notify"),

    DEFAULTACCOUNT_URL(BASE_URL.getValue() + "/defaultAccount");


    private String value;

    private WXConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
