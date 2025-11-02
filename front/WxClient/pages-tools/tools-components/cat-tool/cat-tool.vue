<template>
	<view>
		<cu-custom :isBack="true" bgColor="bg-color-cat">
			<block slot="content">🐾 每日猫图</block>
		</cu-custom>

		<view class="cat-page">
			<!-- 加载中动画 -->
			<view v-if="loading" class="loading">
				<text>喵喵加载中...(图片加载较慢，请耐心等待)</text>
				<image class="loading-gif" src="/static/cat_load.gif"></image>
			</view>

			<!-- 猫咪展示卡片 -->
			<view v-else class="cat-card">
				<view class="img-wrapper" @click="previewImage">
					<image :src="catImg" mode="aspectFill" class="cat-img" />
				</view>

				<view class="cat-info">
					<text class="cat-breed">{{ catBreed || '未知猫咪' }}</text>
				</view>

				<view class="cat-actions">
					<button @click="downloadImage" class="btn download-btn">保存到相册</button>
					<button @click="loadCat" class="btn refresh-btn">换一只 😺</button>
				</view>
			</view>

			<!-- 猫咪冷知识展示 -->
			<view v-if="catFact" class="fact-card">
				<text class="fact-title">📘 冷知识</text>
				<text class="fact-content">{{ catFact.enText }}</text>
				<text class="fact-content">{{ catFact.zhText }}</text>
			</view>

			<!-- 猫咪种类信息展示 -->
			<view v-if="catBreed" class="breed-card">
				<text class="breed-title">🐱 品种详情</text>

				<view class="breed-field"><text class="label">品种名称：</text>{{ breedInfo.name }}</view>
				<view class="breed-field"><text class="label">原产地：</text>{{ breedInfo.origin || '未知' }}</view>
				<view class="breed-field"><text class="label">寿命：</text>{{ breedInfo.lifeSpan }} 年</view>
				<view class="breed-field"><text class="label">体重：</text>{{ breedInfo.weightMetric }} KG</view>
				<view class="breed-field"><text class="label">性格：</text>{{ breedInfo.temperament }}</view>
				<view class="breed-field"><text class="label">简介：</text>{{ breedInfo.zhDescription }}</view>
				
				<view class="breed-field"><text class="label">爱躺膝盖：</text>{{ breedInfo.lap ? '喜欢' : '不喜欢' }}</view>
				<view class="breed-field"><text class="label">室内饲养：</text>{{ breedInfo.indoor ? '适合' : '不适合' }}</view>
				<view class="breed-field"><text class="label">智商（1-5）：</text>{{ breedInfo.intelligence }}</view>
				<view class="breed-field"><text class="label">适应性（1-5）：</text>{{ breedInfo.adaptability }}</view>
				<view class="breed-field"><text class="label">喜爱程度（1-5）：</text>{{ breedInfo.affectionLevel }}</view>
				<view class="breed-field"><text class="label">亲人度（1-5）：</text>{{ breedInfo.affectionLevel }}</view>
				<view class="breed-field"><text class="label">活力值（1-5）：</text>{{ breedInfo.energyLevel }}</view>
				<view class="breed-field"><text class="label">掉毛程度（1-5）：</text>{{ breedInfo.sheddingLevel }}</view>
				<view class="breed-field"><text class="label">狗狗友好（1-5）：</text>{{ breedInfo.dogFriendly }}</view>

				<view @click="handleCopy" class="link-section" v-if="breedInfo.wikipediaUrl">
					<navigator :url="breedInfo.wikipedia_url" class="link">
						查看维基百科 →
					</navigator>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getCatFactAPI,
		getCatBreedInfoAPI
	} from "../../apis/catToolApi.js";
	export default {
		data() {
			return {
				catImg: "",
				catFact: "",
				catBreed: "",
				breedInfo: null,
				loading: true,
				downing:false
			};
		},
		methods: {
			handleCopy(){
				uni.setClipboardData({
					data:this.breedInfo.wikipediaUrl
				})
			},
			previewImage() {
				uni.previewImage({
					urls: [this.catImg]
				});
			},
			async loadCat() {
				this.loading = true;
				try {
					const imgRes = await uni.request({
						url: "https://api.thecatapi.com/v1/images/search?has_breeds=true&limit=1",
						method: "GET",
					});
					const cat = imgRes.data[0];
					this.catImg = cat.url;

					// 获取物种信息 
					const breedRes = await uni.request({
						url: `https://api.thecatapi.com/v1/images/${imgRes.data[0].id}`,
						method: "GET"
					})
					if (breedRes.data.breeds) {
						const res = await getCatBreedInfoAPI(breedRes.data.breeds[0].id);
						this.catBreed = res.data.zhName;
						this.breedInfo = res.data
					} else {
						this.catBreed = '未知品种';
					}

					// 获取猫咪冷知识
					const factRes = await getCatFactAPI();
					this.catFact = factRes.data;
				} catch (err) {
					console.error(err);
					this.catFact = "喵喵出错了，请稍后再试 🐾";
				} finally {
					this.loading = false;
				}
			},
			downloadImage() {
				if(this.downing){
					return;
				}
				this.downing = true;
				uni.downloadFile({
					url: this.catImg,
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
								complete(){
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
					},
				});
			},
		},
		mounted() {
			this.loadCat();
		},
	};
</script>

<style scoped>
	.cat-page {
		min-height: 100vh;
		background: linear-gradient(180deg, #fff6f8 0%, #ffeef5 100%);
		display: flex;
		flex-direction: column;
		align-items: center;
		padding-bottom: 60rpx;
	}

	.loading {
		display: flex;
		flex-direction: column;
		align-items: center;
		color: #999;
		font-size: 28rpx;
		margin-top: 120rpx;
	}

	.loading-gif {
		width: 96rpx;
		height: 96rpx;
		margin-top: 20rpx;
	}

	.cat-card {
		width: 90vw;
		background: #fff;
		border-radius: 24rpx;
		box-shadow: 0 8rpx 20rpx rgba(255, 156, 173, 0.2);
		margin-top: 40rpx;
		overflow: hidden;
		animation: fadeIn 0.6s ease;
	}

	.img-wrapper {
		position: relative;
		overflow: hidden;
		border-radius: 24rpx 24rpx 0 0;
	}

	.cat-img {
		width: 100%;
		height: 480rpx;
		object-fit: cover;
		transition: transform 0.4s;
	}

	.cat-img:active {
		transform: scale(1.03);
	}

	.cat-info {
		text-align: center;
		padding: 24rpx 0 12rpx 0;
	}

	.cat-breed {
		font-size: 32rpx;
		color: #ff7ca8;
		font-weight: bold;
		text-shadow: 0 2rpx 4rpx rgba(255, 156, 173, 0.3);
	}

	.cat-actions {
		display: flex;
		justify-content: space-around;
		padding: 20rpx 0 30rpx;
	}

	.btn {
		width: 42%;
		background: linear-gradient(135deg, #ff93b7, #ffb4c9);
		color: #fff;
		border: none;
		border-radius: 50rpx;
		font-size: 28rpx;
		font-weight: 600;
		padding: 20rpx 0;
		box-shadow: 0 4rpx 10rpx rgba(255, 146, 164, 0.4);
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
		box-shadow: 0 8rpx 20rpx rgba(255, 156, 173, 0.2);
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
		color: #ff7ca8;
		margin-bottom: 16rpx;
	}

	.fact-content {
		font-size: 28rpx;
		color: #555;
	}

	.breed-field {
		margin-bottom: 10rpx;
		font-size: 28rpx;
		color: #444;
	}

	.label {
		color: #ff93b7;
		font-weight: bold;
	}

	.link-section {
		margin-top: 12rpx;
		text-align: right;
	}

	.link {
		font-size: 26rpx;
		color: #ff7ca8;
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