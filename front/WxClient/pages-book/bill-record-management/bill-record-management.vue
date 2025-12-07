<!-- 账单导入导出功能/暂时支持导出 -->
<template>
	<view>
		<cu-custom :isBack="true">
			<block slot="content">账单导出</block>
		</cu-custom>
		<form>
			<view class="cu-form-group">
				<view class="title">账本选择</view>
				<picker @change="pickerChange" :value="index" :range="bookList" range-key="title">
					<view class="picker">
						{{bookList[index].title}}
					</view>
				</picker>
			</view>
			<view class="cu-form-group">
				<view class="title">账单类型</view>
				<picker @change="pickerChangeOfType" :value="indexType" :range="typeList">
					<view class="picker">
						{{typeList[indexType]}}
					</view>
				</picker>
			</view>
			<view class="cu-form-group">
				<view class="title">开始日期</view>
				<picker mode="date" :value="date" @change="dateChangeOfStart">
					<view class="picker">
						{{exportParams.startTime}}
					</view>
				</picker>
			</view>
			<view class="cu-form-group">
				<view class="title">结束日期</view>
				<picker mode="date" :value="date" @change="dateChangeOfEnd">
					<view class="picker">
						{{exportParams.endTime}}
					</view>
				</picker>
			</view>
		</form>
		<view @click="handleExport"
			style="width: 80vw; margin: 10vw; padding: 30rpx; border-radius: 15rpx; background-color: #ffa500; font-weight: bold; color: white; font-size: large;"
			class="flex justify-center align-center">
			导出
		</view>
		<view style="margin-top: 30rpx; background-color: white; padding: 10rpx; gap: 5rpx; flex-direction: column;"
			class="flex">
			<view style="font-size: large; font-weight: bold;">账单导出规则</view>
			<view style="tips-item">1. 导出的账单会以excel附件发送到账号绑定邮箱</view>
			<view style="tips-item">2. 为了运营维护成本，每次导出需要观看广告，感谢您的支持</view>
			<view style="tips-item">3. 导出的账单服务器不会保存，请放心使用</view>
		</view>
	</view>
</template>

<script>
	import {
		getUserInfoAPI
	} from '../../apis/userApi';
	import {
		listAllBillBookAPI,
		exportBillRecordAPI,
		getChoosedBillBookAPI
	} from '../../apis/billApi';
	export default {
		data() {
			return {
				index: 0,
				indexType: 0,
				typeList: ["全部", "支出", "收入"],
				userInfo: {},
				bookList: [],
				exportParams: {
					bookId: -1,
					inOutType: "全部",
					startTime: "",
					endTime: "",
					rewardAd: null,
				},
				adId:"adunit-7dbb304ee73b2157"
			}
		},
		onLoad() {
			this.init();
			this.initRewardAd();
		},
		methods: {
			// 观看完广告后的回调
			async onAdFinished() {
			    uni.showLoading({
			        mask: true,
			        title: "生成中..."
			    })
			
			    try {
			        const res = await exportBillRecordAPI(this.exportParams)
			        uni.showToast({
			            title: "注意查收邮件！",
			            icon: "success"
			        });
			    } catch (e) {
			        console.error("导出异常：", e)
			        uni.showToast({
			            title: "生成失败，请稍后再试",
			            icon: "error"
			        })
			    } finally {
			        // 最重要：确保任何情况下都关闭 loading
			        uni.hideLoading()
			    }
			},

			// 用户点击按钮时播放广告
			showRewardAd() {
				if (this.rewardAd) {
					this.rewardAd.show().catch(() => {
						// 如果没加载好就重新加载再播放
						this.rewardAd.load()
							.then(() => this.rewardAd.show())
							.catch(err => {
								console.error("激励广告显示失败", err);
								uni.showToast({
									title: "广告暂不可用",
									icon: "none"
								});
							})
					})
				}
			},
			initRewardAd() {
				if (uni.createRewardedVideoAd) {
					this.rewardAd = uni.createRewardedVideoAd({
						adUnitId: this.adId // 替换你的广告 ID
					});

					// 广告加载成功
					this.rewardAd.onLoad(() => {
						console.log("激励广告加载成功");
					});

					// 广告加载失败
					this.rewardAd.onError(err => {
						console.error("激励广告加载失败:", err);
					});

					// 用户关闭广告回调
					this.rewardAd.onClose(res => {
						if (res.isEnded) {
							// 用户完整观看
							console.log("用户完整观看广告，给予奖励");
							this.onAdFinished();
						} else {
							// 中途关闭
							uni.showToast({
								title: "观看未完成,导出失败",
								icon: "none"
							});
						}
					});
				}
			},
			async handleExport() {
				const _this = this;
				this.exportParams.bookId = this.bookList[this.index].id;
				this.exportParams.inOutType = this.typeList[this.indexType];
				uni.showModal({
					content:"为了能够持续运营，导出需要观看视频，十分感谢!",
					success(option){
						if(option.confirm){
							_this.showRewardAd();
							// _this.onAdFinished();	
						}else{
							
						}
					}
				})
			},
			dateChangeOfEnd(change) {
				this.exportParams.endTime = change.detail.value;
			},
			dateChangeOfStart(change) {
				this.exportParams.startTime = change.detail.value;
			},
			pickerChangeOfType(picker) {
				this.indexType = parseInt(picker.detail.value);
			},
			pickerChange(picker) {
				this.index = parseInt(picker.detail.value);
			},
			async init() {
				uni.showLoading({
					mask:true,
					title:"加载中"
				})
				const resUser = await getUserInfoAPI();
				const resBook = await listAllBillBookAPI();
				const resChooseBook = await getChoosedBillBookAPI();
				if (!resUser || !resBook || !resChooseBook) {
					uni.hideLoading();
					return;
				}
				this.userInfo = resUser.data;
				this.bookList = resBook.data;
				if (!this.userInfo.email) {
					uni.showModal({
						content: "请先绑定邮箱!",
						success(option) {
							if (option.confirm) {
								uni.navigateTo({
									url: "/pages/home/user_modify"
								})
							} else {
								uni.navigateBack();
							}
						}
					})
				}
				const time = new Date();
				this.exportParams.startTime = `${time.getFullYear()}-${time.getMonth()+1}-1`;
				this.exportParams.endTime = `${time.getFullYear()}-${time.getMonth()+1}-${time.getDate()}`;
				this.exportParams.bookId = resChooseBook.data.id;
				this.index = this.bookList.findIndex(book => book.id == this.exportParams.bookId)
				uni.hideLoading();
			}
		}
	}
</script>

<style scoped>
	.tips-item {
		font-size: small;
		color: lightgray;
	}
</style>