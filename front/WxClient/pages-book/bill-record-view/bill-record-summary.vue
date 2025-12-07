<template>
	<view class="container">
		<!-- 导航栏 -->
		<cu-custom bgColor="bg-theme" :isBack="true">
			<block slot="content">{{bookName}}汇总</block>
		</cu-custom>

		<!-- 顶部统计区域 (橙色背景) -->
		<view class="header-summary bg-theme padding-bottom">
			
			<!-- 日期选择行 -->
			<view class="flex align-center padding-lr padding-top-sm text-white text-lg">
				<!-- 开始时间 -->
				<picker mode="date" fields="month" :value="startDate" @change="bindStartDateChange">
					<view class="picker-item">
						{{formatDateCN(startDate)}} <text class="cuIcon-triangledownfill text-sm margin-left-xs"></text>
					</view>
				</picker>
				
				<view class="margin-lr">至</view>
				
				<!-- 结束时间 -->
				<picker mode="date" fields="month" :value="endDate" @change="bindEndDateChange">
					<view class="picker-item">
						{{formatDateCN(endDate)}} <text class="cuIcon-triangledownfill text-sm margin-left-xs"></text>
					</view>
				</picker>
			</view>

			<!-- 总计数值行 -->
			<view class="flex text-center margin-top text-white">
				<!-- 支出 -->
				<view class="flex-sub">
					<view class="text-sm opacity-8 margin-bottom-xs">支出</view>
					<view class="text-xl text-bold">{{totalStats.totalExpense.toFixed(2)}}</view>
				</view>
				
				<!-- 分割线 -->
				<view class="line-vertical"></view>
				
				<!-- 收入 -->
				<view class="flex-sub">
					<view class="text-sm opacity-8 margin-bottom-xs">收入</view>
					<view class="text-xl text-bold">{{totalStats.totalIncome.toFixed(2)}}</view>
				</view>
				
				<!-- 分割线 -->
				<view class="line-vertical"></view>
				
				<!-- 结余 -->
				<view class="flex-sub">
					<view class="text-sm opacity-8 margin-bottom-xs">结余</view>
					<view class="text-xl text-bold">{{totalStats.totalSurplus.toFixed(2)}}</view>
				</view>
			</view>
		</view>

		<!-- 列表表头 -->
		<view class="grid col-4 bg-gray padding-sm text-center text-grey text-sm">
			<view>月份</view>
			<view>支出</view>
			<view>收入</view>
			<view>结余</view>
		</view>

		<!-- 列表内容 -->
		<view class="bg-white">
			<view class="grid col-4 padding-sm text-center border-bottom" v-for="(item, index) in monthList" :key="index">
				<view class="text-gray">{{formatDateCN(item.month)}}</view>
				<view class="text-black">{{Number(item.expense).toFixed(2)}}</view>
				<view class="text-black">{{Number(item.income).toFixed(2)}}</view>
				<!-- 结余：小于0显示红色，否则显示黑色或绿色 -->
				<view :class="Number(item.surplus) < 0 ? 'text-red' : 'text-black'">
					{{Number(item.surplus).toFixed(2)}}
				</view>
			</view>
			
			<!-- 空状态 -->
			<view v-if="monthList.length === 0" class="padding text-center text-gray margin-top">
				暂无数据
			</view>
		</view>
	</view>
</template>

<script>
	// 假设你有一个 API 文件，如果还没有，可以先注释掉 import
	// import { getBillSummaryAPI } from "@/apis/billApi.js";
	import {getSummaryMonthAPI} from "@/apis/billApi.js";
	export default {
		data() {
			return {
				bookName: "默认账单",
				startDate: '', // 格式: YYYY-MM
				endDate: '',   // 格式: YYYY-MM
				
				// 统计总数据
				totalStats: {
					totalExpense: 0,
					totalIncome: 0,
					totalSurplus: 0
				},
				
				// 月份列表数据
				monthList: []
			}
		},
		onLoad(option) {
			if (option.bookName) {
				this.bookName = option.bookName;
			}
			// 1. 初始化日期 (上个月 ~ 本月)
			this.initDates();
			// 2. 加载数据
			this.getSummaryData();
		},
		methods: {
			// 初始化日期：前一个月 到 当前月
			initDates() {
				const now = new Date();
				// 当前月
				const currentYear = now.getFullYear();
				const currentMonth = now.getMonth() + 1;
				this.endDate = `${currentYear}-${String(currentMonth).padStart(2, '0')}`;

				// 上个月 (处理跨年情况)
				let prevYear = currentYear;
				let prevMonth = currentMonth - 1;
				if (prevMonth === 0) {
					prevMonth = 12;
					prevYear = currentYear - 1;
				}
				this.startDate = `${prevYear}-${String(prevMonth).padStart(2, '0')}`;
			},

			// 格式化显示日期：2025-12 -> 2025年12月
			formatDateCN(dateStr) {
				if(!dateStr) return '';
				const parts = dateStr.split('-');
				return `${parts[0]}年${parts[1]}月`;
			},

			bindStartDateChange(e) {
				this.startDate = e.detail.value;
				this.getSummaryData();
			},

			bindEndDateChange(e) {
				this.endDate = e.detail.value;
				this.getSummaryData();
			},

			// 获取汇总数据
			async getSummaryData() {
				// 模拟加载
				uni.showLoading({ title: '计算中...' });
				try {
					
					const res = await getSummaryMonthAPI({
						startTime: this.startDate,
						endTime: this.endDate})
					if(res.code === 200) {
						this.totalStats = res.data.totalStats;
						this.monthList = res.data.monthList;
					}
					uni.hideLoading();
				} catch (e) {
					uni.hideLoading();
					uni.showToast({ title: '加载失败', icon: 'none' });
				}
			},

			// 模拟数据生成
			mockApiResponse() {
				// 模拟总计
				this.totalStats = {
					totalExpense: 13019.56,
					totalIncome: 5500.00,
					totalSurplus: -7519.56
				};

				// 模拟列表 (根据选择的日期范围生成2条数据模拟截图)
				this.monthList = [
					{
						month: '2025-12',
						expense: 1278.00,
						income: 5000.00,
						surplus: 3722.00
					},
					{
						month: '2025-11',
						expense: 11741.56,
						income: 500.00,
						surplus: -11241.56
					}
				];
			}
		}
	}
</script>

<style scoped>
	/* 主题色背景 */
	.bg-theme {
		background-color: #ffa500 !important;
		color: #ffffff;
	}
	
	/* 半透明文字 */
	.opacity-8 {
		opacity: 0.8;
	}

	/* 竖线分割符 */
	.line-vertical {
		width: 1px;
		height: 80rpx;
		background-color: rgba(255, 255, 255, 0.3);
		margin-top: 10rpx;
	}

	/* 列表下边框 */
	.border-bottom {
		border-bottom: 1rpx solid #f1f1f1;
	}
	
	/* 调整ColorUI Grid的样式适配 */
	.grid.col-4 > view {
		padding: 10rpx 0;
	}
</style>