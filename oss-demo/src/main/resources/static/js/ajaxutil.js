/**
 * jquery ajax 封装
 */

var loading;
/**
 * type：请求方式  post和 get，默认get
 * url：请求地址
 * data：请求参数，参数类型为Object或String类型
 * dataType:预期服务器返回的数据类型，常用的如：xml、html、json、text
 * cache：设置为false将不会从浏览器缓存中加载请求信息
 * timeout: 超时时间（毫秒）
 * async：默认true，均为异步请求。 如果需要发送同步请求，需将此项设置为false。
 * 注意，同步请求将锁住浏览器，用户其他操作必须等待请求完成才可以执行。
 */
function sendAjaxLoading(obj){
	$.ajax({
		type:obj.type,
		url:obj.url,
		data:obj.data,
		dataType:"JSON",//预期服务器返回的数据类型
		cache:false,//是否设置缓存，默认true
		timeout: 30000,//超时时间（毫秒）
		async:true,//默认true，均为异步请求。 如果需要发送同步请求，需将此项设置为false。注意，同步请求将锁住浏览器，用户其他操作必须等待请求完成才可以执行。
		beforeSend:function(XmlHttpRequest){
			//要求为function类型的参数，发送请求前可以修改XMLHttpRequest对象的函数，例如添加自定义Http头。
			//在beforeSend中如果返回false可以取消本次ajax请求。XMLHttpRequest对象是唯一的参数。
			loading=layer.msg('加载中',{
				icon: 16,
				shade:0.01,
				time:false //取消自动关闭
			});
		},
		complete:function(){
			layer.close(loading)//手动关闭
		},
		success:function(res){

			if(res.returnCode == 400){
				layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				});  
				return;
	        }else if(res.returnCode == 403){
	        	layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				},function(){
					//重新登录
					window.location.href="/auth/login";
				});
	        	return;
	        }else if(res.returnCode == 500){
	        	layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				});
	        	return;
		    }
			//调用传入的success方法
			obj.success(res);
		},
		error:function(res){
			layer.msg("网络出错，请稍后重试！",{
				icon: 2,
				time:2000 //2秒后关闭。不设置默认3秒
			});
		}
	})
}
/**
 * 带 处理中  提示
 */
function sendAjax(obj){
	$.ajax({
		type:obj.type,
		url:obj.url,
		data:obj.data,
		dataType:"JSON",//预期服务器返回的数据类型
		cache:false,//是否设置缓存，默认true
		timeout: 30000,//超时时间（毫秒）
		async:true,//默认true，均为异步请求。 如果需要发送同步请求，需将此项设置为false。注意，同步请求将锁住浏览器，用户其他操作必须等待请求完成才可以执行。
		beforeSend:function(XmlHttpRequest){
			//要求为function类型的参数，发送请求前可以修改XMLHttpRequest对象的函数，例如添加自定义Http头。
			//在beforeSend中如果返回false可以取消本次ajax请求。XMLHttpRequest对象是唯一的参数。
			loading=layer.msg('处理中...',{
				icon: 16,
				shade:0.01,
				time:false //取消自动关闭
			});
		},
		complete:function(){
			layer.close(loading)//手动关闭
		},
		success:function(res){

			if(res.returnCode == 400){
				layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				});  
				return;
	        }else if(res.returnCode == 403){
	        	layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				},function(){
					//重新登录
					window.location.href="/auth/login";
				});
	        	return;
	        }else if(res.returnCode == 500){
	        	layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				});
	        	return;
		    }
			//调用传入的success方法
			obj.success(res);
		},
		error:function(res){
			layer.msg("网络出错，请稍后重试！",{
				icon: 2,
				time:2000 //2秒后关闭。不设置默认3秒
			});
		}
	})
}
/**
 * 不带加载中 异步
 */
function sendCommonAjax(obj){
	$.ajax({
		type:obj.type,
		url:obj.url,
		data:obj.data,
		dataType:"JSON",//预期服务器返回的数据类型
		cache:false,//是否设置缓存，默认true
		timeout: 30000,//超时时间（毫秒）
		async:true,//默认true，均为异步请求。 如果需要发送同步请求，需将此项设置为false。注意，同步请求将锁住浏览器，用户其他操作必须等待请求完成才可以执行。
		beforeSend:function(XmlHttpRequest){
		},
		complete:function(){
			
		},
		success:function(res){
			if(res.returnCode == 400){
				layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				});  
				return;
			}else if(res.returnCode == 403){
				layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				},function(){
					//重新登录
					window.location.href="/auth/login";
				});
				return;
			}else if(res.returnCode == 500){
				layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				});
				return;
			}
			//调用传入的success方法
			obj.success(res);
		},
		error:function(res){
			layer.msg("网络出错，请稍后重试！",{
				icon: 2,
				time:2000 //2秒后关闭。不设置默认3秒
			});
		}
	})
}

/**
 * 不带加载中 同步方法
 */
function sendSyncAjax(obj){
	$.ajax({
		type:obj.type,
		url:obj.url,
		data:obj.data,
		dataType:"JSON",//预期服务器返回的数据类型
		cache:false,//是否设置缓存，默认true
		timeout: 30000,//超时时间（毫秒）
		async:false,//默认true，均为异步请求。 如果需要发送同步请求，需将此项设置为false。注意，同步请求将锁住浏览器，用户其他操作必须等待请求完成才可以执行。
		beforeSend:function(XmlHttpRequest){
		},
		complete:function(){
			
		},
		success:function(res){
			if(res.returnCode == 400){
				layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				});  
				return;
			}else if(res.returnCode == 403){
				layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				},function(){
					//重新登录
					window.location.href="/auth/login";
				});
				return;
			}else if(res.returnCode == 500){
				layer.msg(res.msg,{
					icon: 2,
					time:2000 //2秒后关闭。不设置默认3秒
				});
				return;
			}
			//调用传入的success方法
			obj.success(res);
		},
		error:function(res){
			layer.msg("网络出错，请稍后重试！",{
				icon: 2,
				time:2000 //2秒后关闭。不设置默认3秒
			});
		}
	})
}