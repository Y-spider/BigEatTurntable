<!-- 番茄计时器统计页面 -->
<template>
	<view class="statistics-container">
		<cu-custom :isBack="true">
			<block slot="content">番茄</block>
		</cu-custom>
		
		<!-- 统计概览 -->
		<view class="overview-section">
			<view class="overview-title">数据概览</view>
			<view class="overview-grid">
				<view class="overview-item">
					<text class="overview-number">{{ totalStats.totalPomodoros }}</text>
					<text class="overview-label">累计番茄</text>
				</view>
				<view class="overview-item">
					<text class="overview-number">{{ totalStats.totalFocusMinutes }}</text>
					<text class="overview-label">专注分钟</text>
				</view>
				<view class="overview-item">
					<text class="overview-number">{{ totalStats.totalFocusMinutes > 0 ? Math.round(totalStats.totalFocusMinutes / 60) : 0 }}</text>
					<text class="overview-label">专注小时</text>
				</view>
				<view class="overview-item">
					<text class="overview-number">{{ streakDays }}</text>
					<text class="overview-label">连续天数</text>
				</view>
			</view>
		</view>
		
		<!-- 时间范围选择 -->
		<view class="time-range-section">
			<view class="time-range-title">时间范围</view>
			<view class="time-range-tabs">
				<view 
					class="time-tab" 
					:class="{ 'active': timeRange === 'today' }"
					@click="switchTimeRange('today')"
				>
					<text>今日</text>
				</view>
				<view 
					class="time-tab" 
					:class="{ 'active': timeRange === 'week' }"
					@click="switchTimeRange('week')"
				>
					<text>本周</text>
				</view>
				<view 
					class="time-tab" 
					:class="{ 'active': timeRange === 'month' }"
					@click="switchTimeRange('month')"
				>
					<text>本月</text>
				</view>
				<view 
					class="time-tab" 
					:class="{ 'active': timeRange === 'all' }"
					@click="switchTimeRange('all')"
				>
					<text>全部</text>
				</view>
			</view>
		</view>
		
		<!-- 详细统计 -->
		<view class="detail-section">
			<view class="detail-title">详细数据</view>
			<view class="detail-grid">
				<view class="detail-item">
					<text class="detail-number">{{ currentStats.pomodoros }}</text>
					<text class="detail-label">完成番茄</text>
				</view>
				<view class="detail-item">
					<text class="detail-number">{{ currentStats.focusMinutes }}</text>
					<text class="detail-label">专注分钟</text>
				</view>
				<view class="detail-item">
					<text class="detail-number">{{ currentStats.avgSessionTime }}</text>
					<text class="detail-label">平均时长</text>
				</view>
				<view class="detail-item">
					<text class="detail-number">{{ currentStats.completionRate }}%</text>
					<text class="detail-label">完成率</text>
				</view>
			</view>
		</view>
		
		<!-- 历史记录 -->
		<view class="history-section">
			<view class="history-title">近期记录</view>
			<view class="history-list" v-if="historyData.length > 0">
				<view 
					class="history-item" 
					v-for="(item, index) in historyData" 
					:key="index"
				>
					<view class="history-date">{{ item.date }}</view>
					<view class="history-stats">
						<text class="history-pomodoros">{{ item.pomodoros }}个番茄</text>
						<text class="history-time">{{ item.focusMinutes }}分钟</text>
					</view>
					<view class="history-progress">
						<view class="progress-bar">
							<view 
								class="progress-fill" 
								:style="{ width: item.completionRate + '%' }"
							></view>
						</view>
						<text class="progress-text">{{ item.completionRate }}%</text>
					</view>
				</view>
			</view>
			<view class="empty-state" v-else>
				<text class="empty-icon">📊</text>
				<text class="empty-text">暂无数据</text>
				<text class="empty-desc">开始使用番茄计时器来记录你的专注时光吧！</text>
			</view>
		</view>
		
		<!-- 成就系统 -->
		<!-- <view class="achievement-section">
			<view class="achievement-title">成就徽章</view>
			<view class="achievement-grid">
				<view 
					class="achievement-item" 
					v-for="achievement in achievements" 
					:key="achievement.id"
					:class="{ 'unlocked': achievement.unlocked }"
				>
					<view class="achievement-icon">{{ achievement.icon }}</view>
					<text class="achievement-name">{{ achievement.name }}</text>
					<text class="achievement-desc">{{ achievement.description }}</text>
				</view>
			</view>
		</view> -->
	</view>
</template>

<script>
	export default {
		data() {
			return {
				timeRange: 'today',
				totalStats: {
					totalPomodoros: 0,
					totalFocusMinutes: 0
				},
				currentStats: {
					pomodoros: 0,
					focusMinutes: 0,
					avgSessionTime: 0,
					completionRate: 0
				},
				historyData: [],
				streakDays: 0,
				// achievements: [
				// 	{
				// 		id: 'first_pomodoro',
				// 		name: '初试牛刀',
				// 		description: '完成第一个番茄',
				// 		icon: '🎯',
				// 		unlocked: false
				// 	},
				// 	{
				// 		id: 'daily_goal',
				// 		name: '日积月累',
				// 		description: '连续7天完成番茄',
				// 		icon: '📅',
				// 		unlocked: false
				// 	},
				// 	{
				// 		id: 'focus_master',
				// 		name: '专注大师',
				// 		description: '累计专注100小时',
				// 		icon: '🧠',
				// 		unlocked: false
				// 	},
				// 	{
				// 		id: 'pomodoro_king',
				// 		name: '番茄之王',
				// 		description: '累计完成500个番茄',
				// 		icon: '👑',
				// 		unlocked: false
				// 	}
				// ]
			}
		},
		onLoad() {
			this.loadStatistics()
			this.loadHistoryData()
			this.checkAchievements()
		},
		methods: {
			// 加载统计数据
			loadStatistics() {
				// 加载累计统计
				const totalStats = uni.getStorageSync('pomodoro_total_stats')
				if (totalStats) {
					this.totalStats = totalStats
				}
				
				// 加载今日统计
				const today = new Date().toDateString()
				const todayStats = uni.getStorageSync(`pomodoro_stats_${today}`)
				if (todayStats) {
					this.todayStats = todayStats
				}
				
				this.updateCurrentStats()
			},
			
			// 更新当前统计数据
			updateCurrentStats() {
				switch (this.timeRange) {
					case 'today':
						this.currentStats = {
							pomodoros: this.todayStats.completedPomodoros || 0,
							focusMinutes: this.todayStats.focusMinutes || 0,
							avgSessionTime: this.calculateAvgSessionTime(),
							completionRate: this.calculateCompletionRate()
						}
						break
					case 'week':
						this.currentStats = this.calculateWeekStats()
						break
					case 'month':
						this.currentStats = this.calculateMonthStats()
						break
					case 'all':
						this.currentStats = {
							pomodoros: this.totalStats.totalPomodoros,
							focusMinutes: this.totalStats.totalFocusMinutes,
							avgSessionTime: this.calculateAvgSessionTime(),
							completionRate: this.calculateCompletionRate()
						}
						break
				}
			},
			
			// 计算平均会话时间
			calculateAvgSessionTime() {
				if (this.currentStats.pomodoros === 0) return 0
				return Math.round(this.currentStats.focusMinutes / this.currentStats.pomodoros)
			},
			
			// 计算完成率
			calculateCompletionRate() {
				// 这里可以根据实际需求计算完成率
				// 暂时返回一个模拟值
				return Math.min(100, Math.round((this.currentStats.pomodoros / 8) * 100))
			},
			
			// 计算本周统计
			calculateWeekStats() {
				// 模拟本周数据
				return {
					pomodoros: 28,
					focusMinutes: 700,
					avgSessionTime: 25,
					completionRate: 85
				}
			},
			
			// 计算本月统计
			calculateMonthStats() {
				// 模拟本月数据
				return {
					pomodoros: 120,
					focusMinutes: 3000,
					avgSessionTime: 25,
					completionRate: 80
				}
			},
			
			// 切换时间范围
			switchTimeRange(range) {
				this.timeRange = range
				this.updateCurrentStats()
			},
			
			// 加载历史数据
			loadHistoryData() {
				// 模拟历史数据
				this.historyData = [
					{
						date: '2024-01-15',
						pomodoros: 8,
						focusMinutes: 200,
						completionRate: 100
					},
					{
						date: '2024-01-14',
						pomodoros: 6,
						focusMinutes: 150,
						completionRate: 75
					},
					{
						date: '2024-01-13',
						pomodoros: 7,
						focusMinutes: 175,
						completionRate: 88
					},
					{
						date: '2024-01-12',
						pomodoros: 5,
						focusMinutes: 125,
						completionRate: 63
					},
					{
						date: '2024-01-11',
						pomodoros: 9,
						focusMinutes: 225,
						completionRate: 100
					}
				]
				
				// 计算连续天数
				this.calculateStreakDays()
			},
			
			// 计算连续天数
			calculateStreakDays() {
				let streak = 0
				const today = new Date()
				
				for (let i = 0; i < this.historyData.length; i++) {
					const item = this.historyData[i]
					if (item.pomodoros > 0) {
						streak++
					} else {
						break
					}
				}
				
				this.streakDays = streak
			},
			
			// 检查成就
			checkAchievements() {
				// 检查第一个番茄成就
				if (this.totalStats.totalPomodoros >= 1) {
					this.achievements[0].unlocked = true
				}
				
				// 检查连续7天成就
				if (this.streakDays >= 7) {
					this.achievements[1].unlocked = true
				}
				
				// 检查专注100小时成就
				if (this.totalStats.totalFocusMinutes >= 6000) {
					this.achievements[2].unlocked = true
				}
				
				// 检查500个番茄成就
				if (this.totalStats.totalPomodoros >= 500) {
					this.achievements[3].unlocked = true
				}
			}
		}
	}
</script>

<style scoped>
.statistics-container {
	min-height: 100vh;
	background: #f8f9fa;
	padding-bottom: 40rpx;
}

/* 概览部分 */
.overview-section {
	margin: 30rpx 20rpx;
	background: linear-gradient(135deg, #007aff, #5856d6);
	border-radius: 20rpx;
	padding: 40rpx 30rpx;
	color: white;
}

.overview-title {
	font-size: 32rpx;
	font-weight: bold;
	margin-bottom: 30rpx;
	text-align: center;
}

.overview-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 30rpx;
}

.overview-item {
	text-align: center;
}

.overview-number {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	margin-bottom: 10rpx;
}

.overview-label {
	font-size: 24rpx;
	opacity: 0.9;
}

/* 时间范围选择 */
.time-range-section {
	margin: 30rpx 20rpx;
	background: #ffffff;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.time-range-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 30rpx;
	text-align: center;
}

.time-range-tabs {
	display: flex;
	gap: 10rpx;
}

.time-tab {
	flex: 1;
	text-align: center;
	padding: 20rpx;
	border-radius: 10rpx;
	background: #f8f9fa;
	font-size: 28rpx;
	color: #666666;
	transition: all 0.3s ease;
}

.time-tab.active {
	background: #007aff;
	color: white;
}

/* 详细统计 */
.detail-section {
	margin: 30rpx 20rpx;
	background: #ffffff;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.detail-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 30rpx;
	text-align: center;
}

.detail-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 30rpx;
}

.detail-item {
	text-align: center;
	padding: 20rpx;
	background: #f8f9fa;
	border-radius: 15rpx;
}

.detail-number {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: #007aff;
	margin-bottom: 10rpx;
}

.detail-label {
	font-size: 24rpx;
	color: #666666;
}

/* 历史记录 */
.history-section {
	margin: 30rpx 20rpx;
	background: #ffffff;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.history-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 30rpx;
	text-align: center;
}

.history-item {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.history-item:last-child {
	border-bottom: none;
}

.history-date {
	font-size: 28rpx;
	color: #333333;
	font-weight: bold;
	width: 120rpx;
}

.history-stats {
	flex: 1;
	margin-left: 20rpx;
}

.history-pomodoros {
	display: block;
	font-size: 26rpx;
	color: #007aff;
	font-weight: bold;
	margin-bottom: 5rpx;
}

.history-time {
	font-size: 24rpx;
	color: #666666;
}

.history-progress {
	width: 120rpx;
	text-align: right;
}

.progress-bar {
	width: 80rpx;
	height: 8rpx;
	background: #e9ecef;
	border-radius: 4rpx;
	overflow: hidden;
	margin-bottom: 5rpx;
}

.progress-fill {
	height: 100%;
	background: linear-gradient(90deg, #007aff, #5856d6);
	border-radius: 4rpx;
	transition: width 0.3s ease;
}

.progress-text {
	font-size: 20rpx;
	color: #666666;
}

/* 空状态 */
.empty-state {
	text-align: center;
	padding: 60rpx 20rpx;
}

.empty-icon {
	font-size: 80rpx;
	display: block;
	margin-bottom: 20rpx;
}

.empty-text {
	font-size: 32rpx;
	color: #333333;
	font-weight: bold;
	margin-bottom: 10rpx;
	display: block;
}

.empty-desc {
	font-size: 26rpx;
	color: #666666;
	line-height: 1.5;
}

/* 成就系统 */
.achievement-section {
	margin: 30rpx 20rpx;
	background: #ffffff;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.achievement-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 30rpx;
	text-align: center;
}

.achievement-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20rpx;
}

.achievement-item {
	text-align: center;
	padding: 30rpx 20rpx;
	border-radius: 15rpx;
	background: #f8f9fa;
	border: 2rpx solid #e9ecef;
	transition: all 0.3s ease;
}

.achievement-item.unlocked {
	background: linear-gradient(135deg, #fff3cd, #ffeaa7);
	border-color: #ffc107;
}

.achievement-icon {
	font-size: 48rpx;
	margin-bottom: 15rpx;
	display: block;
}

.achievement-name {
	font-size: 26rpx;
	color: #333333;
	font-weight: bold;
	margin-bottom: 8rpx;
	display: block;
}

.achievement-desc {
	font-size: 22rpx;
	color: #666666;
	line-height: 1.4;
}
</style>
