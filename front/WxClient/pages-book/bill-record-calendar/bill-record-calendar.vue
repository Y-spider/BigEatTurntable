<template>
	<view class="page-container">
		<!-- 顶部导航栏 (自定义导航) -->
		<cu-custom :isBack="true">
			<block slot="content">{{chooseBookInfo.title}}</block>
		</cu-custom> <!-- 内容卡片 -->

		<!-- 操作区域：日期选择 & 类型切换 -->
		<view class="control-bar">
			<!-- 日期选择 -->
			<picker mode="date" fields="month" :value="currentDate" @change="onDateChange">
				<view class="date-picker-btn">
					<text class="cuIcon-calendar margin-right-xs"></text>
					{{ currentYear }}年{{ currentMonth }}月
					<text class="cuIcon-right margin-left-xs"></text>
				</view>
			</picker>

			<!-- 切换 Tabs -->
			<view class="tab-control">
				<view class="tab-item" :class="{ active: tabIndex === 0 }" @tap="switchTab(0)">结余</view>
				<view class="tab-item" :class="{ active: tabIndex === 1 }" @tap="switchTab(1)">收入</view>
				<view class="tab-item" :class="{ active: tabIndex === 2 }" @tap="switchTab(2)">支出</view>
			</view>

			<!-- 全部成员按钮 -->
			<view class="member-filter">
				<view class="cu-tag round bg-orange-light sm" @click="openChooseUserDialog">{{showUserInfo.name}}</view>
			</view>
		</view>

		<!-- 月度概览卡片 (颜色适配橙色主题) -->
		<view class="summary-card">
			<view class="summary-item">
				<view class="label">{{ currentMonth }}月收入</view>
				<view class="value text-orange">{{ monthSummary.totalIncome || 0}}</view>
			</view>
			<view class="summary-item border-left border-right">
				<view class="label">{{ currentMonth }}月支出</view>
				<view class="value text-orange">{{ monthSummary.totalExpense || 0 }}</view>
			</view>
			<view class="summary-item">
				<view class="label">{{ currentMonth }}月结余</view>
				<view class="value text-orange">{{ monthSummary.balance || 0}}</view>
			</view>
		</view>

		<!-- 日历主体区域 -->
		<view class="calendar-wrapper bg-theme">
			<!-- 星期表头 -->
			<view class="week-header">
				<view class="week-day" v-for="(item, index) in weeks" :key="index">{{ item }}</view>
			</view>

			<!-- 背景大水印 -->
			<view class="watermark">{{ currentMonth }}</view>

			<!-- 日期网格 -->
			<view class="days-grid">
				<!-- 空白占位 -->
				<view class="day-cell empty" v-for="n,index in emptyDays" :key="index"></view>

				<!-- 真实日期 -->
				<view class="day-cell" v-for="(day, index) in days" :key="index"
					:class="{ 'selected': day.fullDate === selectedDate }" @tap="selectDay(day)">
					<view class="day-num">{{ day.dayNum }}</view>

					<!-- 金额显示逻辑 (根据Tab切换显示不同字段) -->
					<view class="day-money" v-if="day.showAmount !== 0">
						{{ day.showAmount }}
					</view>
					<!-- 没钱的时候占位，防止高度塌陷 -->
					<view class="day-money" v-else>
						<text v-if="tabIndex===0 && day.hasRecord">0</text>
					</view>
				</view>
			</view>
		</view>
		<!-- 选择用户 -->
		<change-show-user-dialog ref="changeShowUserDialog" @onChange="userChangeClllBack"></change-show-user-dialog>
		<!-- 底部选中日期详情 -->
		<view class="daily-detail">
			<view class="date-label">{{ selectedDate }}</view>
			<view class="money-label">
				<text class="margin-right-sm">收: {{ selectedDayDetail.income }}</text>
				<text class="margin-right-sm">支: {{ selectedDayDetail.expense }}</text>
				<text>余: {{ selectedDayDetail.balance }}</text>
			</view>
		</view>
		<view class="card-body">
			<view style="display: flex; flex-direction: column;">
				<view @click="showDetaile(record.id)" class="record-item"
					v-for="(record,index) in itemRecordList[itemRecordListIndex]" :key="record.id">
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
				<!-- 底部提示 -->
				<view v-if="itemRecordList[itemRecordListIndex]" class="foot-box"
					style="display: flex; padding: 30rpx; justify-content: center; align-items: center;">
					已经到底部了~~~ 
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	import changeShowUserDialog from '../../components/change_show_user_dialog.vue';
	import {
		getChoosedBillBookAPI,
		getMonthBillRecordListAPI,
		getMonthSummaryAPI
	} from "@/apis/billApi.js";
	export default {
		components:{
			changeShowUserDialog
		},
		data() {
			return {
				weeks: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
				currentYear: 2025,
				currentMonth: 12,
				currentDay:1,
				currentDate: '2025-12',

				tabIndex: 0, // 0:结余, 1:收入, 2:支出

				selectedDate: '',

				monthSummary: {
					totalIncome: 0,
					totalExpense: 0,
					balance: 0
				},

				selectedDayDetail: {
					income: 0,
					expense: 0,
					balance: 0
				},
				showUserInfo: {
					id: -1,
					name: "全部用户"
				}, // -1表示获取全部用户
				days: [],
				emptyDays: 0,
				chooseBookInfo:{},
				chooseSummaryTime:null,
				billRecordList:[],
				itemRecordList:[],
				itemRecordListIndex:0
			}
		},

		onShow() {
		    const now = new Date();
		    this.currentYear = now.getFullYear();
		    this.currentMonth = now.getMonth() + 1;
			
		    // 初始化日历网格
		    this.initCalendar(this.currentYear, this.currentMonth);
		
		    // 补零函数
		    const padZero = (n) => (n < 10 ? '0' + n : n);
			
		    // 默认选中日期
		    this.selectedDate = `${this.currentYear}-${padZero(this.currentMonth)}-${padZero(now.getDate())}`;
			this.chooseSummaryTime = new Date().toISOString().slice(0, 19);
			this.currentDay = padZero(now.getDate());
		    // 模拟获取数据
		    this.fetchData();
		
		},

		methods: {
			showDetaile(id) {
				uni.navigateTo({
					url: `/pages-book/bill-record-detail/bill-record-detail?id=${id}&bookName=${this.chooseBookInfo.title}`
				})
			},
			userChangeClllBack(chooseUser) {
				this.showUserInfo = chooseUser;
				this.fetchData();
			},
			openChooseUserDialog(){
				this.$refs.changeShowUserDialog.open();
			},
			initCalendar(year, month) {
				this.days = [];
				// 计算当月1号是周几 (JS中0是周日)
				let firstDayOfWeek = new Date(year, month - 1, 1).getDay();

				// 调整为周一开头
				if (firstDayOfWeek === 0) {
					this.emptyDays = 6;
				} else {
					this.emptyDays = firstDayOfWeek - 1;
				}

				let daysInMonth = new Date(year, month, 0).getDate();

				for (let i = 1; i <= daysInMonth; i++) {
					let fullDate = `${year}-${month}-${i < 10 ? '0' + i : i}`;
					this.days.push({
						dayNum: i,
						fullDate: fullDate,
						hasRecord: false, // 标记这一天是否有记录
						showAmount: 0, // 当前日历格子上显示的数字
						income: 0,
						expense: 0,
						balance: 0
					});
				}
			},

			// 模拟API获取数据并随机填充
			async fetchData() {
				const bookRes = await getChoosedBillBookAPI();
				this.chooseBookInfo = bookRes.data;
				const monthSummaryRes = await getMonthSummaryAPI(this.chooseSummaryTime,this.showUserInfo.id);
				this.monthSummary = monthSummaryRes.data;
				const recordListRes = await getMonthBillRecordListAPI(this.chooseSummaryTime,this.showUserInfo.id);
				this.billRecordList = recordListRes.data;
				let i = 0;
				this.itemRecordListIndex = this.currentDay - 1;
				this.itemRecordList = [];
				this.days = this.days.map(day => {
					
					if(this.billRecordList[day.fullDate]){
						const item = this.billRecordList[day.fullDate];
						this.itemRecordList.push(this.billRecordList[day.fullDate].recordList);
						return{
							balance:parseFloat(item.inAmount - item.outAmount).toFixed(2),
							dayNum:day.dayNum,
							expense:item.outAmount,
							fullDate:day.fullDate,
							hasRecord:true,
							income:item.inAmount,
							showAmount:parseFloat(item.inAmount - item.outAmount).toFixed(2),
							showIndex:i++,
						}
					}else{
						this.itemRecordList.push([]);
						return{
							...day,
							showIndex:i++
						}
					}
				});

				// 4. 计算当前要显示的数值 (根据当前Tab)
				this.updateDaysDisplay();

				// 5. 更新选中日期的底部详情
				this.updateSelectedDetail();
			},

			// 切换 Tab
			switchTab(index) {
				this.tabIndex = index;
				this.updateDaysDisplay();
			},

			// 更新日历格子上显示的数字
			updateDaysDisplay() {
				this.days.forEach(day => {
					if (this.tabIndex === 0) {
						// 结余模式：显示 (收入-支出)
						day.showAmount = day.balance;
					} else if (this.tabIndex === 1) {
						// 收入模式
						day.showAmount = day.income;
					} else if (this.tabIndex === 2) {
						// 支出模式：通常显示为负数或者正数，这里模拟显示负数逻辑
						// 如果为了日历美观，也可以显示正数，看产品需求。这里设为支出原本的数值
						day.showAmount = day.expense === 0 ? 0 : -day.expense;
					}
				});
			},

			selectDay(day) {
				this.itemRecordListIndex = day.showIndex;
				this.selectedDate = day.fullDate;
				this.updateSelectedDetail(day);
			},

			updateSelectedDetail(day) {
				let currentDay = day;
				if (!currentDay) {
					currentDay = this.days.find(d => d.fullDate === this.selectedDate);
				}

				if (currentDay) {
					this.selectedDayDetail = {
						income: currentDay.income,
						expense: currentDay.expense,
						balance: currentDay.balance
					};
				} else {
					this.selectedDayDetail = {
						income: 0,
						expense: 0,
						balance: 0
					};
				}
			},

			onDateChange(e) {
				const val = e.detail.value;
				const parts = val.split('-');
				this.currentYear = parseInt(parts[0]);
				this.currentMonth = parseInt(parts[1]);
				this.currentDate = val;
				this.chooseSummaryTime = new Date(val).toISOString().slice(0,19);
				this.selectedDate = `${this.currentYear}-${this.currentMonth}-01`;
				this.fetchData();
				this.initCalendar(this.currentYear, this.currentMonth);
			}
		}
	}
</script>

<style scoped>
	/* 定义 CSS 变量方便维护 */
	:root {
		--primary-color: #ffa500;
		/* 核心橙色 */
		--summary-bg: #fff8e1;
		/* 浅橙色背景 */
		--summary-border: #ffe0b2;
		/* 浅橙色边框 */
	}

	/* 通用类 */
	.bg-theme {
		background-color: #ffa500;
		color: #fff;
	}

	.text-orange {
		color: #ff8c00;
		/* 深一点的橙色用于文字 */
	}

	.bg-orange-light {
		background-color: #ffe0b2;
		color: #e65100;
	}

	.page-container {
		min-height: 100vh;
		background-color: #f8f8f8;
		font-family: Helvetica Neue, Helvetica, sans-serif;
	}

	/* 顶部导航 */
	.custom-nav {
		padding-top: var(--status-bar-height);
		height: calc(44px + var(--status-bar-height));
		display: flex;
		align-items: center;
		padding-left: 20rpx;
		padding-right: 20rpx;
	}

	.nav-content {
		width: 100%;
		display: flex;
		justify-content: space-between;
		align-items: center;
		height: 44px;
	}

	.title {
		font-size: 34rpx;
		font-weight: 500;
	}

	.action-icons text {
		margin-left: 20rpx;
		font-size: 32rpx;
		border: 1px solid rgba(255, 255, 255, 0.4);
		border-radius: 50%;
		padding: 4rpx;
	}

	/* 操作栏 */
	.control-bar {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 30rpx;
		background-color: #f8f8f8;
	}

	.date-picker-btn {
		background: #fff;
		border: 1px solid #eee;
		padding: 10rpx 20rpx;
		border-radius: 30rpx;
		font-size: 28rpx;
		color: #333;
		display: flex;
		align-items: center;
	}

	.tab-control {
		display: flex;
		background: #eee;
		border-radius: 30rpx;
		padding: 4rpx;
	}

	.tab-item {
		padding: 8rpx 30rpx;
		font-size: 26rpx;
		color: #666;
		border-radius: 26rpx;
		transition: all 0.3s;
	}

	.tab-item.active {
		background-color: #ffa500;
		color: #fff;
		box-shadow: 0 2rpx 6rpx rgba(255, 165, 0, 0.3);
	}

	.member-filter .cu-tag {
		height: 70rpx;
		width: 70rpx;
		text-align: center;
		line-height: 1.1;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 20rpx;
	}

	/* 月度汇总卡片 - 橙色主题 */
	.summary-card {
		margin: 0 30rpx 20rpx;
		background-color: #fff8e1;
		/* 浅橙色 */
		border: 1px solid #ffe0b2;
		border-radius: 12rpx 12rpx 0 0;
		display: flex;
		padding: 30rpx 0;
	}

	.summary-item {
		flex: 1;
		text-align: center;
	}

	.summary-item .label {
		font-size: 24rpx;
		color: #9e9e9e;
		margin-bottom: 10rpx;
	}

	.summary-item .value {
		font-size: 32rpx;
		font-weight: bold;
	}

	.border-left {
		border-left: 1px solid #ffe0b2;
	}

	.border-right {
		border-right: 1px solid #ffe0b2;
	}

	/* 日历样式 */
	.calendar-wrapper {
		margin: 0 30rpx;
		border-radius: 16rpx;
		padding-bottom: 20rpx;
		position: relative;
		overflow: hidden;
		box-shadow: 0 4rpx 16rpx rgba(255, 165, 0, 0.2);
	}

	.watermark {
		position: absolute;
		bottom: -20rpx;
		right: 10rpx;
		font-size: 300rpx;
		font-weight: bold;
		color: rgba(255, 255, 255, 0.2);
		/* 半透明白色 */
		pointer-events: none;
		z-index: 1;
	}

	.week-header {
		display: flex;
		padding: 20rpx 0;
		border-bottom: 1px solid rgba(255, 255, 255, 0.2);
	}

	.week-day {
		flex: 1;
		text-align: center;
		font-size: 28rpx;
		color: #fff;
		opacity: 0.9;
	}

	.days-grid {
		display: flex;
		flex-wrap: wrap;
		padding: 10rpx;
		position: relative;
		z-index: 2;
	}

	.day-cell {
		width: 14.28%;
		height: 100rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		margin-bottom: 10rpx;
		border-radius: 12rpx;
		transition: background 0.2s;
	}

	.day-num {
		font-size: 30rpx;
		margin-bottom: 4rpx;
		font-weight: 500;
	}

	.day-money {
		font-size: 20rpx;
		height: 28rpx;
		opacity: 0.9;
	}

	/* 选中状态 */
	.day-cell.selected {
		background-color: #fff;
		color: #ffa500;
		font-weight: bold;
		transform: scale(1.05);
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
	}

	/* 选中后金额的颜色也变深 */
	.day-cell.selected .day-money {
		color: #ffa500;
		opacity: 1;
	}

	/* 底部详情 */
	.daily-detail {
		margin-top: 20rpx;
		padding: 30rpx;
		background-color: #fff;
		display: flex;
		justify-content: space-between;
		align-items: center;
		font-size: 28rpx;
		color: #333;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.02);
	}

	.date-label {
		font-weight: bold;
		color: #333;
	}

	.money-label {
		color: #666;
		font-size: 26rpx;
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
	
</style>