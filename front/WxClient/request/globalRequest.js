// 正式环境
// const BASE_URL = "https://www.chopper.love:39001/api/"
// 开发环境
// const BASE_URL = "http://192.168.242.22:16378/"
const BASE_URL = "http://192.168.100.3:16378/"

// 检查是否登录，如果没有登录则进行登录
function checkLogin(){
	return new Promise((resolve, reject) => {
		let token = uni.getStorageSync("token")
		if(token){
			return resolve(token);
		}else{
			uni.login({
				success(res) {
					 uni.request({
						method:"POST",
						url:BASE_URL + "user/client/login",
						data:{"code":res.code},
						success(res){
							console.log("登录成功")
							uni.setStorageSync("token",res.data.data.token)
							uni.setStorageSync("userName",res.data.data.userName)
							return resolve()
						},
						fail(failMsg){
							console.log(failMsg)
							return reject(failMsg)
						}
					})
				}
			})
		}
	})
	
}

export function httpOFPost(path, params = {}, loading = true,method) {
	checkLogin().then(()=>{
	if (false) {
		uni.showLoading({
			title: "加载中",	
			mask: true
		});
	};

	return new Promise((resolve, reject) => {
		uni.request({
			header: {
				token: uni.getStorageSync("token") || ""
			},
			url: BASE_URL + path,
			method:method,
			data: params,
			async success(res) {
				// uni.hideLoading();
				resolve(res.data);
				// res.data?.code表示先判断res.data是否为null或undefined，
				//如果不是，则访问其code属性。这样可以有效避免在对象为null或undefined时造成的错误
				if (res.data?.code == -1) {
					uni.showToast({
						icon: "error",
						duration: 2000,
						title: res.data.errMsg
					});
					reject(res.data)
				}
				else if(res.data?.code == -99){
					uni.removeStorageSync("token")
					uni.showToast({
						icon: "error",
						duration: 2000,
						title: "令牌失效"
					});
					reject(res.data)
				}

			},
			fail(err) {
				reject(err);
			},
			complete() {
				// uni.hideLoading();    // 在showToast之前执行会受影响
			}
		});
	});
	})
};

// 封装发送get请求
export function httpOFGet(path,loading = true){
	checkLogin()
	// console.log('%c请求拦截：', ' background:orange',path);
	if(false){
		uni.showLoading({
			title:"加载中",
			mask:true
		})
	};
	
	return new Promise((resolve,reject)=>{
		uni.request({
			url:BASE_URL + path,
			method:"GET",
			header:{
				"token":uni.getStorageSync("token") || "",
			},
			timeout:60000,
			async success(res){
				// uni.hideLoading()
				resolve(res.data) // 将响应数据返回
				// console.log('响应拦截：', path,res.data);
				if(res.data?.code == -1){
					uni.showToast({
						icon:"fail",
						title:res.data.errMsg,
						duration:2000
					});
				}
				else if(res.data?.code == -99){
					uni.removeStorageSync("token")
					uni.showToast({
						icon: "error",
						duration: 2000,
						title: "令牌失效"
					});
					reject(res.data)
				}
			},
			fail(err){
				// uni.hideLoading();
				uni.showToast({
					icon:"fail",
					title:"服务器错误，请稍后再试",
					duration:1200
				})
				reject(err);
			},
		})
	});
} 

// 封装发送get请求
export function httpOfGetWithNotToken(path,loading = false){
	if(false){
		uni.showLoading({
			title:"加载中",
			mask:true
		})
	};
	
	return new Promise((resolve,reject)=>{
		uni.request({
			url:BASE_URL + path,
			method:"GET",
			timeout:1000*30,
			async success(res){
				// uni.hideLoading()
				resolve(res.data) // 将响应数据返回
				if(res.data?.code == -1){
					uni.showToast({
						icon:"fail",
						title:res.data.errMsg,
						duration:2000
					});
				};
			},
			fail(err){
				// uni.hideLoading();
				uni.showToast({
					icon:"fail",
					title:"服务器错误，请稍后再试",
					duration:1200
				})
				reject(err);
			},
		})
	});
} 