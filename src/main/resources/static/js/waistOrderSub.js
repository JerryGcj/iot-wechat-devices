new Vue(
    {
        el: '#app',
        data: {
            saleNo:'',
            agreeNameList : "",
            linkNo : "",
            agreeList : [],
            ifRead: false,
            ifFill: false,
            status: true,
            logistic: false,
            understandDesc:'',
            failCount:0,
            mktList : [],// 号码列表
            currentPage : 1,// 号码当前页数
            nbrMoral : "",// 号码蕴意
            isFour : "",
            accNbr:"", //主卡号码
            accNbrSub:"", //副卡号码
            shopid:"",
            timeCount: 5,
            prodName: "",
            prodDisplayPrice: "",
            prodDisplayPriceUnit: "",
            contact: "",
            custCertNo: "",
            selCity: "",
            areaCodeName:"",
            custAreaName: "", //寄送的省市区
            regionId: "",//号卡区域
            contactNumber: "",
            detailedAddr: "",
            remark: "",
            orderAttrList: [], //订单属性：采集订单来源等
            prodCfg: {},
            saleProd:{},
            orderSaveReqVo : {
                orderCustInfo: {
                    custSelectNbr: "",// 客
                }
            },
            coordinate: { //拖拽
                client: {},
                elePosition: {}
            },
            starPodcast : false,
            starPodcastUrl : "",
            orderConfirmVo : {
                prodName: "",
                prodDisplayPrice: "",
                prodDisplayPriceUnit: "",
                contact: "",
                custCertNo: "",
                contactNumber: "",
                detailedAddr: "",
                remark: "",
                //客户入网信息
                orderCustInfo : {
                    custAccNbr : "",	//入网号码
                    custName : "",		//入网姓名
                    custCertNbr : "",	//证件号码
                },
                //配送方式信息
                orderLogisticInfo :{
                    receiverName : "",	//收货人
                    receiverPhone : "",	//收货号码
                    receiverAddr : "",	//收货地址
                },
                //产品清单
                orderProdInfo : [],
                prodName : "",
                mtkName : "",
                accNbrType:"",  //0 主卡，1 副卡
                openNo:"",
                terminalInfo:""
            },
            terminalInfo:''
        },
        mounted: function () {
            _this = this;
            _this.terminalInfo = returnCitySN.cip + ' | ' + getBrowser()+ ' | ' + getOS();

            if(UOP.isNotNull(UOP.url("orderCode"))){ //返回填写页进行选号

                _this.qyrWaistOrder();

            }else{
                _this.infoDefault();
                _this.orderAttrList = setOrderFromData();
                _this.shopid = UOP.url("shopid");
                _this.staffCodeY = window.sessionStorage.getItem("staffCodeY");
                if ("TRUE" == _this.staffCodeY) {
                    console.log("查询到工号")
                } else {
                    console.log("查询不到工号")
                    //查询不到工号，无法下单
                }
            }

            _this.starPodcastUrl = window.sessionStorage.getItem("starPodcastUrl");
            const appCode = window.sessionStorage.getItem("starPodcastAppCode");
            if(UOP.isNotNull(_this.starPodcastUrl) && appCode === 'xbko2o'){
                _this.starPodcast = true;
            }

            _this.openNo = window.sessionStorage.getItem("starPodcastOpenNo");

            //获取明白卡信息
            /* _this.understandDesc = getUnderstandDesc();*/

            _this.linkNo = UOP.url("linkNo");
            _this.qryAgreement();
        },
        methods: {
            //拖拽
            touchstartHandle(dragBox, e) {
                let element = e.targetTouches[0]
                // 记录点击的坐标
                this.coordinate.client = {
                    x: element.clientX,
                    y: element.clientY
                }
                // 记录需要移动的元素坐标
                this.coordinate.elePosition.left = this.$refs[dragBox].offsetLeft
                this.coordinate.elePosition.top = this.$refs[dragBox].offsetTop
            },
            touchmoveHandle(dragBox, e) {
                let element = e.targetTouches[0]
                // 根据初始 client 位置计算移动距离(元素移动位置=元素初始位置+光标移动后的位置-光标点击时的初始位置)
                let x = this.coordinate.elePosition.left + (element.clientX - this.coordinate.client.x)
                let y = this.coordinate.elePosition.top + (element.clientY - this.coordinate.client.y)
                // 限制可移动距离，不超出可视区域
                x = x <= 0 ? 0 : x >= innerWidth - this.$refs[dragBox].offsetWidth ? innerWidth - this.$refs[dragBox].offsetWidth : x
                y = y <= 0 ? 0 : y >= innerHeight - this.$refs[dragBox].offsetHeight ? innerHeight - this.$refs[dragBox].offsetHeight : y
                // 移动当前元素
                // this.$refs[refName].style.left = x + 'px'
                this.$refs[dragBox].style.top = y + 'px'
            },
            // 初始化
            infoDefault: function () {
                var saleProd = JSON.parse(window.sessionStorage.getItem("saleProd"));
                if(UOP.isNotNull(saleProd)){
                    _this.prodCfg.prodName = saleProd.saleName;
                    _this.prodCfg.prodDisplayPrice = saleProd.salePrice /100 ;
                    _this.prodCfg.prodDisplayPriceUnit = saleProd.priceUnit;
                    _this.prodCfg.prodPrice = saleProd.payPrice/100;
                    _this.prodCfg.saleNo= saleProd.saleNo;
                    _this.prodCfg.prodCfgId = saleProd.prodCfgId; //腰部查询号码是需要通过集团编码调用接口

                }else{
                    _this.prodCfg = JSON.parse(window.localStorage.getItem("prodData"));

                }

                if(getQueryVariable("authCode") && getQueryVariable("authCode").indexOf('waistInfo') !=-1 && getQueryVariable("prodNbr")){
                    UOP.doPost(
                        "/uop-web/orderUtilityController/httpapi/getProdData",
                        {
                            "prodNbr" :getQueryVariable("prodNbr"),
                        },
                        function(ret) {
                            _this.prodCfg = ret.data;
                            window.localStorage.setItem("prodData",  JSON.stringify(ret.data));

                        },function(fail){},true)

                }

                //获取明白卡信息
                _this.understandDesc = getUnderstandDesc();
                /*     var prodCfg = JSON.parse(window.localStorage
                         .getItem("prodData"));
                     _this.orderSaveReqVo.orderProdInfo = prodCfg;*/

            },
            search: function (flag) {
                if (flag != null)
                    _this.currentPage++;
                else
                    _this.currentPage = 1;
                _this.selectNbr();
            },
            selectNbr: function () {
                var reqVo = {
                    "currentPage":_this.currentPage,
                    "prodCfgId":_this.prodCfg.prodCfgId,
                    "selCity": $("#sel_city").html(),
                    "nbrMoral": _this.nbrMoral,
                    "type":"1"
                    // "nbrMoral":_this.nbrMoral,

                    /*regionId: $("#sel_city").val(),
                    prodCfgId: _this.prodCfg.prodCfgId,
                    prodNo: _this.orderSaveReqVo.orderProdInfo.prodNo,
                    currentPage: _this.currentPage,
                    nbrMoral: _this.nbrMoral,
                    isFour: _this.isFour,*/
                }
                UOP.doPost("/uop-web/yborder/status/qryNumInstList",
                    reqVo, function (ret) {
                        _this.failCount = 0;
                        _this.mktList = ret.data;
                    }, function (ret) {
                        _this.mktList = null;
                        _this.currentPage = 1;
                        if (_this.failCount < 2) {
                            _this.failCount++;
                            _this.selectNbr();
                        }
                    })
                document.onkeydown = function (e) {
                    var ev = document.all ? window.event : e;
                    if (ev.keyCode == 13) {
                        _this.search();
                        return false;
                    }
                }
            },

            /**
             * 天翼类订单数据初始化
             */
            setTyData: function (tyOrderVo) {
                //客户入网信息
                _this.setOrderCustInfo(tyOrderVo.tel, tyOrderVo.name, tyOrderVo.identityId);
                //配送方式信息
                _this.setOrderLogisticInfo(tyOrderVo.consignee, tyOrderVo.userTel, tyOrderVo.detailedaddr);

            },
            /**
             * 设置客户入网信息
             * @param custAccNbr 入网号码
             * @param custName 入网姓名
             * @param custCertNbr 证件号码
             */
            setOrderCustInfo: function (custAccNbr, custName, custCertNbr) {
                _this.orderConfirmVo.orderCustInfo.custAccNbr = custAccNbr;
                _this.orderConfirmVo.orderCustInfo.custName = custName;
                _this.orderConfirmVo.orderCustInfo.custCertNbr = custCertNbr;
            },

            /**
             * 设置配送方式信息
             * @param receiverName 收货人
             * @param receiverPhone 收货号码
             * @param receiverAddr 收货地址
             */
            setOrderLogisticInfo: function (receiverName, receiverPhone, receiverAddr) {
                _this.orderConfirmVo.orderLogisticInfo.receiverAddr = receiverAddr;
                _this.orderConfirmVo.orderLogisticInfo.receiverName = receiverName;
                _this.orderConfirmVo.orderLogisticInfo.receiverPhone = receiverPhone;
            },
            setOrderConfirmInfo: function (orderConfirmInfo) {

                /*_this.orderConfirmVo.prodName = orderConfirmInfo.confirmProdName;
                _this.orderConfirmVo.mtkName = orderConfirmInfo.confirmMktName;
                _this.confirmType = orderConfirmInfo.confirmType;
                //天翼
                _this.setTyData(orderConfirmInfo.orderInfo);*/
            },
            divDisabled: function (id, disabled, disableStyle) {
                var attr = "auto";
                if (disabled) {
                    attr = "none";
                }
                //设置样式
                if (UOP.isNotNull(disableStyle)) {
                    var btn = $("#" + id);
                    btn[0].className = disableStyle;
                }
                //设置点击事件，达到置灰效果
                $("#" + id).css("pointer-events", attr);
            },
            confirmOrder: function () {
                // _this.divDisabled("confirmOrderBtn", true);
                /* var orderCustInfo = _this.orderSaveReqVo.orderCustInfo;
                 var orderProdInfo = _this.orderSaveReqVo.orderProdInfo;*/
                var tyOrderVo = {

                    contact : _this.contact,// 用户姓名
                    contactNumber : _this.contactNumber,// 收货人联系号码
                    detailedaddr : _this.custAreaName + ' '+_this.detailedAddr,// 收获地址
                    remark : _this.remark,// 客戶备注
                    postcode:"100000",
                    prodNo :  _this.prodCfg.prodNo,// 产品Id
                    saleNo : _this.prodCfg.saleNo, //2022-3-2 新增商品编码
                    code : window.sessionStorage.getItem("code"),// 场景编码
                    selCity : $("#sel_city").val(),//业务号码区域
                    accNbr : _this.accNbr,
                    subAccNbr:_this.accNbrSub,
                    areaCodeName : $("#sel_city").html(),
                    custCertNo:_this.custCertNo,
                    shopid:_this.shopid,
                    orderDevAccNbr : window.sessionStorage.getItem("orderDevAccNbr"),
                    orderAttrList: _this.orderAttrList,
                    orderConfirmInfo : window.sessionStorage.getItem("orderConfirmInfo"),
                    openNo : _this.openNo

                }
                UOP.userTrail("订单填写（明白卡）", "submit", "同意协议并办理", _this.prodCfg.prodNo, window.sessionStorage.getItem("code"));
                UOP.doPost("/uop-web/yborder/status/order", JSON.stringify(tyOrderVo),
                    function (ret) {
                        console.log(ret.data);
                        layer.msg("订购成功");
                        /* var obj = JSON.parse(ret.data.resultMsg);
                         console.log(obj.biz.orderId);*/
                        /* window.sessionStorage.setItem("orderCode", ret.data.orderCode);*/
                        window.location.href = '/uop-web/html/order/pre_tpl/preference_success.html?code=' + ret.data;
                    }, function (ret) {
                        console.log(ret.data);
                        _this.hideBusiConfirm();
                        layer.msg(ret.resultMsg);
                    }, false, "application/json")

            },

            orderSaveNew: function () {
                UOP.userTrail("订单填写（明白卡）", "sureInfo", "订单提交", _this.prodCfg.prodNo, window.sessionStorage.getItem("code"));
                if(!_this.status){
                    _this.accNbrSubmit();
                }else{
                    /*trk_wap_jt.trackAction('EVENT_000873', {
                        'c9': '订单提交',
                        'btn_location': '5G升级包-底部确认按钮-填写信息页-1_1'
                    });*/
                    if (!_this.checkInfo()) {
                        return false;
                    }
                    var tyOrderVo = {
                        contact : _this.contact,// 用户姓名
                        contactNumber : _this.contactNumber,// 收货人联系号码
                        detailedaddr : _this.custAreaName
                            + ' '+_this.detailedAddr,// 收获地址
                        remark : _this.remark,// 客戶备注
                        postcode:"100000",
                        prodNo : _this.prodCfg.prodNo,// 产品Id
                        code : window.sessionStorage.getItem("code"),// 场景编码
                        accNbr : _this.accNbr,
                        subAccNbr:_this.accNbrSub,
                        selCity : $("#sel_city").html(),//业务号码区域
                        custCertNo:_this.custCertNo,
                        shopid:_this.shopid,
                        orderDevAccNbr : window.sessionStorage.getItem("orderDevAccNbr"),
                        orderAttrList: _this.orderAttrList,
                        orderConfirmInfo: "" //业务确认信息


                    }

                    var orderConfirmInfo = {
                        orderInfo: tyOrderVo,
                        confirmProdName: _this.prodCfg.prodName,
                        confirmMktName: _this.prodCfg.prodName,
                        confirmType: "1",
                        terminalInfo:_this.terminalInfo
                    }
                    window.sessionStorage.setItem("orderConfirmInfo",JSON.stringify(orderConfirmInfo));
                    //设置确认信息
                    _this.setOrderConfirmInfo(orderConfirmInfo);
                    $("#busiConfirm").show();
                }


            },

            checkInfo: function () {
                _this.regionId = $("#sel_city").val();
                _this.custAreaName = $("#sel_city1").html();
                if (UOP.isNull(_this.contact)) {
                    layer.msg("请输入入网姓名！");
                    return;
                }
                if (!UOP.verifyName(_this.contact)) {
                    return;
                }
                if (UOP.isNull(_this.custCertNo)) {
                    layer.msg("请输入身份证号！");
                    return;
                }
                if (!UOP.checkCard(_this.custCertNo)) {
                    return;
                }

                if (UOP.isNull(_this.regionId)) {
                    layer.msg("请选择所在地区！");
                    return;
                }
                if (UOP.isNull(_this.accNbr)) {
                    layer.msg("请选择号码！");
                    return;
                }
                if(UOP.isNotNull(window.sessionStorage.getItem("isPreSub")) && "3"== window.sessionStorage.getItem("isPreSub") ){
                    if (UOP.isNull(_this.accNbrSub)) {
                        layer.msg("请选择副卡号码！");
                        return;
                    }
                }

                if (!UOP.checkisCellPhoneNum(_this.contactNumber)) {
                    return false;
                }

                if ("请选择" === _this.custAreaName) {
                    layer.msg("请选择寄送地址的城市！");
                    return;
                }
                if (UOP.isNull(_this.detailedAddr)) {
                    layer.msg("请输入详细地址！");
                    return;
                }
                if (!$('#agreementIcon').hasClass("on")) {
                    layer.msg("您还未认真阅读并同意服务协议！");
                    return false;
                }
                return true;
            },


            /**
             * 展示或关闭物流填写弹窗；
             * show为true时展示，为false时关闭，关闭前对物流信息做校验
             * @param show
             */
            showAndFillInLogistics: function (show) {
                if(!_this.status){
                    return ;
                }
                if (show) {
                    $("#addressPage").show();
                    _this.logistic = true;
                    $("#orderInfo").hide();
                    $("#orderBtn").hide();
                } else {
                    if (!_this.checkLogisticsInfo()) {
                        return;
                    }
                    $("#addressPage").hide();
                    _this.logistic = false;
                    $("#orderInfo").show();
                    $("#orderBtn").show();
                    _this.ifFill = true;
                }
            },
            hideBusiConfirm: function () {
                $("#busiConfirm").hide();
            },
            /**
             * 展示或关闭选号弹框
             * @param show 展示或关闭
             * @param confirm 是否需要选号
             * @returns {boolean}
             */
            showSelectNumber: function (show, confirm) {
                if(show){
                    UOP.userTrail("订单填写（明白卡）", "checkOnAgree", "勾选协议", _this.prodCfg.prodNo, window.sessionStorage.getItem("code"));
                    _this.accNbrType = "0";
                }
                if (show) {
                    if ($("#sel_city").val() == null
                        || $("#sel_city").val() == "") {
                        layer.msg("请选择号码归属");
                        return false;
                    }
                    $("#selectNumber").show();
                    _this.search(null);
                } else {
                    if (confirm) {
                        if (!$(".numDiv").hasClass("on")) {
                            layer.msg("请选择号码");
                            return false;
                        }
                    }
                    $("#selectNumber").hide();
                }
            },
            /**
             * 副卡选号
             * @param show
             * @param confirm
             * @returns {boolean}
             */
            showSelectNumberSub: function (show, confirm,type) {
                if(UOP.isNull(_this.accNbr)){
                    layer.msg("请先选择主卡号码");
                    return ;
                }
                if(show){
                    _this.accNbrType = "1";
                }
                if (show) {
                    if ($("#sel_city").val() == null
                        || $("#sel_city").val() == "") {
                        layer.msg("请选择号码归属");
                        return false;
                    }
                    $("#selectNumber").show();
                    _this.search(null);
                } else {
                    if (confirm) {
                        if (!$(".numDiv").hasClass("on")) {
                            layer.msg("请选择号码");
                            return false;
                        }
                    }
                    $("#selectNumber").hide();
                }
            },


            /**
             * 校验物流填写信息
             * @returns {boolean}
             */
            checkLogisticsInfo: function () {

                _this.custAreaName = $("#sel_city1").html();

                if (!UOP.checkisCellPhoneNum(_this.contactNumber)) {
                    return false;
                }
                if ("请选择" == _this.custAreaName) {
                    layer.msg("请选择寄送地址的城市！");
                    return;
                }
                if (UOP.isNull(_this.detailedAddr)) {
                    layer.msg("请输入详细地址！");
                    return;
                }
                return true;
            },

            /**
             * 选号时号码选中效果和回填
             * @param nbr
             */
            sureNumber: function (nbr) {
                $(".numDiv").removeClass("on");
                $("#nbr" + nbr.mdn).addClass("on");
                if( _this.accNbrType == "0"){ //设置主卡
                    _this.accNbr = nbr.mdn;
                    $("#phoneNum").val( _this.accNbr);
                }else{  //设置副卡
                    _this.accNbrSub = nbr.mdn;
                    $("#phoneNumSub").val( _this.accNbrSub);
                }

            },

            /**
             * 协议名称tab区的选中切换效果
             * @param agreeTabId
             * @param agreeId
             */
            changeAgree: function (agreeTabId, agreeId) {
                $(".agreeList").removeClass("on");
                $("#" + agreeTabId).addClass("on");
                $(".agreeDiv").hide();
                $("#" + agreeId).show();
            },
            /**
             * 展示或关闭业务协议弹框，读秒并展示业务协议
             * @param show
             */
            showServiceAgreement: function (show) {
                if (show) {
                    _this.timeCount = 5;
                    var readBtn = document.getElementById("readBtn");
                    var agreeBtn = document.getElementById("agreeBtn");
                    agreeBtn.style.display = "none"
                    readBtn.style.display = "block"
                    $("#serviceAgreement").show();
                    _this.countdown();
                } else {
                    if (_this.timeCount != 0) {
                        layer.msg("请先认真阅读相关协议~");
                        return;
                    }
                    $("#serviceAgreement").hide();
                }
            },
            /**
             * 业务协议读秒展示
             */
            countdown: function () {
                var readBtn = document.getElementById("readBtn");
                var agreeBtn = document.getElementById("agreeBtn");
                var a = setInterval(timeFn, 1000);

                function timeFn() {
                    _this.timeCount--;
                    if (_this.timeCount == 0) {
                        clearInterval(a);
                        readBtn.style.display = "none"
                        agreeBtn.style.display = "block"
                        _this.ifRead = true;
                    }
                }
            },
            /**
             * 提示是否阅读协议
             */
            isReadAgreement: function () {
                if (!_this.ifRead || !$('#agreementIcon').hasClass("on")) {
                    layer.msg("请点击阅读相关协议~");
                }
            },
            isNotNull: function (object) {
                return UOP.isNotNull(object);
            },

            accNbrSubmit:function (){
                if(UOP.isNull(_this.accNbr)){
                    layer.msg("请重新选号！");
                    return ;
                }
                var data = {
                    orderCode : UOP.url("orderCode"),
                    accNbr : _this.accNbr
                }
                UOP.doPost("/uop-web/yborder/status/updateOrderInfo", data,
                    function (ret) {
                        console.log(ret.data);
                        //跳转成功页
                        window.location.href = '/uop-web/html/order/pre_tpl/preference_success.html?code=' +UOP.url("orderCode");
                    }, function (ret) {
                        console.log(ret.data);
                        if(ret.resultCode == '2' ){  //订单未支付
                            window.sessionStorage.setItem("orderInfo",JSON.stringify(ret.data));
                            window.location.href="/uop-web/html/order/orderPay/charting_waistOrder.html";
                        }else if(ret.resultCode == '0'){ //号码被占用问题
                            var Str = ret.resultMsg;
                            layer.confirm(Str, {
                                btn: ['确定','取消'] // 按钮
                            }, function(index){
                                console.log("确定")
                                layer.close(index);
                                $("#phoneNum").html("请重新选号");
                                $("#phoneNum").val("请重新选号");
                                _this.accNbr = "";
                            }, function(index){
                                //确定
                                console.log("取消")
                                layer.close(index);
                                layer.msg("稍后为您取消订单");  //用户不想继续选号
                                _this.cancelOrder();
                            });
                        }else if(ret.resultCode == '1'){ //集团报错
                            layer.msg(ret.resultMsg+"。稍后为您取消订单");
                            _this.cancelOrder();
                        }else{
                            layer.msg(ret.resultMsg);

                        }

                        //若是号码被占用，则继续选号？？？？用户不选，订单取消，待退款
                    }, )
            },
            cancelOrder : function (){
                var data = {
                    orderCode : UOP.url("orderCode")
                }
                UOP.doPost("/uop-web/yborder/status/cancelOrder", data,
                    function (ret) {  //订单取消成功，跳转订购失败页面
                        console.log(ret.data);
                        window.location.href = "/uop-web/html/order/pre_tpl/waistError.html";
                    }, function (ret) {
                        console.log(ret.data);
                    }, false)
            },
            qyrWaistOrder:function (){
                var data = {
                    orderCode : UOP.url("orderCode")
                }
                UOP.doPost("/uop-web/yborder/status/qryWaistOrder", data,
                    function (ret) {
                        console.log(ret.data);

                        _this.contact = ret.data.contact;
                        _this.custCertNo = ret.data.custCertNo;
                        _this.contactNumber = ret.data.contactNumber;
                        _this.detailedAddr = ret.data.detailedaddr;
                        _this.prodNbr = ret.data.prodNo;
                        //_this.selCity = tyOrderVo.selCity;
                        _this.remark = ret.data.remark;
                        $("#sel_city").addClass("noClick");

                        $("#sel_city").html(ret.data.areaCodeName);
                        $("#sel_city").val(ret.data.selCity);

                        _this.status = false;
                        _this.ifRead = true;

                        $("#addressPage").hide();
                        $("#orderInfo").show();
                        $("#orderBtn").show();
                        _this.ifFill = true;

                        _this.getProdData();

                    }, function (ret) {
                        console.log(ret.data);
                    }, false)
            },
            getProdData: function () {
                let
                    prodNbr = {
                        "prodNbr": _this.prodNbr
                    };
                UOP
                    .doPost(
                        "/uop-web/orderUtilityController/httpapi/getProdData",
                        prodNbr,
                        function (ret) {
                            _this.prodCfg = ret.data;

                        }, function (fail) {
                        }, false)

            },
            closeLogistic : function (){
                _this.logistic = false;
            },
            openLogistic : function (){
                _this.logistic = true;
            },
            qryAgreement : function (){
                UOP.doPost("/uop-web/orderUtilityController/qryAgreement",
                    {'linkNo' : UOP.url("linkNo")},
                    function (ret){
                        _this.agreeList = ret.data;
                        for (var i in _this.agreeList) {
                            var agree = _this.agreeList[i];
                            _this.agreeNameList = _this.agreeNameList +"《" + agree.agreeName + "》";
                        }

                    },function (ret){
                        console.log(ret);
                    });

            },

        }

    })
