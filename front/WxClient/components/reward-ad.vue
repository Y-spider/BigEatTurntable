<!-- 激励视频观看组件 -->
<template>
	<view></view>
</template>

<script>
export default {
	name: "reward-ad",
	data() {
		return {
			videoAd: null
		}
	},
	methods: {
		showAd(adId) {
			return new Promise((resolve, reject) => {
				// #ifdef MP-WEIXIN
				// 初始化激励广告实例
				if (!this.videoAd) {
					this.videoAd = wx.createRewardedVideoAd({
						adUnitId: adId
					})

					this.videoAd.onError(err => {
						console.error("激励视频加载错误:", err)
						reject('load_error')
					})

					this.videoAd.onClose(res => {
						if (res && res.isEnded) {
							uni.showToast({
								icon:"none",
								title:"今日成功解锁该功能！"
							})
							resolve("今日成功解锁功能")	// 完整观看
						} else {
							reject("取消后无法获取奖励")	// 未完整观看
						}
					})
				}

				// 拉起播放
				this.videoAd
					.load()
					.then(() => this.videoAd.show())
					.catch(err => {
						console.error("激励广告加载失败:", err)
						reject('show_error')
					})
				// #endif


				// #ifndef MP-WEIXIN
				// 其他平台（H5、APP），不支持激励广告，可直接返回观看失败
				uni.showToast({
					title: "当前平台不支持激励广告",
					icon: "none"
				})
				reject("not_support")
				// #endif
			})
		}
	}
}
</script>

<style></style>
