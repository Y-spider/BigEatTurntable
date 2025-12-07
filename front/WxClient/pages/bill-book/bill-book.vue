<template>
	<view style="max-height: 100vh;">
		<view class="bar" :style="{ height: customeBarHeight + 'px' }">
			<view style="display: flex; gap: 10rpx; color: white; align-items: center; margin-top: 100rpx;"
				@click="toBillBookPage">
				<text class="cuIcon-moreandroid"></text>
				<text style="font-weight: bolder;">{{chooseBook.title}}</text>
				<view style="color: #f3f3f3;font-size: 34rpx;">
					<text>切换账本</text>
					<text class="cuIcon-right"></text>
				</view>
			</view>
		</view>
		<!-- 头部模块，包含统计，邀请好友，选择列表三大部分完成 -->
		<view class="head-box">
			<view class="stastic-part" @click="modalName = 'Modal'">
				<view class="pay-out stastic-item">
					<view class="month-des">
						<text>记账笔数</text>
					</view>
					<view class="money-des">
						<text class="money-integer">{{summaryData.totalCount}}</text>
					</view>
				</view>
				<view style="width: 1rpx; min-height: 50%; background-color: white; color: #ffa500;">5</view>
				<view class="pay-out stastic-item">
					<view class="month-des">
						<text>{{getMonthOfChooseTime()}}月支出</text>
					</view>
					<view class="money-des">
						<text class="money-integer">{{getIntegerOfAmount(summaryData.totalExpense)}}</text>
						<text class="money-decimal">.{{getDecimalOfAmount(summaryData.totalExpense)}}</text>
					</view>
				</view>
				<view style="width: 1rpx; min-height: 50%; background-color: white; color: #ffa500;">5</view>
				<view class="pay-in stastic-item">
					<view class="month-des">
						<text>{{getMonthOfChooseTime()}}月收入</text>
					</view>
					<view class="money-des">
						<text class="money-integer">{{getIntegerOfAmount(summaryData.totalIncome)}}</text>
						<text class="money-decimal">.{{getDecimalOfAmount(summaryData.totalIncome)}}</text>
					</view>
				</view>
				<view style="width: 1rpx; min-height: 50%; background-color: white; color: #ffa500;">5</view>
				<view class="pay-profile stastic-item">
					<view class="month-des">
						<text>{{getMonthOfChooseTime()}}月结余</text>
					</view>
					<view class="money-des">
						<text class="money-integer">{{getIntegerOfAmount(summaryData.balance)}}</text>
						<text class="money-decimal">.{{getDecimalOfAmount(summaryData.balance)}}</text>
					</view>
				</view>
			</view>
			<view class="share-part">
				<view class="text-des" style="color: white;font-weight: bold;">同行好友</view>
				<view class="show-info">
					<!-- 好友展示列表 -->
					<view class="friend-list">
						<view style="background-color: white;" class="round" v-for="(user,index) in allUserList"
							:key="index">
							<image class='cu-avatar round sm' :src="user.avatar"></image>
						</view>
						<view class='cu-avatar round sm' style="background-color: white;">
							<view class="cuIcon-roundadd" style="color: lightgray; font-size: large;"></view>
						</view>
					</view>
					<button open-type="share"
						style="position: absolute;width: 100%; left: -24%; opacity: 0;">分享</button>
					<!-- 左侧抽屉展示 -->
					<view class="choose-user" @click="changeShowUser">
						<view style="color: white;font-weight: bold;">{{showUserInfo.name}} ▼</view>
					</view>
				</view>
			</view>
			<view class="fun-part">
				<view @click="toFunPage(item)" class="fun-item" v-for="(item,index) in funItemList" :key="index">
					{{item.des}}
				</view>
			</view>
			<view class="search-box" @click="toSearchPage">
				<uni-search-bar placeholder="搜索账单" style="width: 100%;" :readonly="true"></uni-search-bar>
			</view>
		</view>
		<!-- 账单记录部分 -->
		<view class="record-box">
			<view v-if="billRecordList.length > 0 " class="day-swiper-box" style="width: 100vw; ">
				<view class="record-card" v-for="(records,outerIndex) in billRecordList" :key="outerIndex"
					:title="records.date + records.week">
					<view class="card-head">
						<view>{{records.date }} {{records.week}} </view>
						<view class="extra" style="color: #b9b9b9; font-size: small; font-weight: bold;">
							{{handleExtra(records)}}
						</view>
					</view>
					<view class="card-body">
						<view style="display: flex; flex-direction: column;">
							<view @click="showDetaile(record.id)" class="record-item"
								v-for="(record,index) in itemRecordList[outerIndex]" :key="record.id">
								<view class="first-line">
									<view class="item-left">
										<!-- 消费类型ICON -->
										<view v-if="record.type == 'in'">
											<image :src="record.labelUrl" style="padding: 10rpx;"
												class="cu-avatar round bg-yellow">
											</image>
										</view>
										<view v-if="record.type == 'out'">
											<image :src="record.labelUrl" style="padding: 10rpx;"
												class="cu-avatar round bg-green">
											</image>
										</view>
										<!-- 消费类描述 -->
										<view>{{record.des}}</view>
										<!-- 消费用户名称 (共享账单才显示)-->
									</view>

									<view class="item-right flex justify-between align-center"
										style="font-weight: bold;flex: 1; gap:30rpx; border-bottom: 1rpx solid #c8c8c8;">
										<view class="item-right-r">
											<view>
												<text
													style="font-weight: bold; padding: 0 10rpx;">{{record.labelName}}</text>
												<text style="color: #ffa500;"> ({{record.name}}) </text>
											</view>
											<view>
												<view class="remark"
													style=" color: #8b8b8b; font-size: small;  padding: 5rpx 10rpx;">
													备注：{{record.remark || '无'}}</view>
											</view>
										</view>
										<view class="item-left-r flex align-center" style="gap: 10rpx;">
											<view :style="record.type === 'in' ? 'color:#ffa500;' : 'color:black;'">
												<text v-if="record.type==='out'">-</text>
												￥{{record.amount}}
											</view>
											<view>
												<image class="cu-avatar sm" style="background-color: white;"
													:src="record.methodUrl"></image>
											</view>
										</view>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>
				<!-- 底部提示 -->
				<view v-if="billRecordList.length > 0" class="foot-box"
					style="display: flex; padding: 30rpx; justify-content: center; align-items: center;">
					已经到底部了~~~
				</view>
			</view>
			<!-- 悬浮框 -->
			<view v-if="billRecordList.length>0">
				<uni-fab ref="fab" :pattern="pattern" :content="content" horizontal="right" vertical="bottom"
					direction="horizontal" @trigger="trigger" @fabClick="fabClick" />
			</view>
			<view v-else style="width: 100vw;" class="flex justify-center">
				<view style="color: white; background-color: #ffa500;" class="make-button-none" @click="makeBill">记一笔
				</view>
			</view>
		</view>
		<!-- 描述录入 -->
		<des-make-record ref="desMakeRecordRef" @onFinish="handleDesFinsh"></des-make-record>
		<!-- 语音录入 -->
		<voice-make-record ref="voiceMakeRecordRef" @voiceFinish="handleVoiceFinsh"></voice-make-record>
		<!-- 信息录入底部弹框 -->
		<make-bill-pop ref="makeBillPop" @onConfirm="handleConfirm"></make-bill-pop>
		<!-- 切换展示 用户弹框-->
		<change-show-user-dialog ref="changeShowUserDialog" @onChange="userChangeClllBack"></change-show-user-dialog>
		<!-- 月份选择框 -->
		<view class="cu-modal" :class="modalName=='Modal'?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="action" @tap="modalName = ''">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					<zqt-month-picker @change="handleChooseMonthChange" mode="date"></zqt-month-picker>
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	import voiceMakeRecord from '../../components/voice-make-record.vue';
	import makeBillPop from '../../components/make-bill-pop.vue';
	import zqtMonthPicker from '../../components/zqt-month-picker/zqt-month-picker.vue';
	import changeShowUserDialog from '../../components/change_show_user_dialog.vue';
	import desMakeRecord from '../../components/des-make-record.vue';
	import {
		getChoosedBillBookAPI,
		getMonthSummaryAPI,
		getMonthBillRecordListAPI,
		getCurrentBookAllUserAPI,
		joinShareBillBookAPI
	} from "@/apis/billApi.js";
	export default {
		components: {
			makeBillPop,
			zqtMonthPicker,
			changeShowUserDialog,
			voiceMakeRecord,
			desMakeRecord
		},
		data() {
			return {
				pattern: {
					color: '#ffa500',
					backgroundColor: '#fff',
					selectedColor: '#ffa500',
					buttonColor: '#ffa500',
					iconColor: '#fff'
				},
				content: [{
						iconPath: "/static/记一笔.svg",
						selectedIconPath: '/static/记一笔.svg',
						text: '记一笔',
						active: false
					},
					{
						iconPath: '/static/语音.svg',
						selectedIconPath: '/static/语音.svg',
						text: '语音记账',
						active: false
					},
					{
						iconPath: '/static/ai.svg',
						selectedIconPath: '/static/ai.svg',
						text: 'ai记账',
						active: false
					}
				],
				showUserInfo: {
					id: -1,
					name: "全部用户"
				}, // -1表示获取全部用户
				allUserList: [], // 账本持有者列表
				modalName: '',
				summaryData: {},
				funItemList: [ // 功能列表
					{
						des: "统计分析",
						path: "/pages-book/bill-record-view/bill-record-view",
					},
					{
						des: "账单日历",
						path: "/pages-book/bill-record-calendar/bill-record-calendar",
					},
					// {
					// 	des: "资产管理",
					// 	path: "",
					// },
					// {
					// 	des: "分类管理",
					// 	path: "",
					// },
					{
						des: "导出账单",
						path: "/pages-book/bill-record-management/bill-record-management",
					},
					// {
					// 	des:"导入账单",
					// 	path:"",
					// },
				],
				customeBarHeight: this.CustomBar,
				avatar: "https://www.sunnygo.chat/images/eat-big-turntable/avatar/NPnWvv8GbnCk1d0ff2e75ed9d02661b182adb7379ea1.jpeg",
				billRecordList: [],
				itemRecordList: [], // 二层消费记录列表
				chooseBook: null,
				chooseSummaryTime: null,
			}
		},
		async onLoad(option) {
			if (!uni.getStorageSync("avatar")) {
				uni.showModal({
					content: "使用记账前需要上传头像信息！！",
					success(option) {
						if (option.confirm) {
							uni.navigateTo({
								url: "/pages/home/user_modify"
							})
						} else {
							uni.navigateTo({
								url: "/pages/index/index"
							})
						}
					}
				})
			} else {
				const shareBookId = option.shareBookId;
				if (shareBookId) {
					// 请求接口创建共享账单
					const res = await joinShareBillBookAPI(shareBookId);
					this.chooseSummaryTime = new Date().toISOString().slice(0, 19);
					this.init();
				}
			}
		},
		onShow() {
			this.chooseSummaryTime = new Date().toISOString().slice(0, 19);
			this.init();
		},
		// 分享邀请好友加入账单
		async onShareAppMessage() {
			if (!this.chooseBook.isShare) {
				uni.showModal({
					content: "默认账单无法邀请好友！",
					showCancel: false
				})
			} else {
				const shareBillBookId = this.chooseBook.parentId ? this.chooseBook.parentId : this.chooseBook.id;
				return {
					title: this.chooseBook.title,
					path: `pages/bill-book/bill-book?shareBookId=${shareBillBookId}`,
					withShareTicket: true
				}
			}
		},
		methods: {
			handleDesFinsh(record){
				// 处理语音上传完成返回record记录
				const billRecord = record;
				this.$refs.desMakeRecordRef.close();
				this.$refs.makeBillPop.open(null,billRecord);
			},
			handleVoiceFinsh(record){
				// 处理语音上传完成返回record记录
				const billRecord = record.data;
				this.$refs.voiceMakeRecordRef.close();
				this.$refs.makeBillPop.open(null,billRecord);
			},
			trigger(e) {
				this.content[e.index].active = !e.item.active
				if(e.index === 0){
					// 记一笔
					this.makeBill();
				}else if(e.index === 1){
					// 打开语音输入组件
					this.$refs.voiceMakeRecordRef.open();
				}else if(e.index === 2){
					this.$refs.desMakeRecordRef.open();
				}
			},
			fabClick() {
				
			},
			toFunPage(item) {
				uni.navigateTo({
					url: item.path
				})
			},
			userChangeClllBack(chooseUser) {
				this.showUserInfo = chooseUser;
				this.init();
			},
			changeShowUser() {
				// 展示弹框，更换展示用户
				this.$refs.changeShowUserDialog.open();
			},
			showDetaile(id) {
				uni.navigateTo({
					url: `/pages-book/bill-record-detail/bill-record-detail?id=${id}&bookName=${this.chooseBook.title}`
				})
			},
			handleChooseMonthChange(e) {
				// 选择月份改变
				if (e.startDate) {
					const [year, month] = e.startDate.split("-");
					const now = new Date();
					const hour = now.getHours().toString().padStart(2, "0");
					const minute = now.getMinutes().toString().padStart(2, "0");
					const second = now.getSeconds().toString().padStart(2, "0");
					this.chooseSummaryTime = `${year}-${month}-01T${hour}:${minute}:${second}`;
					this.init();
					this.modalName = '';
				}


			},
			handleConfirm() {
				this.init();
			},
			getMonthOfChooseTime() {
				const date = new Date(this.chooseSummaryTime);
				// 获取年份
				const year = date.getFullYear();
				// 获取月份（注意 JS 中 getMonth() 返回 0~11，所以要 +1）
				const month = date.getMonth() + 1;
				// 格式化成 "YYYY-MM" 字符串
				const monthStr = `${year}-${month.toString().padStart(2, '0')}`;
				return month;
			},
			getIntegerOfAmount(amount) {
				if (amount) {
					return ("" + amount).split(".")[0] || "0";
				} else {
					return "0";
				}

			},
			getDecimalOfAmount(amount) {
				if (amount) {
					return ("" + amount).split(".")[1] || "00";
				} else {
					return "00";
				}
			},
			async initSummary() {
				const res = await getMonthSummaryAPI(this.chooseSummaryTime, this.showUserInfo.id);
				if (!res) return;
				this.summaryData = res.data;
			},
			async init() {
				const res = await getChoosedBillBookAPI();
				if (!res) return;
				this.chooseBook = res.data;
				this.initSummary();
				const recordRes = await getMonthBillRecordListAPI(this.chooseSummaryTime, this.showUserInfo.id);
				if (!recordRes) return;
				this.billRecordList = [];
				this.itemRecordList = [];
				for (let key in recordRes.data) {
					this.billRecordList.push(recordRes.data[key]);
					this.itemRecordList.push(recordRes.data[key].recordList);
				}
				const resAllUser = await getCurrentBookAllUserAPI();
				if (!resAllUser) return;
				this.allUserList = resAllUser.data;
			},
			toBillBookPage() {
				uni.navigateTo({
					url: "/pages-book/book-management/book-management"
				})
			},
			makeBill() {
				this.$refs.makeBillPop.open();
			},
			handleExtra(dateRecord) {
				let res = "";
				if (dateRecord.inAmount) {
					res += `收入:` + dateRecord.inAmount + " "
				}
				if (dateRecord.outAmount) {
					res += `支出:` + dateRecord.outAmount + " "
				}
				if (dateRecord.inAmount && dateRecord.outAmount) {
					res += `结余:` + (dateRecord.inAmount - dateRecord.outAmount).toFixed(2)
				}
				return res;
			},
			toSearchPage() {
				uni.navigateTo({
					url: `/pages-book/bill-search/bill-search?bookName=${this.chooseBook.title}`
				})
			},
		}
	}
</script>

<style scoped lang="scss">
	.bar {
		background-color: #ffa500;
		display: flex;
		align-items: center;
		color: #fff;
	}

	.head-box {
		display: flex;
		background-color: #ffa500;
		color: #ffffff;
		flex-direction: column;
		align-items: center;
	}

	.stastic-part {
		width: 90vw;
		display: flex;
		justify-content: space-around;
		border: 3rpx solid white;
		border-radius: 15rpx;
		padding: 30rpx 15rpx;
	}

	.stastic-item {
		display: flex;
		flex-direction: column;
		gap: 18rpx;
	}

	.money-integer {
		font-size: large;
		font-weight: bold;
	}

	.month-des {
		font-weight: bold;
		// font-size: large;
	}

	.money-des {
		width: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
		font-weight: bold;
	}

	.share-part {
		width: 95vw;
		display: flex;
		justify-content: space-between;
		padding: 15rpx 5rpx;
		border-bottom: 2rpx solid white;
	}

	.friend-list {
		display: flex;
		align-items: center;
		gap: 5rpx;
	}

	.show-info {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.fun-part {
		width: 95vw;
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin: 30rpx 0rpx;
		flex-wrap: wrap;
	}

	.fun-item {
		padding: 5rpx 8rpx;
		border-radius: 10rpx;
		color: #ffa500;
		background-color: white;
		border-radius: 10rpx;
		padding: 10rpx;
		gap: 30rpx;
	}

	.search-box {
		display: flex;
		width: 100vw;
		margin-top: -20rpx;
	}

	.day-swiper-box {
		display: flex;
		width: 100vw;
		flex-direction: column;
		gap: 30rpx;
		align-items: center;
	}

	.record-card {
		display: flex;
		flex-direction: column;
		width: 95vw;
		background-color: white;
		border-radius: 15rpx;
		margin-top: 15rpx;
		box-shadow: rgba(0, 0, 0, 0.12) 0px 1px 3px, rgba(0, 0, 0, 0.24) 0px 1px 2px;
	}

	.card-head {
		display: flex;
		padding: 30rpx 15rpx;
		justify-content: space-between;
		align-items: center;
		border-bottom: 2rpx solid lightgray;
	}

	.card-body {
		width: 95vw;
	}

	.record-item {
		display: flex;
		flex-direction: column;
		line-gap-override: 5rpx;
		padding: 10rpx;
		gap: 10rpx;
		// border-bottom: 2rpx solid lightgray;
	}

	.first-line {
		display: flex;
		justify-content: space-between;
		padding: 0 5rpx;
		align-items: center;
	}

	.item-left {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.item-right {
		display: flex;
		align-items: center;
		gap: 15rpx;
		padding: 0 10rpx;
	}

	.make-button-none {
		display: flex;
		align-items: center;
		margin-top: 300rpx;
		color: #ffa500;
		border-radius: 30rpx;
		padding: 30rpx;
		background-color: white;
		width: 300rpx;
		box-shadow: rgba(0, 0, 0, 0.12) 0px 1px 3px, rgba(0, 0, 0, 0.24) 0px 1px 2px;
		font-weight: bolder;
		gap: 20rpx;
		justify-content: center;
		font-size: large;
	}

	.make-button {
		color: #ffa500;
		border-radius: 30rpx;
		padding: 20rpx;
		background-color: white;
		width: 200rpx;
		box-shadow: rgba(0, 0, 0, 0.12) 0px 1px 3px, rgba(0, 0, 0, 0.24) 0px 1px 2px;
		display: flex;
		justify-content: center;
		align-items: center;
		font-weight: bolder;
		position: fixed;
		bottom: 120rpx;
		right: 35rpx;
		z-index: 999;
		gap: 10rpx;
	}
</style>