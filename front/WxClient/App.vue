<script>
	import Vue from "vue"
	export default {
		onLaunch: function() {
			uni.getSystemInfo({
				success: function(e) {
					// #ifndef MP
					Vue.prototype.StatusBar = e.statusBarHeight;
					if (e.platform == 'android') {
						Vue.prototype.CustomBar = e.statusBarHeight + 50;
					} else {
						Vue.prototype.CustomBar = e.statusBarHeight + 45;
					};
					// #endif
					// #ifdef MP-WEIXIN
					Vue.prototype.StatusBar = e.statusBarHeight;
					let custom = wx.getMenuButtonBoundingClientRect();
					Vue.prototype.Custom = custom;
					Vue.prototype.CustomBar = custom.bottom + custom.top - e.statusBarHeight;
					// #endif		
					// #ifdef MP-ALIPAY
					Vue.prototype.StatusBar = e.statusBarHeight;
					Vue.prototype.CustomBar = e.statusBarHeight + e.titleBarHeight;
					// #endif
				}
			});
			this.checkUpdate(); // 检查版本更新，强制用户更新
		},
		methods: {
			checkUpdate() {
				const updateManager = wx.getUpdateManager();
				// 监听是否有新版本
				updateManager.onCheckForUpdate((res) => {
					console.log("是否有新版本：", res.hasUpdate);
				});

				// 监听新版本下载完成
				updateManager.onUpdateReady(() => {
					wx.showModal({
						title: '更新提示',
						content: '新版本已经准备好，是否重启应用？',
						confirmText: '立即更新',
						showCancel: false,
						success: (res) => {
							if (res.confirm) {
								// 新版本已经下载好，调用 applyUpdate 应用新版本并重启
								updateManager.applyUpdate();
							}
						}
					});
				});
				// 监听新版本下载失败
				updateManager.onUpdateFailed(() => {
					wx.showModal({
						title: '更新失败',
						content: '新版本下载失败，请检查网络或稍后重试。'
					});
				});

			}
		},

		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log("App Hide");
		}
	}
</script>

<style>
	/*每个页面公共css */
	@import "./colorui/main.css";
	@import "./colorui/icon.css";
	@import "css/app.css"
</style>