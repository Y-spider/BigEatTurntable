<template>
	<view>
		<cu-custom :isBack="true" :backUrl="backUrl">
			<block slot="backText">返回</block>
			<block slot="content">{{turntableInfo.title}}</block>
		</cu-custom>
		<!--  转盘详情页 -->
		<view class="turntable-box">
			<Turntable :turntable="turntableInfo" @routeDone="getRouteResult" :prizeList="prizeList"
				style="margin: 50rpx;"></Turntable>
			<view class="fun-button" style="display: flex;justify-content: space-between; align-items: center;">
				<view class="fun-but">
					<button open-type="share" class="cu-btn bg-gradual-green shadow"> <text class="cuIcon-share"
							style="margin: 0 10rpx;"></text>分享</button>
				</view>
				<view class="fun-but">
					<button @click="handleExportData" class="cu-btn bg-yellow shadow cuIcon-down"> <text
							style="margin: 0 10rpx;"></text>导出</button>
				</view>
				<view v-if="isShowEditButton" class="fun-but">
					<button class="cu-btn bg-red shadow" @click="goEdit"> <text class="cuIcon-edit"
							style="margin: 0 10rpx;"></text> 编辑</button>
				</view>
			</view>
		</view>
		<!-- 分享弹框 -->
		<share-pop-dialog ref="sharePopDialogRef" />
		<!-- 广告区域,到时候占用到这里~~~ -->
		<!-- 旋转记录列表 -->
		<!-- 广告区域 -->

		<!-- 旋转记录列表 -->
		<!-- 旋转记录列表（可滚动） -->
		<view class="record-container">
			<view class="record-header">
				<text class="record-title">旋转记录</text>
				<view class="refresh-btn" @click="refreshSpinRecords">
					<text class="cuIcon-refresh"></text> 刷新
				</view>
			</view>

			<scroll-view scroll-y class="record-scroll" @scrolltolower="loadMoreRecord">
				<view v-if="spinRecords.length === 0" class="record-empty">
					暂无记录
				</view>
				<view v-else>
					<view v-for="(item, index) in spinRecords" :key="index" class="record-item">
						<view class="record-top">
							<text v-if="!item.isMy">星友{{ item.userName }}</text>
							<text class="my-record" v-else>我(自己)</text>
							<text>{{ item.result }}</text>
						</view>
						<view class="record-time">{{ item.createTime }}</view>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
	</view>
</template>

<script>
	import Turntable from "@/components/Turntable.vue";
	import sharePopDialog from "@/components/share_pop_dialog.vue";
	import {
		getTurntableDetailAPI
	} from "@/apis/turntableApi.js";
	import {
		listDishRandomAPI
	} from "@/apis/dishApi.js";
	import {
		listSingleTurntableRecordAPI
	} from "@/apis/rotationRecordApi.js";
	import {
		getOpenidAPI
	} from "@/apis/userApi.js";
	export default {
		components: {
			Turntable,
			sharePopDialog
		},
		data() {
			return {
				tableName: "",
				id: null,
				prizeList: [],
				prize: null, // 奖品
				turntableInfo: {},
				dishTypeId: null,
				isShowEditButton: true,
				backUrl: "",
				spinRecords: [], // ✅ 新增，用来保存旋转记录
				refreshTimer: null,
				isShare: false, // 标识是否是分享操作
				refreshInterval: 1000 * 3, // 每隔3s刷新一次或者手动刷新
				openid: null,
				isSuccessGetTurntableInfo:false
			}
		},
		onHide() {
			this.clearAutoRefresh();
		},
		onUnload() {
			this.clearAutoRefresh();
		},
		onShow() {
			this.init()
		},
		async onShareAppMessage() {
			let expireTime = Date.now() + 30 * 60 * 1000;
			uni.setStorageSync("hasPermissionCheckDetail", {
				expireTime
			})
			if (!this.openid) {
				const res = await getOpenidAPI();
				this.openid = res.data;
			}
			return {
				title: this.tableName,
				path: `/pages/detail/detail?id=${this.id}&tableName=${this.tableName}&backUrl=/pages/index/index&shareOpenid=${this.openid}`,
				withShareTicket: true
			}
		},
		methods: {
			handleExportData() {
				let content = "";
				this.prizeList.forEach(prize => {
					content += prize.fonts[0].text + "\n";
				});

				uni.setClipboardData({
					data: content,
					success(res) {
						uni.showToast({
							duration: 1200,
							title: "导出到剪切板"
						})
					}
				})

			},
			// ✅ 新增刷新操作
			async refreshSpinRecords() {
				uni.showLoading({
					title: "加载中..."
				});
				await this.loadSpinRecords();
				uni.hideLoading();
				uni.showToast({
					title: "刷新成功",
					icon: "success"
				});
			},
			// ✅ 新增：启动自动刷新
			startAutoRefresh(interval) {
				// 先清理旧定时器，避免重复
				this.clearAutoRefresh();
				if(!this.isSuccessGetTurntableInfo){
					this.init();

				}
				this.refreshTimer = setInterval(() => {
					this.loadSpinRecords();
				}, interval);
			},

			// ✅ 新增：清除定时器
			clearAutoRefresh() {
				if (this.refreshTimer) {
					clearInterval(this.refreshTimer);
					this.refreshTimer = null;
				}
			},
			// ✅ 新增：调用后端接口获取记录
			async loadSpinRecords() {
				// 假设接口叫 getSpinRecordsAPI，你换成自己的
				if(!this.isSuccessGetTurntableInfo){
					this.init();
				}
				const res = await listSingleTurntableRecordAPI(this.id);
				if (!res) return;
				this.spinRecords = res.data;
			},
			async handleGenerateTurntableInfo() {
				// 根据随机获取的菜品列表，生成转盘信息
				const res = await listDishRandomAPI(6, this.dishTypeId);
				if (!res) return;
				const dishList = res.data;

				// 一些背景色池（可以自定义更多）
				const bgColors = [
					"#e9e8fe", "#b8c5f2", "#cf7e40", "#e2b29d",
					"#947975", "#6abf69", "#ffb347", "#87ceeb"
				];

				// 转换成转盘数据结构
				const turntableInfoContent = dishList.map((dish, index) => {
					return {
						fonts: [{
							text: dish.name, // 菜品名字
							top: "10%",
							lineClamp: 2
						}],
						id: dish.id,
						isMake: dish.isMake,
						background: bgColors[index % bgColors.length], // 循环取颜色
						lineClamp: 2,
						range: 1,
					};
				});
				// 存到本地变量/状态中（比如 this.turntableInfo）
				this.prizeList = turntableInfoContent;
				this.turntableInfo.title = this.tableName + "菜品"; // 设置转盘名称
			},

			handleShowMake() {
				// 查看菜品制作页面
				const checkPermision = uni.getStorageSync("hasPermissionCheckDetail");
				if (!checkPermision || checkPermision?.expireTime <= Date.now()) {
					this.$refs.sharePopDialogRef.open();
				} else {
					uni.navigateTo({
						url: "/pages/dish_detail/dish_detail?id=" + this.prize.id
					})
				}
			},
			goEdit() {
				let isSystem = true
				uni.setStorageSync('editPrizeList', this.prizeList)
				uni.navigateTo({
					url: `/pages/editWheel/index?turntableName=${this.tableName}&id=${this.id}&isSystem=${isSystem}`
				})
			},
			getRouteResult(prize) {
				this.prize = prize
			},
			async init() {
				if (isNaN(this.dishTypeId)) {
					let res = await getTurntableDetailAPI(this.id)
					if(!res){this.isSuccessGetTurntableInfo = false;return;};
					this.turntableInfo = res.data
					this.prizeList = JSON.parse(res.data.content)
					this.loadSpinRecords(); // 调用获取旋转记录
					this.isShowEditButton = true;
					this.isSuccessGetTurntableInfo = true;
				} else {
					this.isShowEditButton = false;
					// 处理随机菜单转盘
					this.handleGenerateTurntableInfo();
				}
			}
		},
		onLoad(option) {
			this.id = parseInt(option.id)
			this.dishTypeId = parseInt(option.dishTypeId)
			this.tableName = option.tableName
			this.backUrl = option.backUrl ? option.backUrl : this.backUrl
			this.isShare = option.isShare ? option.isShare : this.isShare
			// TODO: 将当前分享的转盘好友也可以保存在自己的账户
			// TODO: 好友可以互相在线编辑 websocket编辑
			this.startAutoRefresh(this.refreshInterval);
			if (option.shareOpenid) {
				// 表示当前用户为用户邀请的用户(也可能是老用户,只管传递至于新老用户由后端判断)
				uni.setStorageSync("shareOpenid", option.shareOpenid);
			}

		}

	}
</script>

<style scoped>
	.record-container {
		margin: 40rpx 30rpx 0;
	}

	.record-title {
		font-weight: bold;
		font-size: 30rpx;
		margin-bottom: 20rpx;
	}

	.record-scroll {
		max-height: 300rpx;
		/* 可自调高度，比如 300~500rpx */
		overflow-y: scroll;
	}

	.record-empty {
		text-align: center;
		color: #888;
		font-size: 26rpx;
		padding: 30rpx 0;
	}

	.record-item {
		padding: 20rpx 0;
		border-bottom: 1px solid #f0f0f0;
	}

	.record-top {
		display: flex;
		justify-content: space-between;
		font-size: 28rpx;
	}

	.record-time {
		color: #888;
		font-size: 24rpx;
		margin-top: 8rpx;
	}

	/* 刷新按钮 */
	.record-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.refresh-btn {
		font-size: 26rpx;
		color: #2e8b57;
		padding: 10rpx 20rpx;
		border: 1px solid #2e8b57;
		border-radius: 8rpx;
		display: flex;
		align-items: center;
	}

	.refresh-btn text {
		margin-right: 8rpx;
	}

	.my-record {
		color: #ff9800;
		/* 你的小程序主题色 */
		font-weight: bold;
	}
</style>