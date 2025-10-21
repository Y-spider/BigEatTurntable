<!-- 菜品详情页面 -->
<template>
	<view class="dish-detail-page">
		<cu-custom :isBack="true">
			<block slot="backText">返回</block>
			<block slot="content">{{dishMake ? dishMake.name : '菜品详情'}}</block>
		</cu-custom>

		<scroll-view class="scroll-content" scroll-y v-if="dishMake">
			<!-- 封面图 -->
			<view class="cover-section">
				<image class="cover-img"  @click="handlePreview(dishMake.coverUrl)" :src="dishMake.coverUrl" mode="aspectFill"></image>
				<view class="cover-overlay">
					<view class="dish-desc">{{ dishMake.desc }}</view>
				</view>
			</view>

			<!-- 食材清单 -->
			<view class="section">
				<view class="section-title">
					<text class="cuIcon-form text-orange"></text>
					<text class="title-text">食材清单</text>
				</view>
				<view class="ingredients-list">
					<view v-for="(item, index) in dishMake.ingredients" :key="index" class="ingredient-item">
						<view class="ingredient-name">{{ item.name }}</view>
						<view class="ingredient-amount">{{ item.amount }}</view>
					</view>
				</view>
			</view>

			<!-- 制作步骤 -->
			<view class="section">
				<view class="section-title">
					<text class="cuIcon-list text-blue"></text>
					<text class="title-text">制作步骤</text>
				</view>
				<view class="steps-list">
					<view v-for="(step, index) in dishMake.steps" :key="index" class="step-item">
						<view class="step-header">
							<view class="step-number">{{ index + 1 }}</view>
							<view class="step-desc">{{ step.desc }}</view>
						</view>
						<image v-if="step.imgUrl" @click="handlePreview(step.imgUrl)" class="step-img" :src="step.imgUrl" mode="aspectFill"></image>
					</view>
				</view>
			</view>

			<!-- 小贴士 -->
			<view class="section" v-if="dishMake.tips">
				<view class="section-title">
					<text class="cuIcon-lightbulb text-yellow"></text>
					<text class="title-text">小贴士</text>
				</view>
				<view class="tips-content">
					<rich-text :nodes="dishMake.tips"></rich-text>
				</view>
			</view>
			<!-- 收藏按钮 -->
			<view class="flex align-center justify-end padding">
				<!-- 普通按钮形式 -->
				<button class="cu-btn round bg-gradual-orange margin-right-sm" @click="toggleCollect">
					<text class="cuIcon-favorfill margin-right-xs"
						:class="{ 'text-white': isCollected, 'text-gray': !isCollected }"></text>
					{{ isCollected ? '已收藏' : '收 藏' }}
				</button>
			</view>


			<!-- 底部占位 -->
			<view style="height: 32rpx;"></view>
		</scroll-view>

		<!-- 加载中 -->
		<view v-else class="loading-area">
			<text class="cuIcon-loading2 cuIconfont-spin text-orange"></text>
			<text class="loading-text">加载中...</text>
		</view>
	</view>
</template>

<script>
	import {collectionAPI,cancelAPI,queryAPI} from "@/apis/dishCollectionApi.js";
	import {
		getDishMakeByIdAPI
	} from "@/apis/dishApi.js";
	export default {
		data() {
			return {
				id: undefined, // 菜品ID
				dishMake: undefined, // 菜品详情
				isCollected: false // 收藏状态
			}
		},
		onLoad(option) {
			this.id = option.id;
			this.init();
		},
		methods: {
			handlePreview(url){
				uni.previewImage({
					urls:[url]
				})
			},
			async init() {
				const res = await getDishMakeByIdAPI(this.id);
				if (!res) return;
				this.dishMake = JSON.parse(res.data.content);
				const resCollection = await queryAPI(this.id);
				if(!resCollection) return;
				this.isCollected = resCollection.data
			},
			// 切换收藏状态
			async toggleCollect() {
				this.isCollected = !this.isCollected
				if(this.isCollected){
					await collectionAPI(this.id);
				}else{
					await cancelAPI(this.id);
				}
			}
		}
	}
</script>

<style scoped>
	.dish-detail-page {
		height: 100vh;
		display: flex;
		flex-direction: column;
		background: #f8f8f8;
	}

	.action-right {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 60rpx;
		height: 60rpx;
	}

	.cuIcon-favor {
		font-size: 44rpx;
		color: #ccc;
		transition: color 0.3s;
	}

	.cuIcon-favor.text-red {
		color: #e54d42 !important;
	}

	.scroll-content {
		flex: 1;
		height: 0;
	}

	.cover-section {
		position: relative;
		height: 400rpx;
		overflow: hidden;
	}

	.cover-img {
		width: 100%;
		height: 100%;
	}

	.cover-overlay {
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
		background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
		padding: 48rpx 32rpx 32rpx 32rpx;
	}

	.dish-desc {
		color: #fff;
		font-size: 32rpx;
		line-height: 1.5;
	}

	.section {
		background: #fff;
		margin: 24rpx 24rpx 0 24rpx;
		border-radius: 16rpx;
		padding: 32rpx;
		box-shadow: 0 2px 8px rgba(255, 165, 0, 0.06);
	}

	.section-title {
		display: flex;
		align-items: center;
		margin-bottom: 24rpx;
		padding-bottom: 16rpx;
		border-bottom: 2rpx solid #f0f0f0;
	}

	.title-text {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-left: 12rpx;
	}

	.ingredients-list {
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	.ingredient-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		background: #f8f8f8;
		border-radius: 12rpx;
		padding: 20rpx 24rpx;
	}

	.ingredient-name {
		font-size: 30rpx;
		color: #333;
		font-weight: 500;
	}

	.ingredient-amount {
		font-size: 28rpx;
		color: #FFA500;
		font-weight: bold;
	}

	.steps-list {
		display: flex;
		flex-direction: column;
		gap: 32rpx;
	}

	.step-item {
		background: #f8f8f8;
		border-radius: 16rpx;
		padding: 24rpx;
	}

	.step-header {
		display: flex;
		align-items: flex-start;
		margin-bottom: 16rpx;
	}

	.step-number {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 60rpx;
		height: 60rpx;
		background: #FFA500;
		color: #fff;
		border-radius: 50%;
		font-size: 28rpx;
		font-weight: bold;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.step-desc {
		flex: 1;
		font-size: 30rpx;
		color: #333;
		line-height: 1.6;
		margin-top: 12rpx;
	}

	.step-img {
		width: 100%;
		height: 300rpx;
		border-radius: 12rpx;
		margin-top: 16rpx;
	}

	.tips-content {
		background: #fff9e6;
		border: 2rpx solid #ffe066;
		border-radius: 12rpx;
		padding: 24rpx;
		font-size: 28rpx;
		color: #333;
		line-height: 1.6;
	}

	.loading-area {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.cuIconfont-spin {
		animation: cuIcon-spin 2s infinite linear;
		font-size: 64rpx;
		margin-bottom: 24rpx;
	}

	.loading-text {
		font-size: 28rpx;
		color: #999;
	}

	@keyframes cuIcon-spin {
		0% {
			transform: rotate(0deg);
		}

		100% {
			transform: rotate(360deg);
		}
	}
</style>