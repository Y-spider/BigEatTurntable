<template>
	<view class="content">
		<image class="logo"></image>
		<view class="text-area">
			<text class="title">欢迎使用吃货大转盘</text>
		</view>
	</view>
</template>

<script>
	import {logInAPI} from "@/apis/userApi.js";
	export default {
		data() {
			return {
				title: 'Hello'
			}
		},
		onShow() {
			let token = uni.getStorageSync("token")
			console.log("token",token)
			if(token){
				// 表示已经登录
			}
			else{
				this.login()
			}
		},
		methods: {
			login(){
				uni.login({
					async success(res) {
						logInAPI({"code":res.code})
						.then((logRes)=>{
							uni.setStorageSync("token",logRes.data)
						})
					}	
				})
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.logo {
		height: 200rpx;
		width: 200rpx;
		margin-top: 200rpx;
		margin-left: auto;
		margin-right: auto;
		margin-bottom: 50rpx;
	}

	.text-area {
		display: flex;
		justify-content: center;
	}

	.title {
		font-size: 36rpx;
		color: #8f8f94;
	}
</style>
