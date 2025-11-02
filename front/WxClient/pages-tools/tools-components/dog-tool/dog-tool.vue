<template>
	<view>
		<cu-custom :isBack="true" bgColor="bg-color-dog">
			<block slot="content">🐶 每日狗图</block>
		</cu-custom>

		<view class="dog-page">
			<!-- 加载中动画 -->
			<view v-if="loading" class="loading">
				<text>加载中...(狗狗正在摆好姿势 🐾)</text>
				<image class="loading-gif" src="/static/dog_load.gif"></image>
			</view>

			<!-- 狗狗展示卡片 -->
			<view v-else class="dog-card">
				<view class="img-wrapper" @click="previewImage">
					<image :src="dogImg" mode="aspectFill" class="dog-img" />
				</view>

				<view class="dog-info">
					<text class="dog-breed">{{ dogBreed || '未知犬种' }}</text>
				</view>

				<view class="dog-actions">
					<button @click="downloadImage" class="btn download-btn">保存到相册</button>
					<button @click="loadDog" class="btn refresh-btn">换一只 🐕</button>
				</view>
			</view>

			<!-- 狗狗冷知识展示 -->
			<view v-if="dogFact" class="fact-card">
				<text class="fact-title">📘 狗狗冷知识</text>
				<text class="fact-content">{{ dogFact.enText }}</text>
				<text class="fact-content">{{ dogFact.zhText }}</text>
			</view>

			<!-- 狗狗种类信息展示 -->
			<view v-if="dogBreed && breedInfo" class="breed-card">
				<text class="breed-title">🐾 犬种详情</text>

				<view class="breed-field"><text class="label">犬种名称：</text>{{ breedInfo.name }}</view>
				<view class="breed-field"><text class="label">原产地：</text>{{ breedInfo.origin || '未知' }}</view>
				<view class="breed-field"><text class="label">寿命：</text>{{ breedInfo.lifeSpan }} 年</view>
				<view class="breed-field"><text class="label">体重：</text>{{ breedInfo.weightMetric }} KG</view>
				<view class="breed-field" v-if="breedInfo.heightMetric"><text class="label">高度：</text>{{ breedInfo.heightMetric }} CM</view>
								<view class="breed-field" v-else><text class="label">高度：</text>无数据</view>
				<view class="breed-field"><text class="label">性格：</text>{{ breedInfo.temperament }}</view>
				<view class="breed-field"><text class="label">简介：</text>{{ breedInfo.zhDescription || '无' }}</view>
				
				<view class="breed-field"><text class="label">职业：</text>{{ breedInfo.bredFor }}</view>
				<view class="breed-field"><text class="label">分组：</text>{{ breedInfo.breedGroup }}</view>
				<view class="breed-field"><text class="label">亲人度（1-5）：</text>{{ breedInfo.affectionLevel==0 ? 5 : breedInfo.affectionLevel}}</view>
				<view class="breed-field"><text class="label">智商（1-5）：</text>{{ breedInfo.intelligence==0 ? 5 : breedInfo.intelligence}}</view>
				<view class="breed-field"><text class="label">活力值（1-5）：</text>{{ breedInfo.energyLevel==0 ? 5 : breedInfo.energyLevel}}</view>
				<view class="breed-field"><text class="label">掉毛程度（1-5）：</text>{{ breedInfo.sheddingLevel==0 ? 5 : breedInfo.sheddingLevel }}</view>

				<view class="link-section" v-if="breedInfo.wikipediaUrl">
					<navigator :url="breedInfo.wikipedia_url" open-type="navigate" class="link">
						查看更多犬种资料 →
					</navigator>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getDogFactAPI,
		getDogBreedInfoAPI
	} from "../../apis/dogToolApi.js";

	export default {
		data() {
			return {
				dogImg: "",
				dogFact: "",
				dogBreed: "",
				breedInfo: null,
				loading: true,
				downing: false
			};
		},
		methods: {
			previewImage() {
				uni.previewImage({
					urls: [this.dogImg]
				});
			},
			async loadDog() {
				this.loading = true;
				try {
					const imgRes = await uni.request({
						url: "https://api.thedogapi.com/v1/images/search?has_breeds=true&limit=1",
						method: "GET",
					});
					const dog = imgRes.data[0];
					this.dogImg = dog.url;
					
					// 获取物种信息
					const breedRes = await uni.request({
						url: `https://api.thedogapi.com/v1/images/${imgRes.data[0].id}/breeds`,
						method: "GET"
					})
					console.log("breedRes",breedRes)
					if (breedRes.data) {
						const res = await getDogBreedInfoAPI(breedRes.data[0].id);
						this.dogBreed = res.data.zhName;
						this.breedInfo = res.data
					} else {
						this.dogBreed = '未知品种';
					}

					const factRes = await getDogFactAPI();
					this.dogFact = factRes.data;
				} catch (err) {
					console.error(err);
					this.dogFact = "汪汪出错了，请稍后再试 🐾";
				} finally {
					this.loading = false;
				}
			},
			downloadImage() {
				if (this.downing) return;
				this.downing = true;
				uni.downloadFile({
					url: this.dogImg,
					success: (res) => {
						if (res.statusCode === 200) {
							uni.saveImageToPhotosAlbum({
								filePath: res.tempFilePath,
								success() {
									uni.showToast({
										title: "已保存到相册",
										icon: "success"
									});
								},
								fail(err) {
									console.error(err);
									uni.showToast({
										title: "保存失败",
										icon: "none"
									});
								},
								complete: () => {
									this.downing = false;
								}
							});
						}
					},
					fail(err) {
						console.error(err);
						uni.showToast({
							title: "下载失败",
							icon: "none"
						});
						this.downing = false;
					},
				});
			},
		},
		mounted() {
			this.loadDog();
		},
	};
</script>

<style scoped>
	.dog-page {
		min-height: 100vh;
		background: linear-gradient(180deg, #fff8ec 0%, #ffe9b5 100%);
		display: flex;
		flex-direction: column;
		align-items: center;
		padding-bottom: 60rpx;
	}

	.loading {
		display: flex;
		flex-direction: column;
		align-items: center;
		color: #b36b00;
		font-size: 28rpx;
		margin-top: 120rpx;
	}

	.loading-gif {
		width: 96rpx;
		height: 96rpx;
		margin-top: 20rpx;
	}

	.dog-card {
		width: 90vw;
		background: #fff;
		border-radius: 24rpx;
		box-shadow: 0 8rpx 20rpx rgba(255, 190, 60, 0.25);
		margin-top: 40rpx;
		overflow: hidden;
		animation: fadeIn 0.6s ease;
	}

	.img-wrapper {
		position: relative;
		overflow: hidden;
		border-radius: 24rpx 24rpx 0 0;
	}

	.dog-img {
		width: 100%;
		height: 480rpx;
		object-fit: cover;
		transition: transform 0.4s;
	}

	.dog-img:active {
		transform: scale(1.03);
	}

	.dog-info {
		text-align: center;
		padding: 24rpx 0 12rpx 0;
	}

	.dog-breed {
		font-size: 32rpx;
		color: #f2a900;
		font-weight: bold;
		text-shadow: 0 2rpx 4rpx rgba(255, 200, 80, 0.4);
	}

	.dog-actions {
		display: flex;
		justify-content: space-around;
		padding: 20rpx 0 30rpx;
	}

	.btn {
		width: 42%;
		background: linear-gradient(135deg, #f8b400, #ffd166);
		color: #fff;
		border: none;
		border-radius: 50rpx;
		font-size: 28rpx;
		font-weight: 600;
		padding: 20rpx 0;
		box-shadow: 0 4rpx 10rpx rgba(255, 170, 0, 0.4);
		transition: transform 0.2s;
	}

	.btn:active {
		transform: scale(0.95);
	}

	.fact-card,
	.breed-card {
		width: 90vw;
		background: #fff;
		border-radius: 24rpx;
		box-shadow: 0 8rpx 20rpx rgba(255, 190, 60, 0.25);
		padding: 24rpx;
		margin-top: 32rpx;
		line-height: 1.6;
		animation: fadeIn 0.5s ease;
	}

	.fact-title,
	.breed-title {
		display: block;
		font-size: 32rpx;
		font-weight: bold;
		color: #f2a900;
		margin-bottom: 16rpx;
	}

	.fact-content {
		font-size: 28rpx;
		color: #5a4a00;
	}

	.breed-field {
		margin-bottom: 10rpx;
		font-size: 28rpx;
		color: #444;
	}

	.label {
		color: #f8b400;
		font-weight: bold;
	}

	.link-section {
		margin-top: 12rpx;
		text-align: right;
	}

	.link {
		font-size: 26rpx;
		color: #f2a900;
		text-decoration: underline;
	}

	@keyframes fadeIn {
		from {
			opacity: 0;
			transform: translateY(30rpx);
		}
		to {
			opacity: 1;
			transform: translateY(0);
		}
	}
</style>
