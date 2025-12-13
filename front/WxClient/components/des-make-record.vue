<template>
	<view>
		<view class="cu-modal" :class="modalName=='Modal'?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view style="position: absolute; left: 20rpx;" @click="showTips">
						<text class="cuIcon-question text-lg" style="color: #ffa500;"></text>
					</view>
					<view class="content">账单录入</view>
					<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>

				<view class="padding-xl">
					<textarea v-model="description" :maxlength="200" placeholder="请输入账单描述，最多200字"
						style="width:100%; height:200rpx; padding:10rpx; border:1px solid #ddd; border-radius:5rpx;"></textarea>

					<view class="btns">
						<button class="btn1" @click="submitRecord" type="primary" style="background-color: #ffa500;">提交</button>
					</view>
				</view>
			</view>
		</view>

		<reward-ad ref="rewardAd"></reward-ad>
	</view>
</template>

<script>
	import rewardAd from './reward-ad.vue';
	import {
		makeBillRecordByDesWithAiAPI
	} from "@/apis/billApi.js";
	export default {
		name: "TextRecordModal",
		components: {
			rewardAd
		},
		props: {
			// 可通过父组件控制弹窗是否显示
			visible: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				modalName: "",
				description: ""
			};
		},
		watch: {
			visible(val) {
				this.modalName = val ? "Modal" : "";
				if (!val) this.description = "";
			}
		},
		methods: {
			// 检查今天是否看过广告
			checkTodayWatched() {
				const today = new Date().toDateString();
				return uni.getStorageSync("des-see-ad-time") === today;
			},

			showTips() {
				uni.showModal({
					content: "为了持续提供优质服务，使用此功能前需观看一次视频广告，每天观看一次即可解锁当天无限次使用权限。建议使用此功能，语音功能耗时较长！",
					showCancel: false
				});
			},

			open() {
				this.modalName = "Modal";
				this.description = "";
			},
			hideModal() {
				this.modalName = "";
				this.$emit("update:visible", false);
			},

			submitRecord() {
				try {
					const _this = this;
					if (!this.checkTodayWatched()) {
						return uni.showModal({
							content: "使用此功能前需观看一次视频广告，当天观看一次即可无限次使用。",
							success(res) {
								if (res.confirm) {
									_this.$refs.rewardAd.showAd("adunit-7dbb304ee73b2157")
										.then(() => {
											uni.setStorageSync("des-see-ad-time", new Date().toDateString());
											_this._submitRecordReal();
										})
										.catch(err => uni.showToast({
											title: err,
											icon: "none"
										}));
								}
							}
						});
					}
					this._submitRecordReal();
				} catch (err) {
					console.log("err", err);
				}
			},
			close(){
				this.modalName = "";
			},

			async _submitRecordReal() {
				if (!this.description.trim()) return uni.showToast({
					title: "请输入账单描述",
					icon: "none"
				});
				try {
					uni.showLoading({
						title: "提交中..."
					});
					const postData = {
						des: this.description
					}
					const res = await makeBillRecordByDesWithAiAPI(postData);
					if (!res) return;
					this.$emit("onFinish", res.data);
					uni.hideLoading();
				} catch (err) {
					uni.hideLoading();
					console.error(err);
				}

			}
		}
	}
</script>

<style scoped>
	.btns {
		display: flex;
		align-items: center;
		padding: 30rpx;
	}

	.btn1 {
		flex: 1;
	}
</style>