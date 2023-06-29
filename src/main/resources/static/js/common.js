/**
 * 统一处理 js 面向对象
 *
 * @author LAIYONGMIN@FFCS.CN
 */

var UOP = {
    "isBack": true,
    "auto": false,
    "doGet": function (url, data, success, fail, isAsync) {
        $.ajax({
            url: url,
            type: 'get',
            dataType: 'json',
            data: data,
            timeout: 60000,
            async: isAsync ? false : true
            // 默认同步
        }).done(
            function (ret) {
                if (ret && ret.result
                    && ret.result === 'TRUE') {
                    if (ret.promptType === 'ALERT') {
                        layer.alert(ret.resultMsg, {
                            skin: 'layui-layer-molv', // 样式类名
                            closeBtn: 0
                        }, function (index) {
                            layer.close(index);
                            if ($.isFunction(success)) {
                                success(ret);
                            }
                        });
                    } else if (ret.promptType === 'CONSOLE') {
                        if ($.isFunction(success)) {
                            success(ret);
                        }
                    } else {
                        if ($.isFunction(success)) {
                            success(ret);
                        }
                    }
                } else {
                    if ($.isFunction(fail)) {
                        fail(ret);
                    }
                }

            }).fail(function (data) {
            layer.msg('网络异常，请刷新试一试!');
        }).always(function (output, status, xhr) {

        });
    },
    // isAsync true 异步；false 同步
    "doPost": function (url, data, success, fail, isAsync, contentType) {
        $.ajax({
            url: url,
            type: 'post',
            dataType: 'json',
            contentType: contentType != null ? contentType : "application/x-www-form-urlencoded; charset=UTF-8",
            data: data,
            timeout: 60000,
            async: isAsync ? false : true
            // 默认同步
        }).done(
            function (ret) {
                if (ret && ret.uopRespVo) {
                    ret = ret.uopRespVo;
                }
                if (ret && ret.result
                    && ret.result === 'TRUE') {
                    if (ret.promptType === 'ALERT') {
                        layer.alert(ret.resultMsg, {
                            skin: 'layui-layer-molv', // 样式类名
                            closeBtn: 0
                        }, function (index) {
                            layer.close(index);
                            if ($.isFunction(success)) {
                                success(ret);
                            }
                        });
                    } else if (ret.promptType === 'CONSOLE') {
                        if ($.isFunction(success)) {
                            success(ret);
                        }
                    } else {
                        if ($.isFunction(success)) {
                            success(ret);
                        }
                    }
                } else {
                    if ($.isFunction(fail)) {
                        fail(ret);
                    }
                }
            }).fail(function (data) {
            layer.msg('网络异常，请刷新试一试!');
        }).always(function (output, status, xhr) {

        });
    },

    // 打开窗体
    openDefWin: function (title, url) {
        return UOP.openWin(title, url, 850, 650);
    },
    // 打开窗体
    openWin: function (title, url, width, height) {
        var winId = layer.open({
            type: 2,
            title: title,
            shadeClose: true,
            shade: false,
            maxmin: true, // 开启最大化最小化按钮
            area: [width + 'px', height + 'px'],
            content: contextPath + "/" + url
        });
        return winId;
    },
    // 验证对象是否为空对象
    isNull: function (param) {
        return $.isEmptyObject(param);
    },
    isNotNull: function (param) {
        return !$.isEmptyObject(param);
    },
    // select 渲染
    renderSelect: function (form, $this, data, sel) {
        if (data && $this && form) {
            var html = "<option value=''></option>";
            $.each(data, function (index, value) {
                if (sel && sel === value.attrValue) {
                    html += "<option selected value='" + value.attrValue + "'>"
                        + value.attrValueName + "</option>";
                } else {
                    html += "<option value='" + value.attrValue + "'>"
                        + value.attrValueName + "</option>";
                }
            });
            $this.append(html);
            form.render('select');
        }
    },
    url: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return unescape(r[2]);
        }
        return null;
    },
    //服务协议
    service: function () {
        layer.open({
            type: 2,
            title: "服务协议",
            closeBtn: 1,
            area: ['90%', '500px'],
            shadeClose: true,
            content: 'agreement.html'
        });
    },
    checkCard: function (card) {
        // 是否为空
        if (card === '') {
            layer.msg('请输入身份证号，身份证号不能为空');
            return false;
        }
        // 校验长度，类型
        if (isCardNo(card) === false) {
            layer.msg('您输入的身份证号码不正确，请重新输入');
            return false;
        }
        // 检查省份
        if (checkProvince(card) === false) {
            layer.msg('您输入的身份证号码不正确,请重新输入');
            return false;
        }
        // 校验生日
        if (checkBirthday(card) === false) {
            layer.msg('您输入的身份证号码生日不正确,请重新输入');
            return false;
        }
        // 检验位的检测
        if (checkParity(card) === false) {
            layer.msg('您的身份证校验位不正确,请重新输入');
            return false;
        }
        return true;
    },

    checkCardAge: function (num) {
        if (num.length == 15) {
            num = changeFivteenToEighteen(num);
        }
        var bstr = num.substring(6, 14)
        var now = new Date();
        var bir = new Date(bstr.substring(0, 4), bstr.substring(4, 6), bstr.substring(6, 8));
        var agen = now - bir;
        var age = Math.round(agen / (365 * 24 * 60 * 60 * 1000));
        return true;
        /*if( age<16 || age>=60){
            layer.msg('您暂时不符合线上办理资格,可到线下营业厅办理相关业务');
            return false;
        }else{
            return  true;
        }*/
    },
    //2019/1/31 新增    zhuangxf
    checkisCellPhoneNum: function (num) {
        if (num === '') {
            layer.msg('请输入手机号码，手机号码不能为空');
            return false;
        }
        if (isCellPhoneNo(num) === false) {
            layer.msg('您输入的手机号码不正确，请重新输入');
            return false;
        }
        return true;
    },
    loadSweepCode: function (jsApiList, successFunc) {
        var url = location.href.split('#')[0];
        UOP.doPost("/uop-web/orderUtilityController/getWxConfig", {
            url_page: url
        }, function (result) {
            wx.config({
                // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                debug: false,
                // 必填，公众号的唯一标识
                appId: result.data.appid,
                // 必填，生成签名的时间戳
                timestamp: result.data.timestamp + "",
                // 必填，生成签名的随机串
                nonceStr: result.data.noncestr,
                // 必填，签名，见附录1
                signature: result.data.signature,
                // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                jsApiList: jsApiList
            });
        }, function (ret) {
        }, true, null)

        wx.error(function (res) {
            layer.alert("出错了：" + res.errMsg);//这个地方的好处就是wx.config配置错误，会弹出窗口哪里错误，然后根据微信文档查询即可。
        });

        wx.ready(function () {
            wx.checkJsApi({
                jsApiList: ['scanQRCode'],
                success: function (res) {

                }
            });

            //点击按钮扫描二维码/条形码
            document.querySelector('#scanQRCode').onclick = function () {
                wx.scanQRCode({
                    needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
                    scanType: ["barCode"], // 可以指定扫二维码还是一维码，默认二者都有
                    success: function (res) {
                        successFunc(res);
                    }
                });
            };
        });
    },
    isClient: function () {
        var u = navigator.userAgent.toLocaleLowerCase(); //获取页面来源并转换成小写
        var isAndroid = u.indexOf("android") > -1 && u.indexOf("ctclient") > -1; //android终端
        var isiOS = u.indexOf("ios") > -1 && u.indexOf("ctclient") > -1; //ios终端
        if (isAndroid) {
            return 1; //安卓
        } else if (isiOS) {
            return 2;  //ios
        } else {
            return 0; // wap
        }
    },

    verifyName: function (name) {
        if (name.indexOf("先生") != -1 || name.indexOf("女士") != -1 || name.indexOf("小姐") != -1) {
            layer.msg('输入正确规范名字');
            return false;
        } else {
            return true;
        }
    },
    getRelativePath: function () {
        var pathName = document.location.pathname;
        var index = pathName.substr(1).indexOf("/");
        var path = pathName.substr(0, index + 1) + "/";
        return path;
    },
    userTrail: function (pageTitle, eleCode, eleName, prodNbr, busiScenarioCode) {
        UOP.doPost(this.getRelativePath() + "pageActionTrailController/writeUserTrail", {
            pageTitle: pageTitle,
            eleCode: eleCode,
            eleName: eleName,
            prodNbr: prodNbr,
            busiScenarioCode: busiScenarioCode,
            pageAddr: window.location.href
        }, function (ret) {
        }, function (ret) {
            /*alert(ret.resultMsg);*/
        }, true)
    },
    userSmsTrail: function (pageTitle, eleCode, eleName, prodNbr, busiScenarioCode,accNbr,msgCode) {
        UOP.doPost(this.getRelativePath() + "pageActionTrailController/writeUserTrail", {
            pageTitle: pageTitle,
            eleCode: eleCode,
            eleName: eleName,
            prodNbr: prodNbr,
            busiScenarioCode: busiScenarioCode,
            accNbr: accNbr,
            msgCode: msgCode,
            pageAddr: window.location.href
        }, function (ret) {
        }, function (ret) {
            /*alert(ret.resultMsg);*/
        }, true)
    },
    popBack: function () {
        this.isBack = false;
        if (window.history && window.history.pushState && window.history.state) {
            history.replaceState({}, null, document.URL);
        } else {
            history.pushState({}, null, document.URL);
        }
    },

}
// form序列化后的对象转JSON对象
$.fn.serializeJson = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};
// 统一设置加载样式
$.ajaxSetup({
    layerIndex: -1,
    beforeSend: function () {
        this.layerIndex = layer.load(1, {
            offset: ['50%', "50%"],
            shade: [0.5, '#393D49']
        });
    },
    complete: function () {
        layer.close(this.layerIndex);
    },
    error: function () {
        layer.msg('网络异常，请刷新试一试!');
    }
});

var vcity = {
    11: "北京",
    12: "天津",
    13: "河北",
    14: "山西",
    15: "内蒙古",
    21: "辽宁",
    22: "吉林",
    23: "黑龙江",
    31: "上海",
    32: "江苏",
    33: "浙江",
    34: "安徽",
    35: "福建",
    36: "江西",
    37: "山东",
    41: "河南",
    42: "湖北",
    43: "湖南",
    44: "广东",
    45: "广西",
    46: "海南",
    50: "重庆",
    51: "四川",
    52: "贵州",
    53: "云南",
    54: "西藏",
    61: "陕西",
    62: "甘肃",
    63: "青海",
    64: "宁夏",
    65: "新疆",
    71: "台湾",
    81: "香港",
    82: "澳门",
    91: "国外"
};


// 检查号码是否符合规范，包括长度，类型
isCardNo = function (card) {
    // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
    var reg = /(^\d{15}$)|(^\d{17}(\d|X)$)/;
    if (reg.test(card) === false) {
        return false;
    }

    return true;
};

// 取身份证前两位,校验省份
checkProvince = function (card) {
    var province = card.substr(0, 2);
    if (vcity[province] == undefined) {
        return false;
    }
    return true;
};

// 检查生日是否正确
checkBirthday = function (card) {
    var len = card.length;
    // 身份证15位时，次序为省（3位）市（3位）年（2位）月（2位）日（2位）校验位（3位），皆为数字
    if (len == '15') {
        var re_fifteen = /^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/;
        var arr_data = card.match(re_fifteen);
        var year = arr_data[2];
        var month = arr_data[3];
        var day = arr_data[4];
        var birthday = new Date('19' + year + '/' + month + '/' + day);
        return verifyBirthday('19' + year, month, day, birthday);
    }
    // 身份证18位时，次序为省（3位）市（3位）年（4位）月（2位）日（2位）校验位（4位），校验位末尾可能为X
    if (len == '18') {
        var re_eighteen = /^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/;
        var arr_data = card.match(re_eighteen);
        var year = arr_data[2];
        var month = arr_data[3];
        var day = arr_data[4];
        var birthday = new Date(year + '/' + month + '/' + day);
        return verifyBirthday(year, month, day, birthday);
    }
    return false;
};

// 校验日期
verifyBirthday = function (year, month, day, birthday) {
    var now = new Date();
    var now_year = now.getFullYear();
    // 年月日是否合理
    if (birthday.getFullYear() == year && (birthday.getMonth() + 1) == month
        && birthday.getDate() == day) {
        // 判断年份的范围（3岁到100岁之间)
        var time = now_year - year;
        if (time >= 3 && time <= 100) {
            return true;
        }
        return false;
    }
    return false;
};

// 校验位的检测
checkParity = function (card) {
    // 15位转18位
    card = changeFivteenToEighteen(card);
    var len = card.length;
    if (len == '18') {
        var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8,
            4, 2
        );
        var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3',
            '2'
        );
        var cardTemp = 0, i, valnum;
        for (i = 0; i < 17; i++) {
            cardTemp += card.substr(i, 1) * arrInt[i];
        }
        valnum = arrCh[cardTemp % 11];
        if (valnum == card.substr(17, 1)) {
            return true;
        }
        return false;
    }
    return false;
};

// 15位转18位身份证号
changeFivteenToEighteen = function (card) {
    if (card.length == '15') {
        var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
        var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
        var cardTemp = 0, i;
        card = card.substr(0, 6) + '19' + card.substr(6, card.length - 6);
        for (i = 0; i < 17; i++) {
            cardTemp += card.substr(i, 1) * arrInt[i];
        }
        card += arrCh[cardTemp % 11];
        return card;
    }
    return card;
};


// 检查手机号码是否符合规范，包括长度，类型
isCellPhoneNo = function (num) {
    var reg = /^1\d{10}$/;
    if (reg.test(num) === false) {
        layer.msg('您输入的手机号码不正确，请重新输入');
        return false;
    }
    return true;

};

/**
 * 描述：在URL中根据参数名获取参数值
 *
 * @date 2020/10/21 14:38
 */
getQueryVariable = function (variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] === variable) {
            return pair[1];
        }
    }
    return false;
}

/**
 * 描述：跳转连接
 *
 * @date 2020/10/21 15:33
 */
skipLink = function (authCode) {
    var url = "";
    /*// 副卡加装跳转
    if (authCode === "addSecondaryCard") {
        url = "https://wapact.189.cn:9001/testCeck/ceckAbility_join.html?evaluate_id=1c5e58d8ea5c657fa5cbb36cba311b4d&cmpid=fwpj-all-andios-fukajiazhuang&ticket=$ticket$";
    }*/

    switch (authCode) {
        // 副卡加装跳转
        case "addSecondaryCard" :
            url = "https://wapact.189.cn:9001/testCeck/ceckAbility_join.html?evaluate_id=1c5e58d8ea5c657fa5cbb36cba311b4d&cmpid=fwpj-all-andios-fukajiazhuang&ticket=$ticket$";
            break;
        // 单宽转融合
        case "kuandaijiac" :
            url = "https://wapact.189.cn:9001/testCeck/ceckAbility_join.html?evaluate_id=7280d7a5a68b2fc4dd9e0695ec84bac7&cmpid=fwpj-all-andios-kuandaironghe&ticket=$ticket$";
            break;
        // 4G升5G套餐迁转自动受理
        case "fourToFive" :
            url = "https://wapact.189.cn:9001/testCeck/ceckAbility_join.html?evaluate_id=82df9de5a15f994c0f0de54885eb7049&cmpid=fwpj-all-andios-4gsheng5g&ticket=$ticket$";
            break;
        // 5G融合宽带办理（新装）
        case "5GFusionBroadbandOrder" :
            url = "https://wapact.189.cn:9001/testCeck/ceckAbility_join.html?evaluate_id=42517fc342062c10279cf0703356041f&cmpid=fwpj-all-andios-5gronghekuandaibanli&ticket=$ticket$";
            break;
        // 天翼高清
        case "itvPreRegister" :
            url = "https://wapact.189.cn:9001/testCeck/ceckAbility_join.html?evaluate_id=4946138ca116a4856f57e099d46f2d14&cmpid=fwpj-all-andios-tianyigaoqing&ticket=$ticket$";
            break;
        case "dbbroadorder":
            url = "https://wapact.189.cn:9001/testCeck/ceckAbility_join.html?evaluate_id=ad680d0b7e0da44b69cc0aff2f54abec&cmpid=fwpj-all-andios-jiazhuangkuandaibanli&ticket=$ticket$";
            break;
    }

    if (url !== "" && url != null) {
        const client = UOP.isClient();
        // 安卓
        if (client === 1) {
            CtclientJS.goLink("5", url, "");
            // ios
        } else if (client === 2) {
            goLink("5", url, "");
            // wap
        } else {
            //window.location = "";
        }
    }
}

/**
 * 描述：IOS goLink方法
 *
 * @date 2020/10/21 15:33
 */
goLink = function (linkType, Link, backLink) {
    window.location.href = "objc://goLink?linkType=" + encodeURIComponent(linkType) + "&Link=" + encodeURIComponent(Link) + "&backLink=" + encodeURIComponent(backLink);
}

/**
 * 描述：设置来源和微店形态
 *
 * @date 2020/10/21 15:33
 */
setOrderFromData = function () {
    var attrList = [];
    var fs = window.sessionStorage.getItem("fs");
    if (UOP.isNotNull(fs)) {
        var fsData = {
            attrName: "",
            attrCd: "fs",//默认：fs
            javaCode: "",
            attrValue: fs,
        };
        attrList.push(fsData)
    }
    var mst = window.sessionStorage.getItem("mst");
    if (UOP.isNotNull(mst)) {
        var mstData = {
            attrName: "",
            attrCd: "mst",//默认：mst
            javaCode: "",
            attrValue: mst,
        };
        attrList.push(mstData)
    }

    var sc = window.sessionStorage.getItem("sc");
    if (UOP.isNotNull(sc)) {
        var mstData = {
            attrName: "",
            attrCd: "sc",//默认：sc 订单来源编码，主要记录投放点
            javaCode: "",
            attrValue: sc,
        };
        attrList.push(mstData)
    }
    return attrList;
}

/**
 * 获取订单来源：fs和 微店形态：mst。设置到sessionStorage
 */
setOrderFsAndMstToSessionStorage = function () {
    var fs = UOP.url("fs");
    if (UOP.isNotNull(fs)) {
        window.sessionStorage.setItem("fs", fs);
    }
    var mst = UOP.url("mst");
    if (UOP.isNotNull(mst)) {
        window.sessionStorage.setItem("mst", mst);
    }
    var sc = UOP.url("sc"); //订单来源编码
    if (UOP.isNotNull(sc)) {
        window.sessionStorage.setItem("sc", sc);
    }
}

getUnderstandDesc = function () {
    //判断会话是否存在新商品信息，是则从新商品获取
    var saleProdDescList = JSON.parse(window.sessionStorage.getItem("saleProdDesc"));
    if (UOP.isNotNull(saleProdDescList)) {
        for (var i in saleProdDescList) {
            if (saleProdDescList[i].saleDescType == '19') { //明白卡
                if (UOP.isNotNull(saleProdDescList[i].saleDesc)) {
                    return saleProdDescList[i].saleDesc;
                }
            }
        }
    }else if(UOP.isNotNull(window.sessionStorage.getItem("saleProd"))){ //新商品不为空且未配置商品描述
        return "";
    } else {
        if(UOP.isNull(window.localStorage.getItem("prodData"))){
            window.location.href = "/uop-web/errorpage/404.html";
            return ;
        }
        var prodData = JSON.parse(window.localStorage.getItem("prodData"));
        var prodAttr = prodData.prodDescVoList;
        for (var i = 0; i < prodAttr.length; i++) {
            if (19 == prodAttr[i].prodDescType) {
                return prodAttr[i].prodDesc;
            }
        }
    }


    return "";
}

urlHtml = function (params) {
    //获取url地址
    var ts_href = window.location.href;
    var ts_mainText = "";
    if (params == 1) {
        //获取地址最后一个“/”的下标
        var ts_indexof = ts_href.lastIndexOf("/");
        //获取地址“/”之后的的内容
        var ts_indexText = ts_href.substring(ts_indexof + 1);
        //获取地址“.html”的下标
        var ts_htmlBeforeText = ts_indexText.indexOf(".html");
        //获取 “/”到".html"之间的内容
        ts_mainText = ts_indexText.substring(0, ts_htmlBeforeText);
    } else if (params == 2) {
        //获取地址“/”的下标
        var ts_indexof = ts_href.lastIndexOf("/");
        //获取地址“/”之后的的内容
        var ts_indexText = ts_href.substring(ts_indexof + 1);
        ts_mainText = ts_indexText;
    } else if (params == 3) {
        //获取地址中倒数二个“/”下标的位置的之后的内容
        var urlParents = ts_href.substr(ts_href.lastIndexOf('/', ts_href.lastIndexOf('/') - 1) + 1);
        ts_mainText = urlParents
    } else if (params == 4) {
        //获取地址中倒数二个“/”的下标之后的内容
        var urlParents = ts_href.substr(ts_href.lastIndexOf('/', ts_href.lastIndexOf('/') - 1) + 1);
        //取到倒数二个“/”的下标的位置和.html之间的内容
        var beforeHtml = urlParents.indexOf(".html");
        if (beforeHtml == -1) {
            ts_mainText = urlParents;

        } else {
            ts_mainText = urlParents.substring(0, beforeHtml);
        }
    } else {
        var urlParents = ts_href.substr(ts_href.lastIndexOf('/', ts_href.lastIndexOf('/') - 1) + 1);
        var beforeHtml = urlParents.indexOf(".html");
        if (beforeHtml == -1) {
            ts_mainText = urlParents;

        } else {
            ts_mainText = urlParents.substring(0, beforeHtml);
        }
    }
    return ts_mainText;
}


getBrowser = function () {

    var u = navigator.userAgent;
    var bws = [{
        name: 'sgssapp',
        it: /sogousearch/i.test(u)
    }, {
        name: 'wechat',
        it: /MicroMessenger/i.test(u)
    }, {
        name: 'weibo',
        it: !!u.match(/Weibo/i)
    }, {
        name: 'uc',
        it: !!u.match(/UCBrowser/i) || u.indexOf(' UBrowser') > -1
    }, {
        name: 'sogou',
        it: u.indexOf('MetaSr') > -1 || u.indexOf('Sogou') > -1
    }, {
        name: 'xiaomi',
        it: u.indexOf('MiuiBrowser') > -1
    }, {
        name: 'baidu',
        it: u.indexOf('Baidu') > -1 || u.indexOf('BIDUBrowser') > -1
    }, {
        name: '360',
        it: u.indexOf('360EE') > -1 || u.indexOf('360SE') > -1
    }, {
        name: '2345',
        it: u.indexOf('2345Explorer') > -1
    }, {
        name: 'edge',
        it: u.indexOf('Edge') > -1 || u.indexOf('Edg') > -1
    }, {
        name: 'ie11',
        it: u.indexOf('Trident') > -1 && u.indexOf('rv:11.0') > -1
    }, {
        name: 'ie',
        it: u.indexOf('compatible') > -1 && u.indexOf('MSIE') > -1
    }, {
        name: 'firefox',
        it: u.indexOf('Firefox') > -1
    }, {
        name: 'safari',
        it: u.indexOf('Safari') > -1 && u.indexOf('Chrome') === -1
    }, {
        name: 'qqbrowser',
        it: u.indexOf('MQQBrowser') > -1 && u.indexOf(' QQ') === -1
    }, {
        name: 'qq',
        it: u.indexOf('QQ') > -1
    }, {
        name: 'chrome',
        it: u.indexOf('Chrome') > -1 || u.indexOf('CriOS') > -1
    }, {
        name: 'opera',
        it: u.indexOf('Opera') > -1 || u.indexOf('OPR') > -1
    }];

    for (var i = 0; i < bws.length; i++) {
        if (bws[i].it) {
            return bws[i].name;
        }
    }

    return 'other';
}


getOS = function () {
    var u = navigator.userAgent;
    var name = "";
    if (!!u.match(/compatible/i) || u.match(/Windows/i)) {
        name= 'windows';
    } else if (!!u.match(/Macintosh/i) || u.match(/MacIntel/i)) {
        name= 'macOS';
    } else if (!!u.match(/iphone/i) || u.match(/Ipad/i)) {
        name= 'ios';
    } else if (!!u.match(/android/i)) {
        name= 'android';
    } else {
        name= 'other';
    }
    return name;
}
