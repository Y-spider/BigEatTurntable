<!-- 语音录入，创建账单 -->
<template>
	<view>
		<view class="cu-modal" :class="modalName=='Modal'?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view style="position: absolute; left: 20rpx;" @click="showTips">
						<text class="cuIcon-question text-lg" style="color: #ffa500;"></text>
					</view>
					<view class="content">语音录入</view>
					<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-red"></text>
					</view> 
				</view>

				<view class="padding-xl">

					<!-- 音量频谱 -->
					<view class="body">
						<nest-jumping-spectrum 
							sColor="#ffa500" 
							:sWidth="500" 
							:spectrumHeightMax="100" 
							ref="jumpingSpectrum">
						</nest-jumping-spectrum>
					</view>

					<!-- 按钮 -->
					<view class="btns">
						<button 
							v-if="showStart" 
							class="btn1" 
							@click="startRecord" 
							type="primary"
							style="background-color: #ffa500;"
						>开始</button>

						<button 
							v-else 
							class="btn2" 
							@click="stopRecord" 
							type="warn"
						>结束</button>
					</view>
				</view>
			</view>
		</view>
		<reward-ad ref="rewardAd"></reward-ad>
	</view>
</template>

<script>
	import rewardAd from './reward-ad.vue';
export default {
	components:{
		rewardAd
	},
	data() {
		return {
			modalName: "",
			showStart: true,

			recorder: null,
			recordFilePath: "", // 录音文件路径
		}
	},

	methods: {
		 checkTodayWatched() {
			const today = new Date().toDateString();
			const lastTime = uni.getStorageSync("voice-see-ad-time") || "";
		
			return lastTime === today;
		},
		showTips() {
		    uni.showModal({
		        content: "为了持续为您提供优质服务，使用此功能前需观看一次视频。每天观看一次即可解锁当天的无限次使用权限。\n 使用语音解析，平均时长在10~30s解析耗时间",
		        showCancel: false
		    });
		},

		/* ----------------------
			打开弹窗
		---------------------- */
		open() {
			this.showStart = true;
			this.modalName = "Modal";
			this.recordFilePath = "";
		},
		close(){
			this.recordFilePath = "";
			this.modalName = "";
		},

		/* ----------------------
			关闭弹窗
		---------------------- */
		hideModal() {
			this.$refs.jumpingSpectrum.stopJumping();
			if (this.recorder) this.recorder.stop();
			this.modalName = "";
		},

		/* ----------------------
			开始录音
		---------------------- */
		startRecord() {
			const _this = this;
		
			// 广告未看 → 先弹提示
			if (!this.checkTodayWatched()) {
				return uni.showModal({
					content: "为了持续为您提供优质服务，使用此功能前需观看一次视频。每天一次即可无限使用语音功能。",
					success(res) {
						if (res.confirm) {
							// 播放广告
							_this.$refs.rewardAd
								.showAd("adunit-7dbb304ee73b2157")
								.then(() => {
									// 广告观看成功 → 开始录音
									_this._startRecordReal();
									uni.setStorageSync("voice-see-ad-time",new Date().toDateString());
								})
								.catch((err) => {
									uni.showToast({
										title: err,
										icon: "none"
									});
								});
						}
					}
				});
			}
		
			// 已观看广告 → 直接开始录音
			this._startRecordReal();
		},
_startRecordReal() {
	// 初始化录音管理器
	this.recorder = uni.getRecorderManager();

	// 录音开始
	this.recorder.onStart(() => {
		this.showStart = false;
		this.$refs.jumpingSpectrum.startJumping();
	});

	// 录音错误
	this.recorder.onError((err) => {
		console.error("录音失败:", err);
		uni.showToast({ title: "录音失败", icon: "none" });
		this.showStart = true;
		this.$refs.jumpingSpectrum.stopJumping();
	});

	// 录音结束
	this.recorder.onStop((res) => {
		this.recordFilePath = res.tempFilePath;
		this.$refs.jumpingSpectrum.stopJumping();
		this.showStart = true;

		// 自动上传录音
		this.uploadVoice();
	});

	// 开始录音
	this.recorder.start({
		duration: 60000, // 最长 60 秒
		format: "mp3"
	});
},

		/* ----------------------
			主动停止录音
		---------------------- */
		stopRecord() {
			if (this.recorder) {
				this.recorder.stop();
			}
		},

		/* ----------------------
			上传录音文件
		---------------------- */
		uploadVoice() {
			if (!this.recordFilePath) {
				return uni.showToast({ title: "录音文件不存在", icon: "none" });
			}

			uni.showLoading({ title: "上传中..." });

			// 使用 uni.uploadFile 上传
			uni.uploadFile({
				url: "http://192.168.1.104:16378/book/record/add/voice",  
				filePath: this.recordFilePath,
				name: "file",
				header:{
					"token":uni.getStorageSync("token")
				},
				success: (res) => {
					uni.hideLoading();
					let data = {};
					try {
						data = JSON.parse(res.data);
					} catch (e) {
						data = res.data;
					}

					// 后端自行处理返回内容
					uni.showToast({
						title: data?.msg || "上传成功",
						icon: "success",
					});

					// 上传成功后你可以触发父组件回调
					this.$emit("voiceFinish", data);
				},
				fail: (err) => {
					uni.hideLoading();
					console.error("上传失败:", err);
					uni.showToast({
						title: "上传失败",
						icon: "none"
					});
				}
			});
		},
	},
}
</script>

<style scoped>
.body {
	height: 200rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.btns {
	display: flex;
	align-items: center;
	padding: 30rpx;
}

.btn1 {
	flex: 1;
	margin-right: 15rpx;
}

.btn2 {
	flex: 1;
	margin-left: 15rpx;
}
</style>
