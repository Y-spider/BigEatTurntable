<template>
	<view class="spectrum-container">
		<view v-for="(sHeight, index) in spectrum" :key="index" class="spectrum-bar" 
		:style="{height: sHeight+'rpx', width: spectrumWidth+'rpx', 'background-color': sColor}">
		</view>
	</view>
</template>

<script>
	/**
	 * 随机跳动的音谱，单位均为rpx
	 */
	export default {
		name: "nestJumpingSpectrum",
		props: {
			// 音谱宽度
			sWidth: {
				type: Number,
				default: 100
			},
			// 音谱最小高度
			spectrumHeightMin: {
				type: Number,
				default: 10
			},
			// 音谱最大高度
			spectrumHeightMax: {
				type: Number,
				default: 30
			},
			// 音谱宽度
			spectrumWidth: {
				type: Number,
				default: 4
			},
			// 音谱跳动步长
			jumpStep: {
				type: Number,
				default: -20
			},
			// 颜色
			sColor: {
				type: String,
				default: '#ff0000'
			}
		},
		data() {
			return {
				jumping: false, // 是否正在跳动
				animationInterval: null, // 动画定时器
				spectrum: [], // 存储音谱位置的数组
			};
		},
		created() {
			// 初始化音谱位置
			let itemWidth = this.spectrumWidth+2;  //加2是为了补上右间隔2rpx
			const spectrumBarsCount = Math.ceil(this.sWidth / itemWidth);
			for (let i = 0; i < spectrumBarsCount; i++) {
				this.spectrum.push(this.spectrumHeightMin);
			}
		},
		methods: {
			startJumping() {
				if (!this.jumping) {
					this.jumping = true;
					this.animationInterval = setInterval(this.animateSpectrum, 100);
				}
			},
			stopJumping() {
				this.jumping = false;
				clearInterval(this.animationInterval);
			},
			animateSpectrum() {
				for (let i = 0; i < this.spectrum.length; i++) {
					const randomJump = Math.floor(Math.random() * (this.jumpStep * 2 + 1)) - this.jumpStep;
					this.spectrum[i] += randomJump;
					// 确保音谱高度在合理范围内
					this.spectrum[i] = Math.max(this.spectrumHeightMin, Math.min(this.spectrumHeightMax, this.spectrum[i]));
					this.$forceUpdate();
				}
			},
		}
	};
</script>

<style>
	.spectrum-container {
		display: flex;
		flex-direction: row;
		justify-content: center;
		align-items: center;
		height: 100%;
	}

	.spectrum-bar {
		margin-right: 2rpx;
		border-radius: 5px;
		transition: height 0.1s;
		/* 连贯性通过过渡效果实现 */
	}
</style>