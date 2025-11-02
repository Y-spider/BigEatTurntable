<!-- 番茄计时器工具 -->
<template>
	<view class="timer-container">
		<cu-custom :isBack="true">
			<block slot="content">番茄计时器</block>
		</cu-custom>
		<view @click="showHelp" style="display: flex; flex-direction: row-reverse; margin: 30rpx;font-size: 45rpx;" class="tips cuIcon-question text-xl">
			
		</view>
		<!-- 计时器显示区域 -->
		<view class="timer-display">
			<view class="timer-circle" :class="{ 'countdown-mode': isRunning }">
				<view class="timer-time">{{ formattedTime }}</view>
				<view class="timer-mode">{{ currentMode }}</view>
				<view class="progress-ring" v-if="isRunning">
					<view class="progress-ring-fill" :style="{ transform: `rotate(${progressAngle}deg)` }"></view>
				</view>
			</view>
		</view>
		
		<!-- 进度条 -->
		<view class="progress-container">
			<view class="progress-bar">
				<view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
			</view>
			<view class="progress-text">{{ Math.round(progressPercent) }}%</view>
		</view>
		
		<!-- 控制按钮 -->
		<view class="control-buttons">
			<button 
				class="control-btn start-btn" 
				:class="{ 'active': isRunning }"
				@click="toggleTimer"
			>
				{{ isRunning ? '暂停' : '开始' }}
			</button>
			<button class="control-btn reset-btn" @click="resetTimer">重置</button>
		</view>
		
		<!-- 模式选择 -->
		<view class="mode-selector">
			<view class="mode-title">选择模式</view>
			<view class="mode-options">
				<view 
					class="mode-option" 
					:class="{ 'active': mode === 'work' }"
					@click="setMode('work')"
				>
					<text class="mode-icon">💼</text>
					<text class="mode-name">工作</text>
					<text class="mode-time">25分钟</text>
				</view>
				<view 
					class="mode-option" 
					:class="{ 'active': mode === 'break' }"
					@click="setMode('break')"
				>
					<text class="mode-icon">☕</text>
					<text class="mode-name">休息</text>
					<text class="mode-time">5分钟</text>
				</view>
				<view 
					class="mode-option" 
					:class="{ 'active': mode === 'longBreak' }"
					@click="setMode('longBreak')"
				>
					<text class="mode-icon">🏖️</text>
					<text class="mode-name">长休息</text>
					<text class="mode-time">15分钟</text>
				</view>
			</view>
		</view>
		
		<!-- 快速统计 -->
		<view class="quick-stats">
			<view class="quick-stat-item" @click="goToStatistics">
				<text class="quick-stat-number">{{ todayStats.completedPomodoros }}</text>
				<text class="quick-stat-label">今日番茄</text>
			</view>
			<view class="quick-stat-item" @click="goToStatistics">
				<text class="quick-stat-number">{{ totalStats.totalPomodoros }}</text>
				<text class="quick-stat-label">累计番茄</text>
			</view>
		</view>
		
		
		<!-- 底部操作栏 -->
		<view class="bottom-actions">
			<button class="action-btn" @click="goToSettings">
				<text class="action-icon">⚙️</text>
				<text class="action-text">设置</text>
			</button>
			<button class="action-btn" @click="goToStatistics">
				<text class="action-icon">📊</text>
				<text class="action-text">统计</text>
			</button>
		</view>
		
		<!-- 帮助弹窗 -->
		<view class="modal-overlay" v-if="showHelpModal" @click="hideHelp">
			<view class="help-modal" @click.stop>
				<view class="modal-header">
					<text class="modal-title">番茄工作法使用说明</text>
					<text class="close-btn" @click="hideHelp">×</text>
				</view>
				<view class="modal-content">
					<view class="help-section">
						<text class="help-section-title">什么是番茄工作法？</text>
						<text class="help-section-content">番茄工作法是一种时间管理方法，将工作时间分割为25分钟的专注时间（番茄时间）和5分钟的短暂休息，每4个番茄时间后进行15-30分钟的长休息。</text>
					</view>
					<view class="help-section">
						<text class="help-section-title">科学原理</text>
						<text class="help-section-content">• 大脑专注力有限，25分钟是理想的专注时间长度\n• 短暂休息有助于大脑恢复，提高长期效率\n• 规律的工作节奏有助于建立良好的工作习惯\n• 时间分割减少拖延，增加任务完成感</text>
					</view>
					<view class="help-section">
						<text class="help-section-title">使用建议</text>
						<text class="help-section-content">• 选择一个任务，专注25分钟\n• 休息时远离工作，适当活动\n• 记录完成的番茄数量，追踪进度\n• 根据个人情况调整时间长度</text>
					</view>
				</view>
			</view>
		</view>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// 计时器状态
				isRunning: false,
				timeLeft: 25 * 60, // 剩余时间（秒）
				mode: 'work', // 当前模式：work, break, longBreak
				timer: null,
				
				// 设置
				settings: {
					workTime: 25,
					breakTime: 5,
					longBreakTime: 15,
					musicVolume: 50
				},
				
				// 统计信息
				todayStats: {
					completedPomodoros: 0,
					focusMinutes: 0
				},
				totalStats: {
					totalPomodoros: 0,
					totalFocusMinutes: 0
				},
				
				// 主题和音乐
				currentTheme: {
					id: 'blue',
					name: '经典蓝',
					primary: '#007aff',
					secondary: '#5856d6'
				},
				currentMusic: {
					id: 'none',
					name: '无音乐',
					icon: '🔇',
					description: '静音模式',
					url: ''
				},
				
				// 弹窗状态
				showHelpModal: false,
				
				// 音频播放器
				audioContext: null,
				backgroundMusic: null
			}
		},
		computed: {
			// 格式化显示时间
			formattedTime() {
				const minutes = Math.floor(this.timeLeft / 60)
				const seconds = this.timeLeft % 60
				return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
			},
			
			// 当前模式显示文本
			currentMode() {
				const modeMap = {
					work: '专注工作',
					break: '短暂休息',
					longBreak: '长休息'
				}
				return modeMap[this.mode] || '专注工作'
			},
			
			// 进度百分比
			progressPercent() {
				const totalTime = this.getTotalTime()
				return ((totalTime - this.timeLeft) / totalTime) * 100
			},
			
			// 进度角度（用于环形进度条）
			progressAngle() {
				return (this.progressPercent / 100) * 360
			}
		},
		onLoad() {
			this.loadSettings()
			this.loadTodayStats()
			this.loadTotalStats()
			this.loadThemeAndMusic()
			this.setMode('work')
		},
		onUnload() {
			if (this.timer) {
				clearInterval(this.timer)
			}
			// 停止背景音乐
			this.stopBackgroundMusic()
		},
		methods: {
			/*
			==========================================
			数据库设计 - 番茄计时器相关表结构
			==========================================
			
			-- 番茄计时器会话统计表
			CREATE TABLE pomodoro_sessions (
				id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
				user_id VARCHAR(50) NOT NULL COMMENT '用户ID',
				session_date DATE NOT NULL COMMENT '会话日期',
				work_sessions INT DEFAULT 0 COMMENT '工作会话数',
				break_sessions INT DEFAULT 0 COMMENT '休息会话数',
				long_break_sessions INT DEFAULT 0 COMMENT '长休息会话数',
				total_work_time INT DEFAULT 0 COMMENT '总工作时间(分钟)',
				total_break_time INT DEFAULT 0 COMMENT '总休息时间(分钟)',
				focus_score DECIMAL(3,1) DEFAULT 0.0 COMMENT '专注评分(1-10)',
				notes TEXT COMMENT '备注',
				created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
				updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
				UNIQUE KEY uk_user_date (user_id, session_date),
				INDEX idx_user_date (user_id, session_date)
			) COMMENT '番茄计时器统计表';
			
			-- 番茄计时器详细记录表
			CREATE TABLE pomodoro_records (
				id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
				user_id VARCHAR(50) NOT NULL COMMENT '用户ID',
				session_id BIGINT NOT NULL COMMENT '会话ID',
				record_type ENUM('work', 'break', 'long_break') NOT NULL COMMENT '记录类型',
				duration_minutes INT NOT NULL COMMENT '持续时间(分钟)',
				planned_duration INT NOT NULL COMMENT '计划持续时间(分钟)',
				start_time TIMESTAMP NOT NULL COMMENT '开始时间',
				end_time TIMESTAMP COMMENT '结束时间',
				is_completed TINYINT(1) DEFAULT 0 COMMENT '是否完成',
				interruption_count INT DEFAULT 0 COMMENT '中断次数',
				created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
				FOREIGN KEY (session_id) REFERENCES pomodoro_sessions(id) ON DELETE CASCADE,
				INDEX idx_user_time (user_id, start_time),
				INDEX idx_session_type (session_id, record_type)
			) COMMENT '番茄计时器详细记录表';
			
			==========================================
			本地存储数据结构 (uni.setStorageSync)
			==========================================
			
			// 用户设置数据
			pomodoro_settings: {
				workTime: 25,        // 工作时长(分钟)
				breakTime: 5,        // 休息时长(分钟)
				longBreakTime: 15,   // 长休息时长(分钟)
				autoStart: false,    // 是否自动开始下一个阶段
				soundEnabled: true,  // 是否启用声音提醒
				vibrationEnabled: true // 是否启用震动提醒
			}
			
			// 今日统计数据
			pomodoro_stats_2024-01-15: {
				completedPomodoros: 8,     // 完成的番茄数
				totalWorkTime: 200,        // 总工作时间(分钟)
				totalBreakTime: 40,        // 总休息时间(分钟)
				focusScore: 8.5,           // 专注评分
				interruptionCount: 3,      // 中断次数
				lastSessionTime: "2024-01-15 16:30:00" // 最后会话时间
			}
			
			// 历史会话数据
			pomodoro_sessions: [
				{
					sessionId: "session_20240115_001",
					date: "2024-01-15",
					records: [
						{
							recordId: "record_001",
							type: "work",
							startTime: "2024-01-15 09:00:00",
							endTime: "2024-01-15 09:25:00",
							duration: 25,
							plannedDuration: 25,
							isCompleted: true,
							interruptionCount: 0
						},
						{
							recordId: "record_002", 
							type: "break",
							startTime: "2024-01-15 09:25:00",
							endTime: "2024-01-15 09:30:00",
							duration: 5,
							plannedDuration: 5,
							isCompleted: true,
							interruptionCount: 0
						}
					]
				}
			]
			
			==========================================
			API接口预留
			==========================================
			*/
			
			// 获取当前模式的总时间
			getTotalTime() {
				const timeMap = {
					work: this.settings.workTime * 60,
					break: this.settings.breakTime * 60,
					longBreak: this.settings.longBreakTime * 60
				}
				return timeMap[this.mode] || 25 * 60
			},
			
			// 设置模式
			setMode(newMode) {
				this.mode = newMode
				this.timeLeft = this.getTotalTime()
				if (this.isRunning) {
					this.toggleTimer()
				}
			},
			
			// 切换计时器状态
			toggleTimer() {
				if (this.isRunning) {
					this.pauseTimer()
				} else {
					this.startTimer()
				}
			},
			
			// 开始计时
			startTimer() {
				this.isRunning = true
				
				// 播放背景音乐
				this.playBackgroundMusic()
				
				this.timer = setInterval(() => {
					this.timeLeft--
					if (this.timeLeft <= 0) {
						this.completeTimer()
					}
				}, 1000)
			},
			
			// 暂停计时
			pauseTimer() {
				this.isRunning = false
				
				// 停止背景音乐
				this.stopBackgroundMusic()
				
				if (this.timer) {
					clearInterval(this.timer)
					this.timer = null
				}
			},
			
			// 重置计时器
			resetTimer() {
				this.pauseTimer()
				this.timeLeft = this.getTotalTime()
			},
			
			// 完成计时
			completeTimer() {
				this.pauseTimer()
				
				// 播放提示音
				this.playNotification()
				
				// 更新统计
				this.updateStats()
				
				// 显示完成提示
				uni.showModal({
					title: '时间到！',
					content: this.mode === 'work' ? '工作时间结束，该休息了！' : '休息时间结束，准备开始工作！',
					showCancel: false,
					success: () => {
						// 自动切换到下一个模式
						this.autoSwitchMode()
					}
				})
			},
			
			// 自动切换模式
			autoSwitchMode() {
				if (this.mode === 'work') {
					// 工作完成后切换到休息
					this.setMode('break')
				} else {
					// 休息完成后切换回工作
					this.setMode('work')
				}
			},
			
			// 播放通知音
			playNotification() {
				// 微信小程序中播放系统提示音
				uni.vibrateShort()
			},
			
			// 更新统计信息
			updateStats() {
				if (this.mode === 'work') {
					this.todayStats.completedPomodoros++
					this.todayStats.focusMinutes += this.settings.workTime
					this.totalStats.totalPomodoros++
					this.totalStats.totalFocusMinutes += this.settings.workTime
				}
				this.saveTodayStats()
				this.saveTotalStats()
			},
			
			// 跳转到设置页面
			goToSettings() {
				uni.navigateTo({
					url: '/pages-tools/tools-components/timer-tool/settings-page'
				})
			},
			
			// 跳转到统计页面
			goToStatistics() {
				uni.navigateTo({
					url: '/pages-tools/tools-components/timer-tool/statistics-page'
				})
			},
			
			// 保存设置
			saveSettings() {
				uni.setStorageSync('pomodoro_settings', this.settings)
			},
			
			// 加载设置
			loadSettings() {
				const saved = uni.getStorageSync('pomodoro_settings')
				if (saved) {
					this.settings = { ...this.settings, ...saved }
				}
			},
			
			// 保存今日统计
			saveTodayStats() {
				const today = new Date().toDateString()
				uni.setStorageSync(`pomodoro_stats_${today}`, this.todayStats)
			},
			
			// 加载今日统计
			loadTodayStats() {
				const today = new Date().toDateString()
				const saved = uni.getStorageSync(`pomodoro_stats_${today}`)
				if (saved) {
					this.todayStats = saved
				}
			},
			
			// 保存累计统计
			saveTotalStats() {
				uni.setStorageSync('pomodoro_total_stats', this.totalStats)
			},
			
			// 加载累计统计
			loadTotalStats() {
				const saved = uni.getStorageSync('pomodoro_total_stats')
				if (saved) {
					this.totalStats = saved
				}
			},
			
			// 加载主题和音乐设置
			loadThemeAndMusic() {
				const savedTheme = uni.getStorageSync('pomodoro_theme')
				if (savedTheme) {
					this.currentTheme = savedTheme
				}
				
				const savedMusic = uni.getStorageSync('pomodoro_music')
				if (savedMusic) {
					this.currentMusic = savedMusic
				}
			},
			
			/*
			==========================================
			API接口预留方法
			==========================================
			*/
			
			// 同步会话数据到服务器
			async syncSessionToServer(sessionData) {
				try {
					console.log('准备同步会话数据到服务器:', sessionData)
					
					// TODO: 实现API调用
					// const response = await uni.request({
					//     url: 'https://your-api.com/pomodoro/sessions',
					//     method: 'POST',
					//     data: {
					//         userId: this.getUserId(),
					//         sessionData: sessionData
					//     }
					// })
					
					// 模拟API响应
					console.log('会话数据同步成功')
					return { success: true, message: '数据同步成功' }
				} catch (error) {
					console.error('同步会话数据失败:', error)
					return { success: false, message: '数据同步失败' }
				}
			},
			
			// 从服务器获取统计数据
			async fetchStatisticsFromServer() {
				try {
					console.log('准备从服务器获取统计数据')
					
					// TODO: 实现API调用
					// const response = await uni.request({
					//     url: 'https://your-api.com/pomodoro/statistics',
					//     method: 'GET',
					//     data: {
					//         userId: this.getUserId(),
					//         dateRange: 'week' // week, month, year
					//     }
					// })
					
					// 模拟API响应数据
					const mockData = {
						weeklyStats: {
							totalPomodoros: 35,
							totalWorkTime: 875, // 分钟
							averageFocusScore: 8.2,
							completionRate: 0.85
						},
						dailyStats: [
							{ date: '2024-01-15', pomodoros: 8, workTime: 200 },
							{ date: '2024-01-14', pomodoros: 6, workTime: 150 },
							{ date: '2024-01-13', pomodoros: 7, workTime: 175 }
						]
					}
					
					console.log('获取统计数据成功:', mockData)
					return { success: true, data: mockData }
				} catch (error) {
					console.error('获取统计数据失败:', error)
					return { success: false, message: '获取数据失败' }
				}
			},
			
			// 同步设置到服务器
			async syncSettingsToServer(settings) {
				try {
					console.log('准备同步设置到服务器:', settings)
					
					// TODO: 实现API调用
					// const response = await uni.request({
					//     url: 'https://your-api.com/pomodoro/settings',
					//     method: 'PUT',
					//     data: {
					//         userId: this.getUserId(),
					//         settings: settings
					//     }
					// })
					
					console.log('设置同步成功')
					return { success: true, message: '设置同步成功' }
				} catch (error) {
					console.error('同步设置失败:', error)
					return { success: false, message: '设置同步失败' }
				}
			},
			
			// 获取用户ID (需要根据实际项目实现)
			getUserId() {
				// TODO: 从用户登录状态或本地存储获取用户ID
				return 'user_' + Date.now() // 临时实现
			},
			
			/*
			==========================================
			新增功能方法
			==========================================
			*/
			
			
			// 显示帮助
			showHelp() {
				this.showHelpModal = true
			},
			
			// 隐藏帮助
			hideHelp() {
				this.showHelpModal = false
			},
			
			
			// 播放背景音乐
			playBackgroundMusic() {
				if (this.currentMusic.id === 'none') {
					this.stopBackgroundMusic()
					return
				}
				
				// 停止当前音乐
				this.stopBackgroundMusic()
				
				// 创建新的音频实例
				this.backgroundMusic = uni.createInnerAudioContext()
				this.backgroundMusic.src = this.currentMusic.url
				this.backgroundMusic.loop = true
				this.backgroundMusic.volume = this.settings.musicVolume / 100
				
				this.backgroundMusic.onPlay(() => {
					console.log('背景音乐开始播放')
				})
				
				this.backgroundMusic.onError((res) => {
					console.error('音乐播放失败:', res)
					uni.showToast({
						title: '音乐播放失败',
						icon: 'none'
					})
				})
				
				this.backgroundMusic.play()
			},
			
			// 停止背景音乐
			stopBackgroundMusic() {
				if (this.backgroundMusic) {
					this.backgroundMusic.stop()
					this.backgroundMusic.destroy()
					this.backgroundMusic = null
				}
			}
		},
		watch: {
			// 监听设置变化
			settings: {
				handler() {
					this.saveSettings()
					// 如果当前没有运行，更新时间显示
					if (!this.isRunning) {
						this.timeLeft = this.getTotalTime()
					}
				},
				deep: true
			}
		}
	}
</script>

<style scoped>
.timer-container {
	min-height: 100vh;
	background: #f8f9fa;
	padding-bottom: 120rpx;
}

/* 计时器显示区域 */
.timer-display {
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 80rpx 0;
}

.timer-circle {
	width: 400rpx;
	height: 400rpx;
	background: #ffffff;
	border-radius: 50%;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
	border: 2rpx solid #e9ecef;
}

.timer-time {
	font-size: 80rpx;
	font-weight: bold;
	color: #007aff;
	margin-bottom: 20rpx;
	font-family: 'Courier New', monospace;
}

.timer-mode {
	font-size: 28rpx;
	color: #666666;
}

/* 进度条 */
.progress-container {
	margin: 40rpx 60rpx;
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.progress-bar {
	flex: 1;
	height: 12rpx;
	background: #e9ecef;
	border-radius: 6rpx;
	overflow: hidden;
}

.progress-fill {
	height: 100%;
	background: linear-gradient(90deg, #007aff, #5856d6);
	border-radius: 6rpx;
	transition: width 1s ease;
}

.progress-text {
	font-size: 24rpx;
	color: #333333;
	font-weight: bold;
	min-width: 80rpx;
	text-align: right;
}

/* 控制按钮 */
.control-buttons {
	display: flex;
	justify-content: center;
	gap: 40rpx;
	margin: 60rpx 0;
}

.control-btn {
	width: 200rpx;
	height: 80rpx;
	border-radius: 40rpx;
	border: none;
	font-size: 32rpx;
	font-weight: bold;
	color: white;
	background: #6c757d;
	transition: all 0.3s ease;
}

.start-btn.active {
	background: linear-gradient(45deg, #007aff, #5856d6);
	box-shadow: 0 8rpx 20rpx rgba(0, 122, 255, 0.3);
}

.reset-btn {
	background: #6c757d;
}

.control-btn:active {
	transform: scale(0.95);
}

/* 模式选择 */
.mode-selector {
	margin: 40rpx 30rpx;
	background: #ffffff;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.mode-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 30rpx;
	text-align: center;
}

.mode-options {
	display: flex;
	gap: 20rpx;
}

.mode-option {
	flex: 1;
	background: #f8f9fa;
	border-radius: 15rpx;
	padding: 30rpx 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 15rpx;
	transition: all 0.3s ease;
	border: 2rpx solid transparent;
}

.mode-option.active {
	background: #e3f2fd;
	border-color: #007aff;
	transform: scale(1.05);
}

.mode-icon {
	font-size: 40rpx;
}

.mode-name {
	font-size: 28rpx;
	color: #333333;
	font-weight: bold;
}

.mode-time {
	font-size: 24rpx;
	color: #666666;
}

/* 快速统计 */
.quick-stats {
	margin: 40rpx 30rpx;
	background: #ffffff;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	display: flex;
	gap: 20rpx;
}

.quick-stat-item {
	flex: 1;
	text-align: center;
	padding: 20rpx;
	background: #f8f9fa;
	border-radius: 15rpx;
	transition: all 0.3s ease;
}

.quick-stat-item:active {
	transform: scale(0.95);
	background: #e3f2fd;
}

.quick-stat-number {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: #007aff;
	margin-bottom: 8rpx;
}

.quick-stat-label {
	font-size: 24rpx;
	color: #666666;
}


/* 底部操作栏 */
.bottom-actions {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: #ffffff;
	box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.1);
	padding: 20rpx;
	display: flex;
	gap: 20rpx;
}

.action-btn {
	flex: 1;
	height: 80rpx;
	background: #f8f9fa;
	border-radius: 40rpx;
	border: none;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 10rpx;
}

.action-icon {
	font-size: 32rpx;
}

.action-text {
	font-size: 28rpx;
	color: #333333;
	font-weight: bold;
}

/* 帮助图标 */
.help-icon {
	width: 60rpx;
	height: 60rpx;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 20rpx;
}

.help-text {
	font-size: 32rpx;
	color: white;
	font-weight: bold;
}

/* 倒计时模式样式 */
.timer-circle.countdown-mode {
	background: linear-gradient(135deg, #007aff, #5856d6);
	box-shadow: 0 8rpx 32rpx rgba(0, 122, 255, 0.3);
}

.timer-circle.countdown-mode .timer-time {
	color: white;
}

.timer-circle.countdown-mode .timer-mode {
	color: rgba(255, 255, 255, 0.9);
}

/* 环形进度条 */
.progress-ring {
	position: absolute;
	top: -10rpx;
	left: -10rpx;
	width: 420rpx;
	height: 420rpx;
	border-radius: 50%;
	border: 8rpx solid rgba(255, 255, 255, 0.2);
}

.progress-ring-fill {
	position: absolute;
	top: -8rpx;
	left: -8rpx;
	width: 420rpx;
	height: 420rpx;
	border-radius: 50%;
	border: 8rpx solid transparent;
	border-top-color: rgba(255, 255, 255, 0.8);
	transform-origin: center;
	transition: transform 1s ease;
}


/* 弹窗样式 */
.modal-overlay {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 9999;
}

.help-modal, .selector-modal {
	background: white;
	border-radius: 20rpx;
	margin: 40rpx;
	max-width: 600rpx;
	max-height: 80vh;
	overflow: hidden;
}

.modal-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #e9ecef;
}

.modal-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
}

.close-btn {
	font-size: 48rpx;
	color: #999999;
	width: 60rpx;
	height: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.modal-content {
	padding: 30rpx;
	max-height: 60vh;
	overflow-y: auto;
}

.help-section {
	margin-bottom: 40rpx;
}

.help-section-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 15rpx;
	display: block;
}

.help-section-content {
	font-size: 26rpx;
	color: #666666;
	line-height: 1.6;
	display: block;
}

</style>
