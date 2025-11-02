<!-- 番茄计时器设置页面 -->
<template>
	<view class="settings-container">
		<cu-custom :isBack="true">
			<block slot="content">设置</block>
		</cu-custom>
		
		<!-- 基础设置 -->
		<view class="setting-section">
			<view class="section-title">基础设置</view>
			<view class="setting-item">
				<text class="setting-label">工作时长</text>
				<uni-number-box 
					v-model="settings.workTime" 
					:min="5" 
					:max="60" 
					:step="5"
				></uni-number-box>
			</view>
			<view class="setting-item">
				<text class="setting-label">休息时长</text>
				<uni-number-box 
					v-model="settings.breakTime" 
					:min="1" 
					:max="30" 
					:step="1"
				></uni-number-box>
			</view>
			<view class="setting-item">
				<text class="setting-label">长休息时长</text>
				<uni-number-box 
					v-model="settings.longBreakTime" 
					:min="5" 
					:max="60" 
					:step="5"
				></uni-number-box>
			</view>
		</view>
		
		<!-- 主题设置 -->
		<view class="setting-section">
			<view class="section-title">主题设置</view>
			<view class="setting-item" @click="showThemeSelector">
				<text class="setting-label">主题颜色</text>
				<view class="theme-preview" :style="{ backgroundColor: currentTheme.primary }"></view>
				<text class="arrow">></text>
			</view>
			<view class="setting-item" @click="showBackgroundSelector">
				<text class="setting-label">背景图片</text>
				<text class="arrow">></text>
			</view>
		</view>
		
		<!-- 音乐设置 -->
		<view class="setting-section">
			<view class="section-title">专注音乐</view>
			<view class="setting-item" @click="showMusicSelector">
				<text class="setting-label">背景音乐</text>
				<text class="music-name">{{ currentMusic.name }}</text>
				<text class="arrow">></text>
			</view>
			<view class="setting-item">
				<text class="setting-label">音乐音量</text>
				<slider 
					:value="settings.musicVolume" 
					@change="onVolumeChange"
					min="0" 
					max="100" 
					show-value
				/>
			</view>
		</view>
		
		<!-- 其他设置 -->
		<view class="setting-section">
			<view class="section-title">其他设置</view>
			<view class="setting-item">
				<text class="setting-label">自动开始下一个阶段</text>
				<switch 
					:checked="settings.autoStart" 
					@change="onAutoStartChange"
					color="#007aff"
				/>
			</view>
			<view class="setting-item">
				<text class="setting-label">声音提醒</text>
				<switch 
					:checked="settings.soundEnabled" 
					@change="onSoundChange"
					color="#007aff"
				/>
			</view>
			<view class="setting-item">
				<text class="setting-label">震动提醒</text>
				<switch 
					:checked="settings.vibrationEnabled" 
					@change="onVibrationChange"
					color="#007aff"
				/>
			</view>
		</view>
		
		<!-- 主题选择器 -->
		<view class="modal-overlay" v-if="showThemeModal" @click="hideThemeSelector">
			<view class="selector-modal" @click.stop>
				<view class="modal-header">
					<text class="modal-title">选择主题</text>
					<text class="close-btn" @click="hideThemeSelector">×</text>
				</view>
				<view class="theme-grid">
					<view 
						class="theme-option" 
						v-for="theme in themes" 
						:key="theme.id"
						:class="{ 'active': currentTheme.id === theme.id }"
						@click="selectTheme(theme)"
					>
						<view class="theme-color" :style="{ backgroundColor: theme.primary }"></view>
						<text class="theme-name">{{ theme.name }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 音乐选择器 -->
		<view class="modal-overlay" v-if="showMusicModal" @click="hideMusicSelector">
			<view class="selector-modal" @click.stop>
				<view class="modal-header">
					<text class="modal-title">选择专注音乐</text>
					<text class="close-btn" @click="hideMusicSelector">×</text>
				</view>
				<view class="music-list">
					<view 
						class="music-item" 
						v-for="music in focusMusics" 
						:key="music.id"
						:class="{ 'active': currentMusic.id === music.id }"
						@click="selectMusic(music)"
					>
						<view class="music-icon">{{ music.icon }}</view>
						<view class="music-info">
							<text class="music-name">{{ music.name }}</text>
							<text class="music-desc">{{ music.description }}</text>
						</view>
						<view class="music-status" v-if="currentMusic.id === music.id">✓</view>
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
				// 设置
				settings: {
					workTime: 25,
					breakTime: 5,
					longBreakTime: 15,
					musicVolume: 50,
					autoStart: false,
					soundEnabled: true,
					vibrationEnabled: true
				},
				
				// 主题和音乐
				currentTheme: {
					id: 'blue',
					name: '经典蓝',
					primary: '#007aff',
					secondary: '#5856d6'
				},
				themes: [
					{ id: 'blue', name: '经典蓝', primary: '#007aff', secondary: '#5856d6' },
					{ id: 'green', name: '自然绿', primary: '#34c759', secondary: '#30d158' },
					{ id: 'orange', name: '活力橙', primary: '#ff9500', secondary: '#ff6b00' },
					{ id: 'purple', name: '神秘紫', primary: '#af52de', secondary: '#bf5af2' },
					{ id: 'red', name: '热情红', primary: '#ff3b30', secondary: '#ff2d92' }
				],
				
				currentMusic: {
					id: 'none',
					name: '无音乐',
					icon: '🔇',
					description: '静音模式',
					url: ''
				},
				focusMusics: [
					{ id: 'none', name: '无音乐', icon: '🔇', description: '静音模式', url: '' },
					{ id: 'rain', name: '雨声', icon: '🌧️', description: '轻柔的雨声，帮助放松', url: '/static/audio/rain.mp3' },
					{ id: 'river', name: '河流', icon: '🌊', description: '潺潺流水声，自然宁静', url: '/static/audio/river.mp3' },
					{ id: 'forest', name: '森林', icon: '🌲', description: '鸟鸣和风声，清新自然', url: '/static/audio/forest.mp3' },
					{ id: 'ocean', name: '海浪', icon: '🌊', description: '海浪拍岸，舒缓心情', url: '/static/audio/ocean.mp3' },
					{ id: 'wind', name: '晚风', icon: '🍃', description: '轻柔的晚风，宁静致远', url: '/static/audio/wind.mp3' },
					{ id: 'cricket', name: '蝉鸣', icon: '🦗', description: '夏日蝉鸣，专注时光', url: '/static/audio/cricket.mp3' }
				],
				
				// 弹窗状态
				showThemeModal: false,
				showMusicModal: false
			}
		},
		onLoad() {
			this.loadSettings()
			this.loadThemeAndMusic()
		},
		methods: {
			// 加载设置
			loadSettings() {
				const saved = uni.getStorageSync('pomodoro_settings')
				if (saved) {
					this.settings = { ...this.settings, ...saved }
				}
			},
			
			// 保存设置
			saveSettings() {
				uni.setStorageSync('pomodoro_settings', this.settings)
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
			
			// 音量变化
			onVolumeChange(e) {
				this.settings.musicVolume = e.detail.value
				this.saveSettings()
			},
			
			// 自动开始变化
			onAutoStartChange(e) {
				this.settings.autoStart = e.detail.value
				this.saveSettings()
			},
			
			// 声音变化
			onSoundChange(e) {
				this.settings.soundEnabled = e.detail.value
				this.saveSettings()
			},
			
			// 震动变化
			onVibrationChange(e) {
				this.settings.vibrationEnabled = e.detail.value
				this.saveSettings()
			},
			
			// 显示主题选择器
			showThemeSelector() {
				this.showThemeModal = true
			},
			
			// 隐藏主题选择器
			hideThemeSelector() {
				this.showThemeModal = false
			},
			
			// 选择主题
			selectTheme(theme) {
				this.currentTheme = theme
				uni.setStorageSync('pomodoro_theme', theme)
				this.hideThemeSelector()
			},
			
			// 显示背景选择器
			showBackgroundSelector() {
				uni.showToast({
					title: '背景图片功能开发中',
					icon: 'none'
				})
			},
			
			// 显示音乐选择器
			showMusicSelector() {
				this.showMusicModal = true
			},
			
			// 隐藏音乐选择器
			hideMusicSelector() {
				this.showMusicModal = false
			},
			
			// 选择音乐
			selectMusic(music) {
				this.currentMusic = music
				uni.setStorageSync('pomodoro_music', music)
				this.hideMusicSelector()
			}
		},
		watch: {
			// 监听设置变化
			settings: {
				handler() {
					this.saveSettings()
				},
				deep: true
			}
		}
	}
</script>

<style scoped>
.settings-container {
	min-height: 100vh;
	background: #f8f9fa;
	padding-bottom: 40rpx;
}

/* 设置分组 */
.setting-section {
	margin: 30rpx 20rpx;
	background: #ffffff;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 30rpx;
	padding-left: 10rpx;
	border-left: 4rpx solid #007aff;
}

.setting-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
	padding: 20rpx;
	background: #f8f9fa;
	border-radius: 10rpx;
}

.setting-item:last-child {
	margin-bottom: 0;
}

.setting-label {
	font-size: 28rpx;
	color: #333333;
}

.arrow {
	font-size: 32rpx;
	color: #999999;
}

.theme-preview {
	width: 40rpx;
	height: 40rpx;
	border-radius: 50%;
	border: 2rpx solid #e9ecef;
}

.music-name {
	font-size: 24rpx;
	color: #666666;
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

.selector-modal {
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

/* 主题选择器 */
.theme-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 20rpx;
	padding: 30rpx;
}

.theme-option {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20rpx;
	border-radius: 15rpx;
	border: 2rpx solid #e9ecef;
	transition: all 0.3s ease;
}

.theme-option.active {
	border-color: #007aff;
	background: #e3f2fd;
}

.theme-color {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	margin-bottom: 15rpx;
}

.theme-name {
	font-size: 24rpx;
	color: #333333;
}

/* 音乐选择器 */
.music-list {
	padding: 30rpx;
}

.music-item {
	display: flex;
	align-items: center;
	padding: 25rpx;
	margin-bottom: 15rpx;
	border-radius: 15rpx;
	border: 2rpx solid #e9ecef;
	transition: all 0.3s ease;
}

.music-item.active {
	border-color: #007aff;
	background: #e3f2fd;
}

.music-icon {
	font-size: 40rpx;
	margin-right: 20rpx;
}

.music-info {
	flex: 1;
}

.music-name {
	font-size: 28rpx;
	color: #333333;
	font-weight: bold;
	margin-bottom: 5rpx;
	display: block;
}

.music-desc {
	font-size: 24rpx;
	color: #666666;
	display: block;
}

.music-status {
	font-size: 32rpx;
	color: #007aff;
	font-weight: bold;
}
</style>
