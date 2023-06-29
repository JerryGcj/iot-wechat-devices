var isRead = true;// 是否有我已阅读
var isSms = true; //是否使用短信验证码
var isIdNo = false;//是否有身份证号码

var app = new Vue({
	el: '#app',
	data: {
		isRead : true, // 是否有我已阅读
		model: {
			address: { //地址选择
				visible: false,
				otherProvinces: {
					visible: true
				}
			},
			loading: { //加载中
				visible: false,
				content: '加载中'
			},
			tip: { //页面提示
				visible: false,
				content: '',
				btn: ''
			},
			footBtn: { //底部按钮
				visible: false
			},
		},
		form: {
			cusName: '',
			cusPhone: '',
			cusIdno: '',
			preProvince: {},
			preCity: {},
			preDistrict: {},
			province: {},
			city: {},
			district: {},
			detailAddr: '',
			userRead: false
		},
		vld: {
			cusName: true,
			cusNameTip: '',
			cusPhone: true,
			cusPhoneTip: '',
			cusIdno: true,
			cusIdnoTip: '',
			address: true,
			addressTip: '',
			detailAddr: true,
			detailAddrTip: '',
			ttShow: false
		},
		addressData: [],
		unChooseExpr: {
			"provinceId": "0",
			"cityList": [{
				"area": [{
					"areaName": "请选择",
					"areaId": "0"
				}],
				"cityId": "0",
				"cityName": "请选择"
			}],
			"provinceName": "请选择"
		},
		pData: [],
		cData: [],
		dData: [],
		showDetail: false //是否显示详情
	},
	watch: {},
	mounted() {
		var _this = this;

		//初始化省市区地址信息
		this.initAddressData();

		//显示底部领取按钮
		window.onscroll = function(e) {
			//滚动条高度+视窗高度 = 可见区域底部高度
			var visibleBottom = _this.getScrollOffset().y + _this.getViewportOffset().h;
			//可见区域顶部高度
			var submitBtnY = _this.$refs.getBtn.getBoundingClientRect().height + _this.$refs.getBtn
				.getBoundingClientRect().y;
			if (submitBtnY > visibleBottom || submitBtnY < 0) {
				_this.model.footBtn.visible = true
			} else {
				_this.model.footBtn.visible = false;
			}
		};
	},
	compute() {

	},
	methods: {
		storeForm() {
			let _this = this;
			let formInfo = {
				'cusName': _this.form.cusName,
				'cusPhone': _this.form.cusPhone,
				'cusIdno': _this.form.cusIdno,
				'province': _this.isEmpty(_this.form.province) ? '' : _this.form.province.value == '请选择' ?
					'' : _this.form.province.value.substring(0, 2),
				'city': _this.isEmpty(_this.form.city) ? '' : _this.form.city.value == '请选择' ? '' : _this
					.form.city.value.substring(0, 2),
				'district': _this.isEmpty(_this.form.district) ? '' : _this.form.district.value == '请选择' ?
					'' : _this.form.district.value.substring(0, 2),
				'detailAddr': _this.form.detailAddr
			};
			console.log(formInfo);
			let formObj = JSON.stringify(formInfo);
			localStorage.setItem("formObj", formObj);
		},
		restoreForm() {
			let _this = this;
			try {
				let formObj = JSON.parse(localStorage.getItem("formObj"));
				if (!_this.isEmpty(formObj)) {
					_this.form.cusName = formObj.cusName;
					_this.form.cusPhone = formObj.cusPhone;
					_this.form.cusIdno = formObj.cusIdno;
					if (!_this.isEmpty(_this.form.cusPhone)) {
						_this.vld.ttShow = true;
					}
					_this.form.detailAddr = formObj.detailAddr;
					if (!_this.isEmpty(formObj.province)) {
						//还原省
						let pObj = _this.pData.find(function(item) {
							if (item.value.indexOf(formObj.province) > -1) {
								return true;
							}
						});
						if (!_this.isEmpty(pObj)) {
							_this.form.province = pObj;
							_this.form.preProvince = pObj;
							//还原市
							if (!_this.isEmpty(formObj.city)) {
								let cObj = _this.cData.find(function(item) {
									if (item.value.indexOf(formObj.city) > -1 && item.parentId == pObj
										.id) {
										return true;
									}
								});
								if (!_this.isEmpty(cObj)) {
									_this.form.city = cObj;
									_this.form.preCity = cObj;
									//还原区
									if (!_this.isEmpty(formObj.district)) {
										let dObj = _this.dData.find(function(item) {
											if (item.value.indexOf(formObj.district) > -1 && item
												.parentId == cObj.id) {
												return true;
											}
										});
										if (!_this.isEmpty(dObj)) {
											_this.form.district = dObj;
											_this.form.preDistrict = dObj;
										}
									}
								}
							}
						}
					}
				}
			} catch (e) {
				console.error("回填信息异常:" + e.message)
				localStorage.clear();
			}
		},
		// 校验姓名
		vldName(mod) {
			var _this = this;
			if (_this.isEmpty(_this.form.cusName)) {
				if (mod) {
					_this.toast("请填写申办人姓名", 1500);
				}
				_this.vld.cusNameTip = "*姓名<span>未填写</span>，请填写申办人的姓名";
				_this.vld.cusName = false;
				return false;
			}
			if (_this.form.cusName.length <= 1) {
				if (mod) {
					_this.toast("请正确填写申办人姓名", 2000);
				}
				_this.vld.cusNameTip = " *姓名<span>填写错误</span>，请填写真实的姓名";
				_this.vld.cusName = false;
				return false;
			}
			if (_this.form.cusName.indexOf("先生") > -1 ||
				_this.form.cusName.indexOf("老师") > -1 ||
				_this.form.cusName.indexOf("小姐") > -1 ||
				_this.form.cusName.indexOf("女士") > -1) {
				if (mod) {
					_this.toast("请正确填写申办人姓名", 2000);
				}
				_this.vld.cusNameTip = " *姓名<span>填写错误</span>，请填写真实的姓名";
				_this.vld.cusName = false;
				return false;
			}
			_this.vld.cusName = true;
			return true;
		},

		//校验姓名合法性
		vldNameContain(mod) {
			var _this = this;
			//空字符串判断
			if (_this.isEmpty(_this.form.cusName)) {
				return false;
			}
			//长度判断
			if (_this.form.cusName.length <= 1) {
				return false;
			}
			//进行连续字符判断（单个字连续三次，两个字连续三次出现）
			var str = _this.form.cusName;
			var reg1 = /([\u4E00-\uFA20])\1\1/g;
			var reg2 = /([\u4E00-\uFA20][\u4E00-\uFA20])\1\1/g
			//str.match(reg);//输出: ["AA", "99"]
			if (str.match(reg1) || str.match(reg2)) {
				if (mod) {
					_this.toast("请正确填写申办人姓名", 2000);
				}
				_this.vld.cusNameTip = " *姓名<span>填写错误</span>，请填写真实的姓名";
				_this.vld.cusName = false;
				return false;
			}
			//进行敏感词校验
			var result = false;
			$.ajax({
				type: 'get',
				url: ctxPath + 'sensitiveWordFilter/checkContain',
				contentType: 'application/json',
				dataType: 'json',
				data: {str: _this.form.cusName},
				async: false, //同步
				success: function (res) {
					if (res.code == 200) {
						// _this.toast("敏感词验证通过", 2000);
						result = true;
					} else {
						console.log(123)
						if (mod) {
							_this.toast("申办人姓名存在敏感词", 2000);
						}
						_this.vld.cusNameTip = " *姓名<span>含敏感词</span>，请填写真实的姓名";
						_this.vld.cusName = false;
						result = false;
					}
				},
				error: function (e) {
					_this.model.loading.visible = false;
					_this.toast("出错啦，您可以刷新页面重试。", 3000);
					_this.vld.cusName = false;
					result = false;
				}
			});
			return result;
		},

		//校验联系电话
		vldPhone(mod) {
			var _this = this;
			if (_this.isEmpty(_this.form.cusPhone)) {
				if (mod) {
					_this.toast("请填写联系电话", 2000);
				}
				_this.vld.cusPhoneTip = "*收货号码<span>未填写</span>，请填写申办人常用号码作为收货号码";
				_this.vld.cusPhone = false;
				return false;
			}
			if (!_this.isTelNum(_this.form.cusPhone)) {
				if (mod) {
					this.toast("请正确填写联系电话", 2000);
				}
				this.vld.cusPhoneTip = " *联系电话<span>不正确</span>，请检查已填写联系电话并修改";
				this.vld.cusPhone = false;
				return false;
			}
			this.vld.cusPhone = true;
			return true;
		},
		vldCode(mod) {
			var _this = this;
			if (_this.isEmpty(_this.form.captchaSms)) {
				if (mod) {
					_this.toast("请填写验证码", 2000);
				}
				_this.vld.captchaSmsTip = "*验证码<span>未填写</span>，请填写获取到的验证码";
				_this.vld.captchaSms = false;
				return false;
			}
			this.vld.captchaSms = true;
			return true;
		},
		//校验手机号码
		isTelNum(val) {
			if (!(/^1[3456789]\d{9}$/.test(val))) {
				return false;
			}
			return true;
		},
		//身份证校验
		valIdNo(mod) {
			var _this = this;
			if (_this.isEmpty(_this.form.cusIdno)) {
				if (mod) {
					_this.toast("请填写申办人身份证", 1500);
				}
				_this.vld.cusIdnoTip = "*身份证<span>未填写</span>，请正确填写申办人证件号";
				_this.vld.cusIdno = false;
				return false;
			}
			if (_this.form.cusIdno.length != 18) {
				if (mod) {
					_this.toast("证件位数不足，请检查填写的证件号码", 2500);
				}
				_this.vld.cusIdnoTip = " *身份证<span>填写错误</span>，请检查内容并修改";
				_this.vld.cusIdno = false;
				return false;
			} else {
				if (!_this.isIdCard(_this.form.cusIdno)) {
					if (mod) {
						_this.toast("证件号有误", 1500);
					}
					_this.vld.cusIdnoTip = " *身份证<span>有误</span>，请正确填写申办人证件号";
					_this.vld.cusIdno = false;
					return false;
				}
			}
			var age = _this.getAge(_this.form.cusIdno);
			if (isNaN(age)) {
				if (mod) {
					_this.toast("请输入正确的身份证号码", 2500);
				}
				_this.vld.cusIdnoTip = " *身份证<span>未满16周岁</span>，如有错误，请检查内容并修改";
				_this.vld.cusIdno = false;
				return false;
			}
			if (age < 16) {
				if (mod) {
					_this.toast("抱歉该证件未满16周岁，不能为您办理", 2500);
				}
				_this.vld.cusIdnoTip = " *身份证<span>未满16周岁</span>，如有错误，请检查内容并修改";
				_this.vld.cusIdno = false;
				return false;
			}
			if (age > 64) {
				if (mod) {
					_this.toast("很抱歉~申办人的年龄已超过65岁，不可在线领卡，您可使用[16~65岁]年龄之间的其他家人身份证进行申请哦！", 2500);
				}
				_this.vld.cusIdnoTip = " *身份证<span>超过65周岁</span>，如有错误，请检查内容并修改";
				_this.vld.cusIdno = false;
				return false
			}
			this.vld.cusIdno = true;
			return true
		},
		/**
		 * 身份证15位编码规则：dddddd yymmdd xx p dddddd：6位地区编码 yymmdd: 出生年(两位年)月日，如：910215 xx:
		 * 顺序编码，系统产生，无法确定 p: 性别，奇数为男，偶数为女
		 *
		 * 身份证18位编码规则：dddddd yyyymmdd xxx y dddddd：6位地区编码 yyyymmdd:
		 * 出生年(四位年)月日，如：19910215 xxx：顺序编码，系统产生，无法确定，奇数为男，偶数为女 y: 校验码，该位数值可通过前17位计算获得
		 *
		 * 前17位号码加权因子为 Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ] 验证位
		 * Y = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ] 如果验证码恰好是10，为了保证身份证是十八位，那么第十八位将用X来代替
		 * 校验位计算公式：Y_P = mod( ∑(Ai×Wi),11 ) i为身份证号码1...17 位; Y_P为校验码Y所在校验码数组位置
		 */
		isIdCard(idCard) {
			// 15位和18位身份证号码的正则表达式
			var regIdCard =
				/^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/;

			// 如果通过该验证，说明身份证格式正确，但准确性还需计算
			if (regIdCard.test(idCard)) {
				if (idCard.length == 18) {
					var idCardWi = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4,
					2); // 将前17位加权因子保存在数组里
					var idCardY = new Array(1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2); // 这是除以11后，可能产生的11位余数、验证码，也保存成数组
					var idCardWiSum = 0; // 用来保存前17位各自乖以加权因子后的总和
					for (var i = 0; i < 17; i++) {
						idCardWiSum += idCard.substring(i, i + 1) * idCardWi[i];
					}

					var idCardMod = idCardWiSum % 11; // 计算出校验码所在数组的位置
					var idCardLast = idCard.substring(17); // 得到最后一位身份证号码

					// 如果等于2，则说明校验码是10，身份证号码最后一位应该是X
					if (idCardMod == 2) {
						if (idCardLast == "X" || idCardLast == "x") {
							return true;
						} else {
							return false;
						}
					} else {
						// 用计算出的验证码与最后一位身份证号码匹配，如果一致，说明通过，否则是无效的身份证号码
						if (idCardLast == idCardY[idCardMod]) {
							return true;
						} else {
							return false;
						}
					}
				} else {
					return true;
				}
			} else {
				return false;
			}
		},
		//替换身份证大小写
		replacex: function() {
			var _this = this;
			_this.form.cusIdno = _this.form.cusIdno.replace('x', 'X');
		},
		/*获取年龄*/
		getAge(identityCard) {
			var len = (identityCard + "").length;
			if (len == 0) {
				return 0;
			} else {
				if ((len != 15) && (len != 18)) //身份证号码只能为15位或18位其它不合法
				{
					return 0;
				}
			}
			var strBirthday = "";
			if (len == 18) //处理18位的身份证号码从号码中得到生日和性别代码
			{
				strBirthday = identityCard.substr(6, 4) + "/" + identityCard.substr(10, 2) + "/" + identityCard
					.substr(12, 2);
			}
			if (len == 15) {
				strBirthday = "19" + identityCard.substr(6, 2) + "/" + identityCard.substr(8, 2) + "/" +
					identityCard.substr(10, 2);
			}
			//时间字符串里，必须是“/”
			var birthDate = new Date(strBirthday);
			var nowDateTime = new Date();
			var age = nowDateTime.getFullYear() - birthDate.getFullYear();
			//再考虑月、天的因素;.getMonth()获取的是从0开始的，这里进行比较，不需要加1
			if (nowDateTime.getMonth() < birthDate.getMonth() || (nowDateTime.getMonth() == birthDate
				.getMonth() && nowDateTime.getDate() < birthDate.getDate())) {
				age--;
			}
			return age;
		},
		/*展示选址*/
		showAddress() {
			var _this = this;
			_this.model.address.visible = true;
		},
		//初始化省市区
		initAddressData() {
			var _this = this;
			_this.addressData = addressInfo.allProvinceInfo;
			//添加请选择
			_this.addressData.splice(0, 0, _this.unChooseExpr);
			$.each(_this.addressData, function(index, item) {
				var pItem = {
					id: item.provinceId,
					value: item.provinceName
				}
				_this.pData.push(pItem);
				if (item.provinceId == 0) {
					_this.form.province = pItem;
				}
				$.each(item.cityList, function(indexs, items) {
					var cItem = {
						id: items.cityId,
						value: items.cityName,
						parentId: item.provinceId
					};
					_this.cData.push(cItem);
					if (items.cityId == 0) {
						_this.form.city = cItem;
					}
					$.each(items.area, function(indexss, itemss) {
						var dItem = {
							id: itemss.areaId,
							value: itemss.areaName,
							parentId: items.cityId
						}
						_this.dData.push(dItem);
						if (itemss.areaId == 0) {
							_this.form.district = dItem;
						}
					})
				})
			})
			_this.restoreForm();
			//定位获取省市区
			if (_this.isEmpty(_this.form.province) || _this.form.province.value == '请选择') {
				_this.achieveGdAddress = true;
			}
		},
		confirmAddress() {
			var _this = this;
			if (!_this.isEmpty(_this.form.preDistrict) && !_this.isEmpty(_this.form.preCity) && !_this.isEmpty(
					_this.form.preDistrict)) {
				_this.form.province = _this.form.preProvince;
				_this.form.city = _this.form.preCity;
				_this.form.district = _this.form.preDistrict;
				_this.model.address.visible = false;
				_this.storeForm();
			} else {
				_this.toast("请先选择您的收货地址", 2500);
			}
		},
        //校验收货地址省市区
        vldAddress(mod){
            var _this = this;
            if(_this.isEmpty(_this.form.province)||_this.form.province.id==0||
               _this.isEmpty(_this.form.city)||_this.form.city.id==0||
               _this.isEmpty(_this.form.district)||_this.form.district.id==0){
                if(mod) {
                    _this.toast("请选择收货城市", 2500);
                }
               _this.vld.addressTip = "*请选择<span>收货城市</span>";
               _this.vld.address=false;
               return false;
            }
            this.vld.address=true;
            return true;
        },
		//校验详细地址
		vldDetailAddr(mod) {
			var _this = this;
			if (_this.isEmpty(_this.form.detailAddr)) {
				if (mod) {
					_this.toast("请填写详细地址", 2500);
				}
				_this.vld.detailAddrTip = "*详细地址<span>未填写</span>";
				_this.vld.detailAddr = false;
				return false;
			}
			if (_this.form.detailAddr.length <= 6) {
				if (mod) {
					_this.toast("您的地址过于简单，请尽量填写详细", 2500);
				}
				_this.vld.detailAddrTip = "亲~您的地址过于简单，快递小哥可能无法配送哦！请参考以下示例的格式尽量填写详细（" +
					"<span style='color:red;'>7个字以上</span>" +
					"）。如<br>小区地址：   XX街道XX路XX号XX小区XX栋/单元XX房号<br>写字楼地址： XX街道XX路XX号XX写字楼XX楼层XX公司<br>村镇地址：   XX镇XX村XX组";
				_this.vld.detailAddr = false;
				return false;
			}
			this.vld.detailAddr = true;
			return true;
		},
		//校验详细地址合法性
		vldDetailAddrContain(mod) {
			var _this = this;
			//空字符串判断
			if (_this.isEmpty(_this.form.detailAddr)) {
				return false;
			}
			//长度判断
			if (_this.form.detailAddr.length <= 6) {
				return false;
			}
			//进行连续字符判断（单个字连续三次，两个字连续三次出现）
			var strAddr = _this.form.detailAddr;
			var reg1 = /([\u4E00-\uFA20])\1\1/g;
			var reg2 = /([\u4E00-\uFA20][\u4E00-\uFA20])\1\1/g
			//str.match(reg);//输出: ["AA", "99"]
			if (strAddr.match(reg1) || strAddr.match(reg2)) {
				if (mod) {
					_this.toast("请正确填写详细地址", 2500);
				}
				_this.vld.detailAddrTip = "*详细地址<span>填写错误</span>，请正确填写";
				_this.vld.detailAddr = false;
				return false;
			}
			//进行敏感词校验
			var result = false;
			$.ajax({
				type: 'get',
				url: ctxPath + 'sensitiveWordFilter/checkContain',
				contentType: 'application/json',
				dataType: 'json',
				data: {str: _this.form.detailAddr},
				async: false, //同步
				success: function (res) {
					if (res.code == 200) {
						// _this.toast("敏感词验证通过", 2000);
						result = true;
					} else {
						if (mod) {
							console.log(_this);
							_this.toast("详细地址存在敏感词", 2500);
						}
						_this.vld.detailAddrTip = "*详细地址<span>含敏感词</span>，请正确填写";
						_this.vld.detailAddr = false;
						result = false;
					}
				},
				error: function (e) {
					_this.model.loading.visible = false;
					_this.toast("出错啦，您可以刷新页面重试。", 3000);
					_this.vld.detailAddr = false;
					result = false;
				}
			});
			return result;
		},


		filterAddress() {
			var _this = this;
			if (_this.form.detailAddr.indexOf("详细地址:") > -1) {
				_this.form.detailAddr = _this.form.detailAddr.substring(_this.form.detailAddr.indexOf("详细地址:") + 5, _this
					.form.detailAddr.length);
			}
		},
		// 校验提单资质
		vldQuality(callback) {
			var _this = this;
			var params = {
				'cusId': cusId,
				'cusName': _this.form.cusName,
				'cusIdno': _this.form.cusIdno,
				'cusPhone': _this.form.cusPhone,
				'province': _this.form.province.value,
				'city': _this.form.city.value,
				'district': _this.form.district.value,
				'detailAddr': _this.form.detailAddr
			};

			$.ajax({
				type: 'post',
				url: ctxPath + 'electronCard/validOrder',
				contentType: 'application/json',
				dataType: 'json',
				data: JSON.stringify(params),
				success: function(res) {
					if (res.code == 200) {
						// 触发回调
						if (typeof callback === 'function') {
							callback();
						}
					} else {
						_this.model.loading.visible = false;
						_this.toast(res.msg, 3000);
					}
				},
				error: function(e) {
					_this.model.loading.visible = false;
					_this.toast("出错啦，您可以刷新页面重试。", 3000);
				}
			});
		},
		//校验表单信息
		vldFormInfo() {
			if (!this.vldName(true)) {
				return false;
			}
			if (!this.vldPhone(true)) {
				return false;
			}
			if (!this.vldNameContain(true)) {
				return false;
			}
			if (isIdNo) {
				if (!this.valIdNo(true)) {
					return false;
				}
			}
			if (!this.vldAddress(true)) {
				return false;
			}
			if (!this.vldDetailAddr(true)) {
				return false;
			}
			if (!this.vldDetailAddrContain(true)) {
				return false;
			}
			return true;
		},
		// 提交
		submit() {
			var _this = this;

			// 是否有我已阅读
			if (isRead) {
				if(!_this.form.userRead) {
					_this.toast("请勾选我已阅读", 2000);
					return;
				}
			}

			// 展示加载框
			_this.model.loading.visible = true;
			_this.model.loading.content = "订单提交中...";

			// 校验表单信息
			if (!_this.vldFormInfo()) {
				_this.model.loading.visible = false;
				return;
			}

			// 校验提单资质
			_this.vldQuality(function () {
				var params = {
					'cusId': cusId,
					'cusName': _this.form.cusName,
					'cusIdno': _this.form.cusIdno,
					'cusPhone': _this.form.cusPhone,
					'province': _this.form.province.value,
					'city': _this.form.city.value,
					'district': _this.form.district.value,
					'detailAddr': _this.form.detailAddr,
					'fromType': fromType
				};

				// 提交订单
				$.ajax({
					type: 'post',
					url: ctxPath + 'electronCard/order',
					contentType: 'application/json',
					dataType: 'json',
					data: JSON.stringify(params),
					success: function(res) {
						if (res.code == 200) {
							_this.model.loading.visible = false;
							_this.toast("提交成功，请耐心等待审核~", 3000);

							// 触发埋点
							if (typeof eventTracking === 'function') {
								eventTracking();
							}

							localStorage.removeItem("formObj");
						} else {
							_this.model.loading.visible = false;
							_this.toast(res.msg, 3000);
						}
					},
					error: function(e) {
						_this.model.loading.visible = false;
						_this.toast("出错啦，您可以刷新页面重试。", 3000);
					}
				});
			});
		},
		//提交并支付
		submit2() {
			var _this = this;

			// 展示加载框
			_this.model.loading.visible = true;
			_this.model.loading.content = "订单提交中...";

			if (!_this.vldFormInfo()) {
				_this.model.loading.visible = false;
				return;
			}

			if (!window.confirm("订单需要支付2.9元，是否确认提交订单？")) {
				_this.model.loading.visible = false;
				_this.toast("你已取消支付！", 3000);
				return;
			}

			var params = {
				'cusId': cusId,
				'openid': openid,
				'cusName': _this.form.cusName,
				'cusPhone': _this.form.cusPhone,
				'province': _this.form.province.value,
				'city': _this.form.city.value,
				'district': _this.form.district.value,
				'detailAddr': _this.form.detailAddr
			};
			$.ajax({
				type: 'post',
				url: ctxPath + 'unicomCardPay/order',
				contentType: 'application/json',
				dataType: 'json',
				data: JSON.stringify(params),
				success: function(res) {
					if (res.code == 200) {
						_this.model.loading.visible = false;
						_this.toast("提交成功，正在支付~", 3000);

						if (typeof WeixinJSBridge == "undefined") {
							if (document.addEventListener) {
								document.addEventListener('WeixinJSBridgeReady', _this.onBridgeReady, false);
							} else if (document.attachEvent) {
								document.attachEvent('WeixinJSBridgeReady', _this.onBridgeReady);
								document.attachEvent('onWeixinJSBridgeReady', _this.onBridgeReady);
							}
						} else {
							_this.onBridgeReady(res.result);
						}

						localStorage.removeItem("formObj");
					} else {
						_this.model.loading.visible = false;
						_this.toast(res.msg, 3000);
					}
				},
				error: function(e) {
					_this.model.loading.visible = false;
					_this.toast("出错啦，您可以刷新页面重试。", 3000);
				}
			});
		},

		//提交并支付
		submit3() {
			var _this = this;
			// 是否有我已阅读
			if (_this.isRead) {
				if(!_this.form.userRead) {
					_this.toast("请勾选我已阅读", 2000);
					return;
				}
			}
			// 展示加载框
			_this.model.loading.visible = true;
			_this.model.loading.content = "订单提交中...";

			if (!_this.vldFormInfo()) {
				_this.model.loading.visible = false;
				return;
			}
			var url = window.location.host;
			var strings = url.split(".");
			var first = strings[0];
			var params = {
				'isSms':isSms,
				'origin':first,
				'cusId': cusId,
				'cusName': _this.form.cusName,
				'cusIdno': _this.form.cusIdno,
				'cusPhone': _this.form.cusPhone,
				'captchaSms': _this.form.captchaSms,
				'province': _this.form.province.value,
				'city': _this.form.city.value,
				'district': _this.form.district.value,
				'detailAddr': _this.form.detailAddr
			};

			// 提交订单
			$.ajax({
				type: 'post',
				url: ctxPath + 'unicomCardPay/noPayOrder',
				contentType: 'application/json',
				dataType: 'json',
				data: JSON.stringify(params),
				success: function(res) {
					if (res.code == 200) {
						_this.model.loading.visible = false;
						_this.toast("提交成功，请耐心等待审核~", 3000);

						// 触发埋点
						if (typeof eventTracking2 === 'function') {
							eventTracking2();
						}

						localStorage.removeItem("formObj");
					} else {
						_this.model.loading.visible = false;
						_this.toast(res.msg, 3000);
					}
				},
				error: function(e) {
					_this.model.loading.visible = false;
					_this.toast("出错啦，您可以刷新页面重试。", 3000);
				}
			});
		},
		submitOrder() {},
		onBridgeReady(data) {
			var _this = this;
			WeixinJSBridge.invoke('getBrandWCPayRequest', data, function(res) {
				if(res.err_msg == "get_brand_wcpay_request:ok" ) {
					_this.toast("支付成功！", 3000);
				} else if (res.err_msg == "get_brand_wcpay_request:cancel") {
					_this.toast("支付取消！", 3000);
				} else if (res.err_msg == "get_brand_wcpay_request:fail") {
					_this.toast("支付失败！", 3000);
					WeixinJSBridge.call('closeWindow');
				}
			});
		},

		subSpecialName() {
			this.form.cusName = this.replaceSpecialNameChar(this.form.cusName)
		},
		replaceSpecialNameChar(val) {
			var pattern = new RegExp(
				"[0-9a-zA-Z-+～＠＃＄％＾＆＊（）＿＋\\\\`~!@%_#$^&*()=|{}';',\\[\\].<>/:?~！@#￥……&*（）——|{}【】‘；：”“'。，、？↵\r\n]"
				);
			var rs = "";
			for (var i = 0; i < val.length; i++) {
				rs = rs + val.substr(i, 1).replace(pattern, '');
			}
			return rs;
		},
		subSpecialAddr() {
			this.form.detailAddr = this.replaceSpecialAddrChar(this.form.detailAddr)
		},
		replaceSpecialAddrChar(val) {
			var pattern = new RegExp(
				"[-+～＠＃＄％＾＆＊（）＿＋\\\\`~!@%_#$^&*()=|{}';'‘,\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？↵\r\n]");
			var rs = "";
			for (var i = 0; i < val.length; i++) {
				rs = rs + val.substr(i, 1).replace(pattern, '');
			}
			return rs;
		},
		//提示
		toast(content, times) {
			var _this = this;
			_this.model.tip.content = content;
			_this.model.tip.visible = true;
			setTimeout(function() {
				_this.model.tip.visible = false;
			}, times);
		},
		// 弹出层
		alert(content, btn) {
			this.model.tip.content = content;
			this.model.tip.btn = btn;
			this.model.tip.visible = true;
		},
		// 关闭弹出层
		closeAlert() {
			this.model.tip.content = '';
			this.model.tip.btn = '';
			this.model.tip.visible = false;
		},
		// 判空
		isEmpty(obj) {
			if (obj == null || obj === '' || obj == 'undefined' || typeof(obj) == "undefined" || $
				.isEmptyObject(obj)) {
				return true;
			}
			return false;
		},
		getViewportOffset() {
			if (window.innerWidth) {
				return {
					w: window.innerWidth,
					h: window.innerHeight
				}
			} else {
				if (document.compatMode === "BackCompat") {
					return {
						w: document.body.clientWidth,
						h: document.body.clientHeight
					}
				} else {
					return {
						w: document.documentElement.clientWidth,
						h: document.documentElement.clientHeight,
					}
				}
			}
		},
		//获取滚动条的位置
		getScrollOffset() {
			if (window.pageXOffset) {
				return {
					x: window.pageXOffset,
					y: window.pageYOffset
				}
			} else {
				return {
					x: document.body.scrollLeft + document.documentElement.scrollLeft,
					y: document.body.scrollTop + document.documentElement.scrollTop
				}
			}
		},
		// 显示详情
		toggleDetail() {
			this.showDetail = !this.showDetail;
		},
		// 显示入网协议
		showAgreement1() {
			this.alert(agreementTxt1, "我知道了");
		},
		// 显示信息收集公告
		showAgreement2() {
			this.alert(agreementTxt2, "我知道了");
		},
		// 显示信息收集公告
		showAgreementIot() {
			this.alert(agreementTxtIot, "我知道了");
		},
		showAgreement9() {
			this.alert(agreementTxt9, "我知道了");
		},
		showAgreement10() {
			this.alert(agreementTxt10, "我知道了");
		}
	}
});

function blurscoll(id) {
	document.querySelector('.fixed-btn').style.position = 'fixed'
}

function focusscoll(id) {
	document.querySelector('.fixed-btn').style.position = 'static'
	var stoph = (document.getElementById(id).offsetTop) + 50
	window.scrollTo(0, stoph)
}
