<template>
	<view class="user-edit-page bg-white">
		<!-- 自定义导航栏 -->
		<cu-custom :isBack="true">
			<block slot="content">修改个人信息</block>
		</cu-custom>

		<!-- 用户头像 -->
		<view class="cu-form-group mt-2">
			<view class="title">头像</view>
			<button class="cu-btn bg-white flex align-center avatar-btn" open-type="chooseAvatar"
				@chooseavatar="chooseAvatar">
				<view class='cu-avatar round xl' style="margin-top: -40rpx;">
					<text v-if="!userInfo.avatar" class="cuIcon-people"></text>
					<image class="cu-avatar round xl" v-else :src="userInfo.avatar"></image>
				</view>
			</button>
		</view>

		<!-- 用户昵称 -->
		<view class="cu-form-group">
			<view class="title">昵称</view>
			<view>
				<input class="cu-btn bg-white flex align-center nickname-btn" type="nickname" placeholder="请输入昵称"
					@change="chooseNickname" :value="userInfo.name">

				</input>
			</view>
		</view>

		<!-- 邮箱绑定 -->
		<view class="cu-form-group">
			<view class="title">邮箱</view>
			<input class="input" type="text" placeholder="请输入邮箱" v-model="userInfo.email" />
		</view>

		<view class="p-4" style="display: flex;justify-content: center;">
			<button class="cu-btn block bg-blue lg round" @tap="saveUserInfo"
				style="width: 80vw; background-color: #ffa500;">
				保存信息
			</button>
		</view>
	</view>
</template>

<script>
	import {
		getUserInfoAPI,
		updateUserInfoAPI
	} from "@/apis/userApi.js";

	export default {
		data() {
			return {
				userInfo: {
					avatar: "",
					nickname: "",
					email: ""
				}
			};
		},
		onLoad() {
			this.init();
		},
		methods: {
			// 初始化用户信息
			async init() {
				try {
					const res = await getUserInfoAPI();
					if (res.code === 200) {
						this.userInfo = res.data || {};
					}
				} catch (e) {
					console.error(e);
				}
			},

			// 选择头像（微信API）
			chooseAvatar(e) {
				const {
					avatarUrl
				} = e.detail;
				this.userInfo.avatar = avatarUrl;
			},

			// 选择昵称（微信API）
			chooseNickname(e) {

				this.userInfo.name = e.detail.value
			},

			// 验证邮箱格式
			checkEmail(email) {
				const emailReg = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
				return emailReg.test(email);
			},

			// 保存信息
			async saveUserInfo() {
				if (!this.userInfo.name) {
					uni.showToast({
						title: "请设置昵称",
						icon: "none"
					});
					return;
				}
				if (this.userInfo.email) {
					if (!this.checkEmail(this.userInfo.email)) {

						uni.showToast({
							title: "邮箱格式不正确",
							icon: "none"
						});
						return;
					}
				}
				if (this.userInfo.avatar.includes("tmp")) {
					const avatarRes = await this.$uploadAvatar(this.userInfo.avatar);
					this.userInfo.avatar = avatarRes.data.url;
				}
				const res = await updateUserInfoAPI(this.userInfo);
				if (!res) return;
				uni.setStorageSync("avatar", this.userInfo.avatar)
				uni.setStorageSync("userName", this.userInfo.name)
				uni.setStorageSync("email", this.userInfo.email)
				
				uni.showToast({
					title: "修改成功"
				})
				uni.navigateBack()
			}
		}
	};
</script>

<style scoped>
	.user-edit-page {
		min-height: 100vh;
		background-color: #f8f8f8;
	}

	/* 表单通用样式 */
	.cu-form-group {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 24rpx;
		border-bottom: 1px solid #eee;
		background: #fff;
	}

	.title {
		font-size: 30rpx;
		color: #333;
		width: 150rpx;
	}

	/* 按钮样式 */
	.avatar-btn,
	.nickname-btn {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		border: none;
		padding: 0;
	}

	/* 头像 */
	.avatar {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		margin-right: 10rpx;
	}

	/* 昵称 */
	.nickname {
		font-size: 28rpx;
		color: #555;
	}

	/* 输入框 */
	.input {
		flex: 1;
		text-align: right;
		font-size: 28rpx;
		color: #555;
	}

	button {
		margin-top: 40rpx;
	}
</style>