<!-- 工具库页面 -->
<template>
	<view>
		<cu-custom :isBack="false">
			<block slot="content">不纠结星球</block>
		</cu-custom>

		<!-- 工具分类标题 -->
		<view v-if="commandToolList.length > 0" class="section-title">
			<text class="title-text">🔥 热门推荐</text>
		</view>

		<view class="tools-container">
			<view class="tool-item" v-for="(tool, index) in commandToolList" :key="index" @click="navigateToTool(tool)"
				@longpress="showToolTip(tool)">
				<view class="tool-icon" :style="{ background: tool.bgColor }">
					<text class="icon-text">{{ tool.icon }}</text>
				</view>
				<view class="tool-info">
					<view class="tool-name">{{ tool.name }}</view>
					<view class="tool-desc">{{ tool.shortDesc }}</view>
				</view>
			</view>
		</view>


		<!-- 工具分类标题 -->
		<view v-if="funList.length > 0" class="section-title">
			<text class="title-text">🖼️️️ 休闲时刻</text>
		</view>

		<view class="tools-container">
			<view class="tool-item" v-for="(tool, index) in funList" :key="index" @click="navigateToTool(tool)"
				@longpress="showToolTip(tool)">
				<view class="tool-icon" :style="{ background: tool.bgColor }">
					<text class="icon-text">{{ tool.icon }}</text>
				</view>
				<view class="tool-info">
					<view class="tool-name">{{ tool.name }}</view>
					<view class="tool-desc">{{ tool.shortDesc }}</view>
				</view>
			</view>
		</view>

		<!-- 工具分类标题 -->
		<view v-if="dateToolList.length > 0" class="section-title">
			<text class="title-text">⏳️️️ 日期工具</text>
		</view>

		<view class="tools-container">
			<view class="tool-item" v-for="(tool, index) in dateToolList" :key="index" @click="navigateToTool(tool)"
				@longpress="showToolTip(tool)">
				<view class="tool-icon" :style="{ background: tool.bgColor }">
					<text class="icon-text">{{ tool.icon }}</text>
				</view>
				<view class="tool-info">
					<view class="tool-name">{{ tool.name }}</view>
					<view class="tool-desc">{{ tool.shortDesc }}</view>
				</view>
			</view>
		</view>
		
		
		<!-- 系统分类标题 -->
		<view v-if="systemTool.length > 0" class="section-title">
			<text class="title-text">⚙️️️️ 系统工具</text>
		</view>
		
		<view class="tools-container">
			<view class="tool-item" v-for="(tool, index) in systemTool" :key="index""
				@longpress="showToolTip(tool)">
				<view class="tool-icon" :style="{ background: tool.bgColor }">
					<text class="icon-text">{{ tool.icon }}</text>
				</view>
				<view class="tool-info">
					<view class="tool-name">{{ tool.name }}</view>
					<view class="tool-desc">{{ tool.shortDesc }}</view>
				</view>
				<button open-type="contact" style="position: absolute; width: 300rpx;height: 200rpx;opacity: 0;">1</button>
			</view>
		</view>

		<!-- 教程部分 -->
		<view class="section-title">
			<text class="title-text">📚 使用教程</text>
		</view>

		<view class="pocket-item">
			<view class="box-left">
				<view class="icon-box">
					<image style="width: 64rpx; height: 64rpx;" src="/static/转盘使用教程.png" />
				</view>
				<view class="content">
					<view class="content-title">不纠结星球使用教程</view>
					<view class="content-des">关注公众号，发现更多精彩</view>
				</view>
			</view>
			<view class="box-right">
				<view class="icon-box">
					<image style="width: 168rpx; height: 168rpx;" @click="previewImage" src="/static/公众号.jpg" />
				</view>
			</view>
		</view>

		<!-- 工具提示弹窗 -->
		<view class="tooltip-modal" v-if="showTooltip" @click="hideToolTip">
			<view class="tooltip-content" @click.stop>
				<view class="tooltip-title">{{ currentTool.name }}</view>
				<view class="tooltip-desc">{{ currentTool.description }}</view>
				<view class="tooltip-features">
					<text class="feature-label">主要功能：</text>
					<text class="feature-text">{{ currentTool.features }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				showTooltip: false,
				currentTool: {},
				tools: [{
						id: 'timer',
						name: '番茄计时器',
						icon: '⏰',
						shortDesc: '专注工作，提高效率',
						description: '基于番茄工作法的计时器，帮助你专注工作25分钟，然后休息5分钟，循环往复提高工作效率。',
						features: '25分钟专注时间、5分钟休息时间、循环提醒、统计功能',
						path: '/pages-tools/tools-components/timer-tool/timer-tool',
						bgColor: "#ff0000"
					},
					{
						id: 'password',
						name: '密码生成器',
						icon: '🔐',
						shortDesc: '生成安全密码',
						description: '生成各种强度的安全密码，支持自定义长度、字符类型，确保你的账户安全。',
						features: '自定义长度、字符类型选择、强度检测、批量生成',
						path: '/pages-util/tools-components/password-generator/password-generator',
						bgColor: "#aa557f"
					}
				],
				funList: [{
						"id": "day-cat-knowdage",
						"name": "每日猫图+冷知识",
						"icon": "🐾",
						"shortDesc": "让爱猫人乐不可支",
						"description": "每天随机获取萌猫图片，并伴随有趣的猫咪冷知识，带你了解更多猫咪的秘密世界！不仅如此，还有丰富的猫咪科普知识等你来探索。",
						"features": "每日随机猫咪图片 | 精彩猫咪冷知识 | 探索猫咪科普知识",
						"path": "/pages-tools/tools-components/cat-tool/cat-tool",
						"bgColor": "#FFB6C1"
					},
					{
						"id": "day-dog-knowdage",
						"name": "每日狗图+冷知识",
						"icon": "🐾",
						"shortDesc": "让爱狗人乐不可支",
						"description": "每天随机获取可爱狗狗图片，并伴随有趣的狗狗冷知识，带你了解更多狗狗的秘密世界！不仅如此，还有丰富的狗狗科普知识等你来探索。",
						"features": "每日随机狗狗图片 | 精彩狗狗冷知识 | 探索猫狗狗普知识",
						"path": "/pages-tools/tools-components/dog-tool/dog-tool",
						"bgColor": "#f8b400"
					}

				],
				dateToolList: [
					{
						"id": "节日倒计时",
						"name": "节日倒计时助手",
						"icon": "📅",
						"shortDesc": "记录与期待每一个特别的日子",
						"description": "节日倒计时助手，不仅帮你精准记录与提醒各类热门节假日，还支持自定义纪念日与特别日子。每个节日都附带节日起源、文化背景与趣味传说，让你在期待的同时也能感受传统与故事的魅力。",
						"features": "节假日倒计时 | 自定义纪念日 | 节日由来与神话故事 | 节日文化科普",
						"path": "/pages-tools/tools-components/festival-timer/festival-timer",
						"bgColor": "#FFE9C9"
					}

				],
				systemTool:[
					{
					  "id": "联系客服",
					  "name": "联系客服",
					  "icon": "📞",
					  "shortDesc": "遇到问题？随时联系我",
					  "description": "当你在使用过程中遇到疑问、功能异常、建议反馈或账单问题时，可以通过此页面快速联系到我们。支持在线留言、问题提交，以及查看常见问题，确保你在使用过程中始终顺畅无忧。",
					  "features": "问题反馈 | 在线帮助 | 建议与意见 | 常见问题解答",
					  "path": "/pages-tools/tools-components/contact-service/contact-service",
					  "bgColor": "#E6F3FF"
					}
				],
				commandToolList: [{
						"id": "热门转盘",
						"name": "热门转盘",
						"icon": "🎡",
						"shortDesc": "用转盘轻松做决定，让选择更有趣",
						"description": "一个简单又好玩的大转盘工具，助你在犹豫不决时轻松做选择。支持创建多个主题转盘，如美食选择、出行方案、娱乐活动等，也可自定义选项内容和转盘样式。无论是日常小决定还是聚会互动，都能通过轻轻一转，让选择变得更轻松、更有趣。",
						"features": "自定义转盘 | 多主题选择 | 聚会互动 | 随机决策助手",
						"path": "/pages-tools/tools-components/hot-turantable/hot-turantable",
						"bgColor": "#ffa500"
					},
					{
							"id": "节日倒计时",
							"name": "节日倒计时助手",
							"icon": "📅",
							"shortDesc": "记录与期待每一个特别的日子",
							"description": "节日倒计时助手，不仅帮你精准记录与提醒各类热门节假日，还支持自定义纪念日与特别日子。每个节日都附带节日起源、文化背景与趣味传说，让你在期待的同时也能感受传统与故事的魅力。",
							"features": "节假日倒计时 | 自定义纪念日 | 节日由来与神话故事 | 节日文化科普",
							"path": "/pages-tools/tools-components/festival-timer/festival-timer",
							"bgColor": "#FFE9C9"
						},
				]
			}
		},
		methods: {
			// 预览图片
			previewImage() {
				uni.previewImage({
					urls: ["/static/公众号.jpg"]
				})
			},

			// 跳转到工具页面
			navigateToTool(tool) {
				if(tool.id=="联系客服"){
					uni.openCustomerServiceChat();
					return;
				}
				uni.navigateTo({
					url: tool.path
				})
			},

			// 显示工具提示
			showToolTip(tool) {
				this.currentTool = tool
				this.showTooltip = true

				// 3秒后自动隐藏
				setTimeout(() => {
					this.hideToolTip()
				}, 3000)
			},

			// 隐藏工具提示
			hideToolTip() {
				this.showTooltip = false
				this.currentTool = {}
			}
		}
	}
</script>

<style scoped>
	/* 分类标题样式 */
	.section-title {
		margin: 30rpx 20rpx 20rpx 20rpx;
	}

	.title-text {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}

	/* 工具容器样式 - 网格布局 */
	.tools-container {
		margin: 0 20rpx 30rpx 20rpx;
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
	}

	/* 工具项样式 */
	.tool-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		background: #ffffff;
		border-radius: 20rpx;
		padding: 30rpx 20rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
		border: 1rpx solid #f0f0f0;
		transition: all 0.3s ease;
		min-height: 200rpx;
	}

	.tool-item:active {
		transform: scale(0.95);
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
		background: #f8f9fa;
	}

	/* 工具图标样式 */
	.tool-icon {
		width: 80rpx;
		height: 80rpx;
		background: linear-gradient(135deg, #007aff, #5856d6);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 20rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 122, 255, 0.3);
	}

	.icon-text {
		font-size: 36rpx;
	}

	/* 工具信息样式 */
	.tool-info {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 8rpx;
		text-align: center;
	}

	.tool-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333333;
		margin-bottom: 8rpx;
	}

	.tool-desc {
		font-size: 22rpx;
		color: #666666;
		line-height: 1.4;
	}

	/* 教程部分样式 */
	.pocket-item {
		display: flex;
		margin: 15rpx 20rpx;
		background-color: #ffffff;
		border-radius: 15rpx;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
	}

	.box-left {
		display: flex;
		gap: 30rpx;
	}

	.content {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
		justify-content: center;
	}

	.content-title {
		font-weight: bold;
		font-size: medium;
	}

	.content-des {
		font-size: small;
	}

	.box-right {
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 40rpx !important;
	}

	/* 工具提示弹窗样式 */
	.tooltip-modal {
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

	.tooltip-content {
		background: white;
		border-radius: 20rpx;
		padding: 40rpx;
		margin: 40rpx;
		max-width: 600rpx;
		box-shadow: 0 20rpx 40rpx rgba(0, 0, 0, 0.3);
	}

	.tooltip-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		text-align: center;
	}

	.tooltip-desc {
		font-size: 28rpx;
		color: #666;
		line-height: 1.6;
		margin-bottom: 30rpx;
	}

	.tooltip-features {
		background: #f8f9fa;
		border-radius: 15rpx;
		padding: 20rpx;
	}

	.feature-label {
		font-size: 24rpx;
		color: #999;
		display: block;
		margin-bottom: 10rpx;
	}

	.feature-text {
		font-size: 26rpx;
		color: #333;
		line-height: 1.5;
	}
</style>