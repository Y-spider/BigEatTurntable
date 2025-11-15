<template>
	<view>
		<cu-custom :isBack="true" :backUrl="backUrl">
			<block slot="backText">返回</block>
			<block slot="content">{{turntableInfo.title}} </block>
		</cu-custom>
		<!--  转盘详情页 -->
		<view class="turntable-box">
			<Turntable ref="turntable" :turntable="turntableInfo" @routeDone="getRouteResult" :prizeList="prizeList"
				style="margin: 50rpx;"></Turntable>
			<view class="fun-button"
				style="display: flex;justify-content: space-between; align-items: center;padding: 0rpx 10rpx;">
				<view v-if="turntableInfo.canEdit" class="fun-but">
					<button @click="modalName = 'bottomModal'" class="cu-btn bg-gradual-green shadow"> <text
							class="cuIcon-share" style="margin: 0 10rpx;"></text>分享</button>
				</view>
				<view v-else class="fun-but">
					<button open-type="share" class="cu-btn bg-gradual-green shadow"> <text
							class="cuIcon-share" style="margin: 0 10rpx;"></text>分享</button>
				</view>
				<view class="fun-but">
					<button @click="handleExportData" class="cu-btn bg-yellow shadow cuIcon-down"> <text
							style="margin: 0 10rpx;"></text>导出</button>
				</view>
				<!-- <view v-if="isShowEditButton" class="fun-but"> -->
				<view class="fun-but">
					<button class="cu-btn bg-red shadow" @click="goEdit"> <text class="cuIcon-edit"
							style="margin: 0 10rpx;"></text> 编辑</button>
				</view>
			</view>
		</view>
		<!-- 底部弹框 -->
		<view class="cu-modal bottom-modal" :class="modalName=='bottomModal'?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white">
					<view class="action text-blue"><button type="primary"
							style="color: #ffffff; font-size: small; border:none !important; background-color: none !important;"
							open-type="share">确定</button></view>
					<view class="action text-blue" @tap="hideModal" style="padding:0 30rpx">
						<button size="mini" type="default" >取消</button>
					</view>
				</view>
				<view class="padding-xl " style="height: 500rpx;">
					<view>
						<form>
							<view class="cu-form-group">
								<view class="title">开启次数限制</view>
								<switch :disabled="!turntableInfo.canEdit" class='orange radius' @change="handleSwitchLimit"
									:class="switchLimit?'checked':''" :checked="switchLimit?true:false"></switch>
							</view>
							<view v-if="switchLimit" class="cu-form-group">
								<view class="title">限制抽奖次数(每人)</view>
								<view  class="edit-bottom">
									<view class="bottom-item">
										<uni-number-box :disabled="!turntableInfo.canEdit" v-model="limitCount" min=""></uni-number-box>
									</view>
								</view>
							</view>
						</form>
					</view>
				</view>
			</view>
		</view>
		<!-- 分享弹框 -->
		<share-pop-dialog ref="sharePopDialogRef" />
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
							<text v-if="!item.isMy">{{ item.userName }}</text>
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
		getTurntableDetailAPI,updateTurantableLimitCountAPI
	} from "@/apis/turntableApi.js";
	import {
		listDishRandomAPI
	} from "@/apis/dishApi.js";
	import {
		listSingleTurntableRecordAPI,
		getSpinCountAPI
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
				spinCount:undefined,
				switchLimit: false,
				limitCount:1,
				modalName: "",
				tableName: "",
				id: null,
				prizeList: [],
				prize: null, // 奖品
				turntableInfo: {},
				isShowEditButton: true,
				backUrl: "",
				spinRecords: [], // ✅ 新增，用来保存旋转记录
				refreshTimer: null,
				refreshInterval: 1000 * 3, // 每隔3s刷新一次或者手动刷新
				openid: null,
				isSuccessGetTurntableInfo: false,
				shareOpenid: null,
				requestCount:1
			}
		},
		onHide() {
			this.clearAutoRefresh();
		},
		onUnload() {
			this.clearAutoRefresh();
		},
		onShow() {
			this.init();
			this.clearAutoRefresh();
			this.startAutoRefresh(this.refreshInterval);
		},
		async onShareAppMessage() {
			if(this.turntableInfo.canEdit){
				const postData = {
					id:this.turntableInfo.id,
					limitCount:this.limitCount
				}
				if(!this.switchLimit){
					postData.limitCount = 0
				}
				const updateRes = await updateTurantableLimitCountAPI(postData);
				if(!updateRes) return;
			}
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
				path: `/pages/detail/detail?id=${this.id}&backUrl=/pages/index/index&shareOpenid=${this.openid}`,
				withShareTicket: true
			}
		},
		methods: {
			
			handleSwitchLimit() {
				this.switchLimit = !this.switchLimit
			},
			hideModal() {
				this.modalName = "";
			},
			loadMoreRecord() {
				// 预留处理
			},
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
				this.init();
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
				// if (!this.isSuccessGetTurntableInfo) {
				// 	this.init();
				// }
				this.refreshTimer = setInterval(() => {
					if (this.turntableInfo && this.turntableInfo.type == 0) {
						this.init();
					}
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
				if (!this.isSuccessGetTurntableInfo) {
					this.init();
				}
				const res = await listSingleTurntableRecordAPI(this.id);
				if (!res) return;
				this.spinRecords = res.data;
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
				// this.$refs.turntable.forceFlush()
			},
			async init() {
				let res = await getTurntableDetailAPI(this.id)
				if (!res) {
					this.isSuccessGetTurntableInfo = false;
					return;
				};
				this.turntableInfo = res.data
				if (this.turntableInfo.type == 0 && this.shareOpenid) {
					this.isShowEditButton = false;
				}
				this.prizeList = JSON.parse(res.data.content)
				if(this.requestCount <= 1){
					this.switchLimit = this.turntableInfo.limitCount > 0;
					this.limitCount = this.turntableInfo.limitCount==0 ? 1 : this.turntableInfo.limitCount;
				}
				this.isSuccessGetTurntableInfo = true;
				if (this.turntableInfo.type == 0 && !this.turntableInfo.isRepeat) {
					this.prizeList.forEach(prize => {
						prize.fonts.forEach(f => {
							f.text = `剩余:${prize.count || 0}  -` + f.text
						})
					})
				}
				this.requestCount++;
			}
		},
		onLoad(option) {
			this.id = parseInt(option.id)
			this.backUrl = option.backUrl ? option.backUrl : this.backUrl
			// TODO: 将当前分享的转盘好友也可以保存在自己的账户
			// TODO: 好友可以互相在线编辑 websocket编辑
			this.init();
			this.startAutoRefresh(this.refreshInterval);
			if (option.shareOpenid) {
				this.shareOpenid = option.shareOpenid;
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