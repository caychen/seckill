/**
 * 
 */

// 存放交互逻辑的js代码
var seckill = {
	// 封装秒杀相关ajax的url
	URL : {
		now(){
			return '/seckill/time/now';
		},
		exposer(seckillId){
			return '/seckill/' + seckillId + '/exposer';
		},
		execution(seckillId, md5){
			return '/seckill/' + seckillId + '/' + md5 + '/execution';
		}
	},
	//验证手机号
	validatePhone(phone){//ES6
		if(phone && phone.length == 11 && !isNaN(phone)){
			return true;
		}else{
			return false;
		}
	},
	
	//倒计时
	countDown(seckillId, nowTime, startTime, endTime){
		var seckillBox = $("#seckill-box");
		if(nowTime > endTime){
			//秒杀结束
			seckillBox.html('秒杀结束');
		}else if(nowTime < startTime){
			//秒杀未开始，计时事件绑定
			//startTime 被解释成一个字符串，所以需要先将startTime转换成数字
			var killTime = new Date(new Number(startTime) + 1000);
			seckillBox.countdown(killTime, function(event){
				var format = event.strftime('秒杀倒计时： %D天 %H时 %M分 %S秒');
				seckillBox.html(format);
				
			}).on('finish,countdown', function(){
				//倒计时间结束
				//获取秒杀地址，控制显示逻辑，执行秒杀
				seckill.handlerSeckill(seckillId, seckillBox);
			});
		}else{
			//秒杀已开始
			seckill.handlerSeckill(seckillId, seckillBox);
		}
	},
	
	//处理秒杀逻辑
	handlerSeckill(seckillId, node){
		node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');//
		
		$.when($.ajax({
			url:seckill.URL.exposer(seckillId),
			type:'post',
		})).done(function(result){
			//在回调函数中，执行交互流程
			if(result && result.ok){
				var exposer = result.data;
				if(exposer.exposed){
					//开启秒杀
					//获取秒杀地址
					var killUrl = seckill.URL.execution(seckillId, exposer.md5);
					console.log(killUrl);
					
					$("#killBtn").one('click', function(){//只绑定一次事件
						//执行秒杀请求操作
						//1、先禁用按钮
						$(this).addClass('disabled');
						
						//2、发送秒杀请求
						$.when($.ajax({
							url:killUrl,
							type:'post'
						})).done(function(result){
							var killResult = result.data;
							var state = killResult.state;
							var stateInfo = killResult.stateInfo;
							
							if(result && result.ok){
								//3、显示秒杀结果
								node.html('<span class="label label-success">' + stateInfo + '</span>');
							}else{
								node.html('<span class="label label-danger">' + stateInfo + '</span>');								
							}
							
						}).fail(function(error){
							
						});
					});
					node.show();
				}else{
					//未开启秒杀
					var now = exposer.now;
					var start = exposer.start;
					var end = exposer.end;
					//重新计算计时逻辑
					seckill.countDown(seckillId, now, start, end);
				}
			}
				
		}).fail(function(error){
			
		});
	},
	
	// 详情页秒杀逻辑
	detail : {
		init(params){//ES6
			//手机验证和登录，计时交互
			//规划交互流程
			
			//从cookie中读取手机号
			var killPhone = $.cookie('killPhone');
			//console.log(seckillId, startTime, endTime);
			//验证手机号
			if(!seckill.validatePhone(killPhone)){
				//绑定phone
				var killPhoneModal = $("#killPhoneModal");
				killPhoneModal.modal({
					show:true,//显示弹出层 
					backdrop:'static',
					keyboard:false,//禁止使用esc
				});
				
				$("#killPhoneBtn").on('click', function(){
					var inputPhone = $("#killphoneKey").val();
					if(seckill.validatePhone(inputPhone)){
						//将手机号写入cookie
						$.cookie('killPhone', inputPhone, {
							expires: 7,//有效期，单位：天
							path:'/seckill'//设置在该路径下，cookie才有效
						});
						//刷新页面
						window.location.reload();
					}else{
						$("#killphoneMessage").hide().html('<label class="label label-danger">手机号输入错误！</label>').show(500);
					}
				});
			}
			//已经登录
			
			//计时交互
			var seckillId = params.seckillId;
			var startTime = params.startTime;
			var endTime = params.endTime;
			$.when($.ajax({
				url:seckill.URL.now(),
				type:'get',
			})).done(function(result){
				if(result && result.ok){
					var nowTime = result.data;
					//时间判断
					seckill.countDown(seckillId, nowTime, startTime, endTime);
				}
			}).fail(function(error){
				
			})
		}
	}
}