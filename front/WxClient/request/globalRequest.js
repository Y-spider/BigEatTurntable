// 正式环境
// const BASE_URL = "https://www.sunnygo.chat/turntable/api/"
// 开发环境
// const BASE_URL = "http://127.0.0.1:16378/"
const BASE_URL = "http://192.168.1.103:16378/"
let isCheckLogin = false
// 检查是否登录，如果没有登录则进行登录
function checkLogin(){
	return new Promise((resolve, reject) => {
		isCheckLogin = true;
		let token = uni.getStorageSync("token")
		if(token){
			isCheckLogin = false
			return resolve(token);
		}else{
			uni.login({
				success(res) {
					uni.showLoading({
						mask:true,
						title:"登录中..."
					})
					let postData = {"code":res.code}
					if(uni.getStorageSync("shareOpenid")){
						postData.shareOpenid = uni.getStorageSync("shareOpenid");
					}
					 uni.request({
						method:"POST",
						url:BASE_URL + "user/client/login",
						data:postData,
						success(res){
							console.log("登录成功")
							uni.setStorageSync("token",res.data.data.token)
							uni.setStorageSync("userName",res.data.data.userName)
							uni.setStorageSync("avatar",res.data.data.avatar)
							uni.setStorageSync("email",res.data.data.email)
							uni.setStorageSync("openMusic",true) // 默认开启音效
							return resolve()
						},
						fail(failMsg){
							console.log(failMsg)
							return reject(failMsg)
						},
						complete(){
							isCheckLogin = false;
							uni.hideLoading();
						}
					})
				}
			})
		}
	})
}

export function httpOFPost(path, params = {}, loading = true,method) {
	return checkLogin().then(()=>{
	return new Promise((resolve, reject) => {
		uni.request({
			header: {
				"token": uni.getStorageSync("token") || ""
			},
			url: BASE_URL + path,
			method:method,
			data: params,
			async success(res) {
				// res.data?.code表示先判断res.data是否为null或undefined，
				//如果不是，则访问其code属性。这样可以有效避免在对象为null或undefined时造成的错误
				if (res.data?.code == -1) {
					uni.showToast({
						icon: "error",
						duration: 2000,
						title: res.data.errMsg
					});
					reject(res.data)
					return false;
				}
				else if(res.data?.code == -99){ // -99 token失效需要重新登录
					uni.removeStorageSync("token")
					uni.showToast({
						icon: "error",
						duration: 2000,
						title: "令牌失效"
					});
					reject(res.data)
					return false;
				}
				else if(res.data?.code !== 200){
					uni.showToast({
						icon: "error",
						duration: 2000,
						title: "系统错误"
					});
					reject(res.data)
					return false;
				}
				// uni.hideLoading();
				resolve(res.data);
				return true;
			},
			fail(err) {
				reject(err);
				return false;
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
	return checkLogin().then(()=>{
		return new Promise((resolve,reject)=>{
			uni.request({
				url:BASE_URL + path,
				method:"GET",
				header:{
					"token":uni.getStorageSync("token") || "",
				},
				timeout:60000,
				async success(res){
					if(res.data?.code == -1){
						uni.showToast({
							icon:"fail",
							title:res.data.errMsg,
							duration:2000
						});
						return false;
					}
					else if(res.data?.code == -99){
						uni.removeStorageSync("token")
						uni.showToast({
							icon: "error",
							duration: 2000,
							title: "令牌失效"
						});
						reject(res.data)
						return false;
					}
					else if(res.data?.code !== 200){
						uni.showToast({
							icon: "error",
							duration: 2000,
							title: "系统错误"
						});
						reject(res.data)
						return false;
					}
					resolve(res.data) // 将响应数据返回
					return true;
				},
				fail(err){
					uni.showToast({
						icon:"fail",
						title:"服务器错误，请稍后再试",
						duration:1200
					})
					reject(err);
					return false;
				},
			})
		});
	})
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