<template>
	<view class="container">
		<!-- 导航栏：背景改为自定义主题色 bg-theme -->
		<cu-custom bgColor="bg-theme" :isBack="true">
			<block slot="content">{{chooseBookInfo.title || '记账本'}}</block>
		</cu-custom>

		<!-- 顶部统计概览区域：背景改为自定义主题色 bg-theme -->
		<view class="header-summary bg-theme">
			<view class="filter-row flex justify-between align-center padding-lr">
				<view class="date-filter">
					<picker mode="date" fields="month" :value="currentDate" @change="bindDateChange">
						<view class="text-lg" style="font-size: large; font-weight: bold;">
							{{currentDate}} <text class="cuIcon-triangledownfill"></text>
						</view>
					</picker>
				</view>
				<view style="font-size: large;" class="type-switch text-df">
					<!-- 文字颜色逻辑调整：未选中时使用半透明白 -->
					<text :class="currentType==='expense'?'text-white text-bold':'text-white-opacity'"
						@click="switchType('expense')" class="margin-right">支出</text>
					<text :class="currentType==='income'?'text-white text-bold':'text-white-opacity'"
						@click="switchType('income')">收入</text>
				</view>
			</view>

			<view style="font-size: large;" class="padding-lr margin-top-sm">
				<view @click="openChangeUserDialog" class="text-xl margin-bottom-xs">{{showUserInfo.name}} <text
						class="cuIcon-triangledownfill"></text></view>
				<view class="text-xl opacity-8">共{{currentType==='expense'?'支出':'收入'}}</view>
				<view class="flex justify-between align-end">
					<view class="text-xxl text-bold">
						<text class="text-lg">¥</text> {{totalAmount.toFixed(2)}}
					</view>
					<view class="cu-tag line-white round" @click="viewSummary">查看汇总</view>
				</view>
			</view>
		</view>

		<!-- 1. 支出构成 (饼图) -->
		<view class="bg-white margin-top-sm padding-top">
			<view v-if="currentType=='expense'" class="padding-lr text-lg text-black text-bold">支出构成</view>
			<view v-else class="padding-lr text-lg text-black text-bold">收入构成</view>
			<!-- 图表容器 -->
			<view class="charts-box">
				<qiun-data-charts type="pie" :opts="pieOpts" :chartData="pieChartData" canvasId="pieChart"
					:canvas2d="true" />
			</view>

			<!-- 支出构成列表 -->
			<view class="cu-list menu-avatar no-padding">
				<view class="cu-item" v-for="(item, index) in categoryList" :key="index">
					<!-- 图标背景色逻辑：使用主题色或ColorUI的橙色系 -->
					<view class="cu-avatar round sm bg-theme">
						<text :class="'cuIcon-' + item.icon"></text>
					</view>
					<view class="content">
						<view class="text-grey">{{item.name}} <text
								class="text-gray text-sm margin-left-sm">{{item.percent}}%</text></view>
						<view class="cu-progress round sm striped active">
							<!-- 进度条颜色改为 bg-theme -->
							<view class="bg-theme" :style="[{ width:item.percent + '%'}]"></view>
						</view>
					</view>
					<view class="action text-right">
						<view class="text-black">¥ {{item.amount.toFixed(2)}}</view>
						<view class="text-gray text-xs">{{item.count}}笔</view>
					</view>
					<view class="cuIcon-right text-gray margin-left-sm"></view>
				</view>
			</view>
		</view>

		<!-- 2. 资产账户构成 (动态渲染) -->
		<view class="bg-white margin-top-sm padding">
			<view v-if="currentType=='expense'" class="text-lg text-black text-bold margin-bottom">资产账户支出构成</view>
			<view v-else class="text-lg text-black text-bold margin-bottom">资产账户收入构成</view>

			<!-- 使用 v-for 循环真实数据 -->
			<view class="flex align-center margin-bottom-sm" v-for="(item, index) in assetList" :key="index">
				<!-- 图标背景 -->
				<view class="cu-avatar round sm bg-theme">
					<!-- 假设后端返回的 icon 是 'weixin'，对应 class 'cuIcon-weixin' -->
					<text :class="'cuIcon-' + (item.icon || 'moneybag')"></text>
				</view>
				<view class="margin-left-sm flex-sub">
					<view>{{item.name}}</view>
					<view class="text-gray text-sm">{{item.percent}}%</view>
				</view>
				<view class="text-right">¥ {{Number(item.amount).toFixed(2)}}</view>
			</view>

			<!-- 如果没有数据 -->
			<view v-if="assetList.length === 0" class="text-center text-gray padding">
				暂无账户数据
			</view>
		</view>

		<!-- 3. 每日支出对比 (柱状图) -->
		<!-- 3. 每日支出对比 (柱状图) -->
		<view class="bg-white margin-top-sm padding-top">
			<view class="padding-lr flex justify-between align-center">
				<view v-if="currentType=='expense'" class="text-lg text-black text-bold">每日支出对比</view>
				<view v-else class="text-lg text-black text-bold">每日收入比</view>
				<!-- 新增：左右切换控制器 -->
				<view class="flex align-center">
					<button class="cu-btn icon sm round shadow" :disabled="chartPageIndex === 0"
						@click="changeChartPage(-1)">
						<text class="cuIcon-back"></text>
					</button>
					<view class="text-sm margin-lr text-gray" style="width: 160rpx; text-align: center;">
						{{currentChartRangeStr}}
					</view>
					<button class="cu-btn icon sm round shadow" :disabled="isChartEnd" @click="changeChartPage(1)">
						<text class="cuIcon-right"></text>
					</button>
				</view>
			</view>

			<view class="charts-box">
				<qiun-data-charts type="column" :opts="columnOpts" :chartData="columnChartData" canvasId="columnChart"
					:canvas2d="true" />
			</view>
		</view>

		<!-- 4. 支出排行榜 (列表) -->
		<view class="bg-white margin-top-sm padding-bottom">
			<view v-if="currentType=='expense'" class="padding text-lg text-black text-bold">支出排行榜</view>
			<view v-else class="padding text-lg text-black text-bold">收入排行榜</view>
			<view class="record-box">
				<view class="day-swiper-box" style="width: 100vw; flex-direction: column;display: flex;justify-content: center;padding: 0 10rpx;">
					<view class="record-card">
						<view class="card-head">
							<view> </view>
							<view class="extra" style="color: #b9b9b9; font-size: small; font-weight: bold;">
								
							</view>
						</view>
						<view class="card-body">
							<view style="display: flex; flex-direction: column;">
								<view @click="showDetaile(record.id)" class="record-item"
									v-for="(record,index) in rankList" :key="record.id">
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
					<view  class="foot-box"
						style="display: flex; padding: 30rpx; justify-content: center; align-items: center;">
						已经到底部了~~~
					</view>
				</view>
			</view>
		</view>

		<view class="padding-bottom-xl"></view>
		<change-show-user-dialog ref="changeShowUserDialog" @onChange="userChangeClllBack"></change-show-user-dialog>
	</view>
</template>

<script>
	import changeShowUserDialog from '../../components/change_show_user_dialog.vue';
	// 引入API
	import {
		// 假设你把聚合接口命名为 getMonthSummaryAPI
		getChoosedBillBookAPI,
		getStasticDataAPI
	} from "@/apis/billApi.js";

	export default {
		components:{
			changeShowUserDialog
		},
		data() {
			return {
				showUserInfo: {
					id: -1,
					name: "全部用户"
				},
				chooseBookInfo: {
					title: '我的账本'
				}, // 可以通过上个页面传参获取，这里保留默认

				// --- 核心状态 ---
				currentDate: '', // 在 onLoad 中初始化为当前年月
				currentType: 'expense', // expense | income
				totalAmount: 0.00, // 保底数据

				// --- 1. 饼图相关 ---
				pieChartData: {
					series: []
				}, // 保底
				categoryList: [], // 保底
				pieOpts: {
					color: ["#ffa500", "#ffce7b", "#f37b1d", "#8dc63f", "#1cbbb4"],
					padding: [5, 5, 5, 5],
					enableScroll: false,
					extra: {
						pie: {
							activeOpacity: 0.5,
							activeRadius: 10,
							offsetAngle: 0,
							labelWidth: 15,
							border: false,
							borderWidth: 3,
							borderColor: "#FFFFFF"
						}
					},
					legend: {
						show: false
					}
				},

				// --- 2. 柱状图相关 ---
				columnChartData: {
					categories: [],
					series: []
				}, // 保底
				fullMonthDays: [], // 存放完整的X轴日期
				fullMonthAmounts: [], // 存放完整的Y轴金额
				chartPageIndex: 0, // 当前页码
				chartPageSize: 7, // 每页显示条数

				columnOpts: {
					color: ["#ffa500"],
					padding: [15, 15, 0, 5],
					enableScroll: false, // 关闭原生滚动，使用自定义分页
					legend: {
						show: false
					},
					xAxis: {
						disableGrid: true
					},
					yAxis: {
						data: [{
							min: 0
						}],
						gridType: "dash",
						dashLength: 2
					},
					extra: {
						column: {
							type: "group",
							width: 20,
							activeBgColor: "#000000",
							activeBgOpacity: 0.08
						}
					}
				},

				// --- 3. 其他列表 ---
				assetList: [], // 资产账户构成
				rankList: [] // 排行榜
			}
		},
		computed: {
			// 计算柱状图当前显示的日期范围文字
			currentChartRangeStr() {
				if (this.fullMonthDays.length === 0) return '';
				let start = this.chartPageIndex * this.chartPageSize;
				let end = Math.min(start + this.chartPageSize, this.fullMonthDays.length);
				let sStr = this.fullMonthDays[start];
				let eStr = this.fullMonthDays[end - 1] || this.fullMonthDays[start];
				return `${sStr} ~ ${eStr}`;
			},
			// 是否已经是柱状图的最后一页
			isChartEnd() {
				return (this.chartPageIndex + 1) * this.chartPageSize >= this.fullMonthDays.length;
			}
		},
		onShow() {
			// 初始化当前月份
			const now = new Date();
			const year = now.getFullYear();
			const month = (now.getMonth() + 1).toString().padStart(2, '0');
			this.currentDate = `${year}-${month}`;

			this.init();
		},
		methods: {
			showDetaile(id) {
				uni.navigateTo({
					url: `/pages-book/bill-record-detail/bill-record-detail?id=${id}&bookName=${this.chooseBookInfo.title}`
				})
			},
			openChangeUserDialog(){
				this.$refs.changeShowUserDialog.open();
			},
			userChangeClllBack(chooseUser) {
				this.showUserInfo = chooseUser;
				this.init();
			},
			async init() {
				this.getRealData();
			},

			bindDateChange(e) {
				this.currentDate = e.detail.value;
				this.getRealData();
			},

			switchType(type) {
				if (this.currentType === type) return;
				this.currentType = type;
				this.chartPageIndex = 0; // 切换类型重置柱状图页码
				this.getRealData();
			},

			// 分页切换柱状图
			changeChartPage(step) {
				this.chartPageIndex += step;
				this.updateChartDataSlice();
			},

			viewSummary() {
				uni.navigateTo({
					url:"/pages-book/bill-record-view/bill-record-summary?bookName="+this.chooseBookInfo.title
				})
			},

			// 获取某个月有多少天
			getDaysInMonth(year, month) {
				return new Date(year, month, 0).getDate();
			},

			// =========================================
			// 核心方法：请求接口并处理数据
			// =========================================
			async getRealData() {
				uni.showLoading({
					title: '加载中...'
				});

				try {
					// 1. 发起请求
					const chooseBookRes = await getChoosedBillBookAPI();
					this.chooseBookInfo = chooseBookRes.data;
					// 获取统计数据
					const postData = {
						userId:this.showUserInfo.id,
						date:this.currentDate,
						type:this.currentType == 'expense' ? "out" : "in"
					}
					const res = await getStasticDataAPI(postData);
					// 假设后端返回 code: 200 为成功
					if (res.code === 200) {
						const data = res.data || {};

						// --- A. 更新总金额 ---
						this.totalAmount = Number(data.totalAmount) || 0;

						// --- B. 更新分类列表 & 饼图 ---
						this.categoryList = data.categoryList || [];
						// 转换饼图需要的数据格式: { name: 'xx', value: 123 }
						let pieSeriesData = this.categoryList.map(item => {
							return {
								name: item.name,
								value: Number(item.amount)
							};
						});
						// 赋值给 uCharts (如果数据为空，饼图会显示无数据状态)
						this.pieChartData = {
							series: [{
								data: pieSeriesData
							}]
						};

						// --- C. 更新资产列表 ---
						this.assetList = data.assetList || [];

						// --- D. 更新排行榜 ---
						this.rankList = data.rankList || [];

						// --- E. 核心：处理每日数据生成柱状图 ---
						this.processColumnChart(data.dailyData || {});
					}
				} catch (e) {
					console.error("获取统计数据失败", e);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 处理柱状图数据逻辑：补全整月数据
			processColumnChart(dailyDataMap) {
				// 1. 获取当前选中的年月
				let [yearStr, monthStr] = this.currentDate.split('-');
				let year = parseInt(yearStr);
				let month = parseInt(monthStr);

				// 2. 获取当月天数
				let daysCount = this.getDaysInMonth(year, month);

				// 3. 清空并重新生成
				this.fullMonthDays = [];
				this.fullMonthAmounts = [];

				for (let i = 1; i <= daysCount; i++) {
					// 生成日期字符串 (例如: "12-01")
					let dayStr = `${monthStr}-${i.toString().padStart(2, '0')}`;
					this.fullMonthDays.push(dayStr);

					// 从后端 map 中取值，如果该日期没有数据，则补 0
					// 注意：接口返回的 key 可能是 "1" 也可能是 "01"，需要统一转字符串匹配
					let key1 = i.toString();
					let key2 = i.toString().padStart(2, '0');

					let amount = dailyDataMap[key1] !== undefined ? dailyDataMap[key1] :
						(dailyDataMap[key2] !== undefined ? dailyDataMap[key2] : 0);

					this.fullMonthAmounts.push(Number(amount));
				}

				// 4. 切片显示第一页
				this.chartPageIndex = 0;
				this.updateChartDataSlice();
			},

			// 柱状图切片逻辑 (纯前端显示逻辑，无需改动)
			updateChartDataSlice() {
				if (this.fullMonthDays.length === 0) {
					this.columnChartData = {
						categories: [],
						series: []
					};
					return;
				}

				let start = this.chartPageIndex * this.chartPageSize;
				let end = start + this.chartPageSize;

				let slicedDays = this.fullMonthDays.slice(start, end);
				let slicedAmounts = this.fullMonthAmounts.slice(start, end);

				this.columnChartData = {
					categories: slicedDays,
					series: [{
						name: this.currentType === 'expense' ? "支出" : "收入",
						data: slicedAmounts
					}]
				};
			}
		}
	}
</script>

<style scoped>
	page {
		background-color: #F1F1F1;
	}

	/* --- 主题色定义 --- */
	.bg-theme {
		background-color: #ffa500 !important;
		color: #ffffff;
	}

	.text-theme {
		color: #ffa500 !important;
	}

	/* 半透明白，用于未选中的文字 */
	.text-white-opacity {
		color: rgba(255, 255, 255, 0.7);
	}

	.header-summary {
		padding-bottom: 30rpx;
		/* 确保背景色应用 */
		background-color: #ffa500;
		color: #ffffff;
	}

	.charts-box {
		width: 100%;
		height: 400rpx;
	}

	.cu-list.menu-avatar>.cu-item {
		height: 120rpx;
	}

	.cu-list.menu-avatar>.cu-item .content {
		left: 120rpx;
		width: calc(100% - 120rpx - 180rpx);
	}

	.cu-list.menu-avatar>.cu-item .action {
		width: 180rpx;
		text-align: right;
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
</style>