<template>
	<view>
		<cu-custom :isBack="false">
			<block slot="content">吃什么呢?૮₍ ˃ ⤙ ˂ ₎ა</block>
		</cu-custom>
		<!-- 按钮区开始 -->
		<view class="choice-bar-wrap">
			<view class="choice-bar">
				<view class="choice-btn" :class="{ active: mode === 'random' }" @click="mode = 'random'">
					<image class="choose-icon" src="../../static/餐饮.png"></image>
					听天由命
				</view>
				<view class="choice-btn" :class="{ active: mode === 'nearby' }" @click="mode = 'nearby'">
					<image class="choose-icon" src="../../static/附近餐厅.png"></image>
					附近餐饮
				</view>
			</view>
		</view>
		<!-- 转盘区域 -->
		<view class="turntable-box">
			<view class="turntable-title">
				{{turntable.title}}
			</view>
			<LuckyWheel ref="myLucky" width="700rpx" height="700rpx" offsetDegree=10 :blocks="blocks"
				:prizes="prizeList" :buttons="buttons" :defaultStyle="defaultStyle" :default-config="defaultConfig"
				@start="startCallBack" @end="endCallBack" />
			<!-- 结果弹框 -->
			<view class="cu-modal" :class="modalName=='DialogModal2'?'show':''">
				<view class="cu-dialog">
					<view class="cu-bar bg-white justify-end">
						<view class="content">抽奖结果</view>
						<view class="action" @tap="hideModal">
							<text class="cuIcon-close text-red"></text>
						</view>
					</view>
					<view class="padding-xl">
						{{randomEmotion()}}{{resultPrize.fonts[0].text}}
					</view>
					<view class="cu-bar bg-white">
						<view class="action margin-0 flex-sub text-yellow " @tap="playAgain()">
							<text></text>再来一次
						</view>
						<view class="action margin-0 flex-sub text-green solid-left">
							<button open-type="share" class="share-btn">分享</button>
							<text>分享</text>
						</view>
						<view class="action margin-0 flex-sub text-red solid-left" @tap="handConfim()">确定</view>
					</view>
				</view>
			</view>
			<view class="fun-button" style="display: flex;justify-content: space-between; align-items: center;">
				<view class="fun-but">
					<button v-if="isShowMaker" class="cu-btn bg-gradual-green shadow"> <text class="cuIcon-form"
							style="margin: 0 10rpx;"></text>菜谱</button>
				</view>
				<view class="fun-but">
					<button class="cu-btn bg-red shadow" @click="goEdit"> <text class="cuIcon-edit"
							style="margin: 0 10rpx;"></text> 编辑</button>
				</view>
			</view>
		</view>
		<!-- 固定类型 -->
		<view style="width: 100vw;position: relative; top: 50rpx;">
			<swiper class="swiper" circular :duration="500">
				<swiper-item v-for="types,index in typeList" :key="index">
					<!-- 按钮容器：横向排列、自动换行、居中、等间距 -->
					<view  class="btn-wrapper" style="display: flex;gap: 15rpx;justify-content: space-evenly;flex-wrap: wrap;">
						<button v-for="item in types" :key="item.id" class="cu-btn round shadow" style="width: 30%;"
							:class="selectedType === item.id ? 'bg-yellow' : 'bg-gray'" @click="selectType(item.id,item)">
							{{ item.title }}
							<view class="cu-tag sm bg-red radius tag" style="position: relative;left: 15px;">系统</view>
						</button>
					</view>
				</swiper-item>
			</swiper>
		</view>
		<!-- 自定义菜单区域 -->
		<view class="custom-select-area">
			<view v-if="customTypes.length > 0" class="custom-tip">
				自定义菜单
			</view>
			<view class="custom-btn-list">
				<button v-for="item in customTypes" :key="item.id" class="cu-btn round shadow custom-btn"
					:class="selectedType === item.id ? 'bg-yellow' : 'bg-gray'" @click="getTuratableDetail(item.id)">
					{{ item.title }}
					<view class='cu-tag sm bg-orange radius' style="margin-left: 10rpx;">自定义</view>
				</button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getUserTurntableInfoAPI,
		getTurntableDetailAPI,
		getAllSystemTurntableAPI
	} from "@/apis/turntableApi.js"
	import {
		saveRecordAPI
	} from "@/apis/rotationRecordApi.js"
	import LuckyWheel from '@/components/@lucky-canvas/uni/lucky-wheel'
	import mySwiperVue from "../../components/my-swiper.vue"
	export default {
		components: {
			LuckyWheel
		},
		data() {
			return {
				typeList: [], // 接口返回的餐类
				resultEmotionList: ["₍ᐢ..ᐢ₎♡", "૮(˶ᵔ ᵕ ᵔ˶)ა", "૮꒰ ˶• ༝ •˶꒱ა", "꒰ᐢ⸝⸝•༝•⸝⸝ᐢ꒱ ​​", "°꒰๑'ꀾ'๑꒱°", "(ᕑᗢᓫ∗)",
					"₍ᐢ.ˬ.⑅ᐢ₎", "ଘ(੭ˊ꒳​ˋ)੭ "
				],
				resultPrize: null,
				defaultConfig: {
					accelerationTime: 2000,
					decelerationTime: 1600,
				},
				blocks: [{
					padding: '13px',
					background: 'red'
				}],
				buttons: [{
						radius: '50px',
						background: '#FFA500'
					},
					{
						radius: '45px',
						background: '#FFA500'
					},
					{
						radius: '40px',
						background: '#FFA500',
						pointer: true,
						fonts: [{
							text: '吃货\n开奖',
							top: '-20px'
						}]
					},
				],
				modalName: "",
				isShowMaker: false,
				selectedItem: null,
				mode: 'random',
				selectedType: 1, // 当前选中的固定类型，默认选中早餐菜单
				prizeList: [
					// { fonts: [{ text: '炒饭', top: '10%' }], background: '#e9e8fe',range:1 },
				],
				customTypes: [
					// {title:"xxx",id:1}
				],
				turntable: {
					"id": 1,
					"title": "早餐",
					type: 1
				},
				audioPlay: null,
				audioEnd: null,
				openMusic: true,
				roatingDuration: 2,
				luckWheel: null,
				isCheckMenu: true,
				pageShowSize:7, // 系统swiper-item每页展示数量
			}
		},
		onLoad() {
			this.initAudio()
			this.luckWheel = this.$refs.myLucky
		},
		destroyed() {
			this.audioPlay.destroy() // 释放资源
			this.audioEnd.destroy()
		},
		methods: {
			randomEmotion() {
				const list = this.resultEmotionList;
				return list[Math.floor(Math.random() * list.length)];
			},
			hideModal() {
				this.modalName = ""
				uni.setStorageSync("routing", false)
			},
			handConfim() {
				let saveRecordData = {
					turntableId: this.turntable.id,
					turntableName: this.turntable.title,
					result: this.resultPrize.fonts[0].text,
					type: this.turntable.type
				}
				saveRecordAPI(saveRecordData)
				this.modalName = ""
				uni.setStorageSync("routing", false)
			},
			// 抽奖结束触发回调
			endCallBack(prize) {
				if (this.isCheckMenu) return;
				this.resultPrize = prize
				this.isShowMaker = prize?.isMake
				this.audioPlay.stop()
				if (this.openMusic) {
					this.audioEnd.play()
				}
				this.modalName = "DialogModal2"
			},
			// 点击抽奖按钮触发回调
			startCallBack() {
				this.isShowMaker = false
				// 先开始旋转
				this.isCheckMenu = false
				let routing = uni.getStorageSync("routing")
				if (routing) {
					// 之前装盘还未出结果，无法再次转动
					return;
				}
				this.checkSetting()
				if (this.openMusic) {
					this.audioPlay.play()
				}
				this.$refs.myLucky.play()
				uni.setStorageSync("routing", true)
				setTimeout(() => {
					// 调用stop停止旋转并传递中奖奖品  不传入小标则可以使用range 权重了
					this.$refs.myLucky.stop()
				}, this.roatingDuration * 1000)
			},
			playAgain() {
				// 再来一次
				this.modalName = ""
				uni.setStorageSync("routing", false)
				this.startCallBack()
			},
			async getTuratableDetail(id) {
				if (uni.getStorageSync("routing")) {
					// 当前正在转动无法切换
					return;
				}
				this.selectedType = id
				let res = await getTurntableDetailAPI(id)
				this.prizeList = JSON.parse(res.data.content)
				this.turntable = res.data
				// 下面是为了强制刷新轮盘内容
				this.isCheckMenu = true
				this.$refs.myLucky?.play?.();
				this.$refs.myLucky?.stop?.(-1);
			},
			async init() {
				uni.setStorageSync("routing", false)
				let res = await getUserTurntableInfoAPI()
				let turntableInfoRes = await getAllSystemTurntableAPI()
				for(let i = 0;i < Math.round(turntableInfoRes.data.length / this.pageShowSize);i++){
					this.typeList.push(turntableInfoRes.data.slice(i*this.pageShowSize,i*this.pageShowSize+this.pageShowSize))
				}
				this.customTypes = res.data.splice(0, 6) // 只	展示前6个
				this.getTuratableDetail(1)
			},
			checkSetting() {
				let duration = uni.getStorageSync("roatingDuration")
				let openMusic = uni.getStorageSync("openMusic")
				this.roatingDuration = duration ? duration : this.roatingDuration
				this.openMusic = openMusic != '' || openMusic != undefined ? openMusic : this.openMusic
			},
			initAudio() {
				this.audioPlay = uni.createInnerAudioContext({
					useWebAudioImplement: true
				});
				this.audioEnd = uni.createInnerAudioContext({
					useWebAudioImplement: true
				});
				this.audioPlay.src = "/static/audio/audioPlayForce_1.mp3"; // 本地或网络音频
				this.audioPlay.loop = true
				this.audioEnd.src = "/static/audio/audioEnd.mp3"
			},
			goEdit() {
				uni.setStorageSync('editPrizeList', this.prizeList)
				uni.navigateTo({
					url: `/pages/editWheel/index?id=${this.selectedType}`
				})
			},
			selectType(id,type) {
				if (uni.getStorageSync("routing")) {
					// 当前正在转动无法切换
					return;
				}
				this.selectedType = type;
				this.turntable = {
					"id": id,type,
					"title": type.title,
					type: 1
				}
				this.getTuratableDetail(id)
			},
		},
		watch: {
			selectedType: {
				handler(newVal, oldVal) {
					if (newVal > 7) {
						let res = this.customTypes.find(item => item.id === newVal)
						this.turntable = {
							id: res.id,
							title: res.title,
							type: 0
						}
					}
				}
			}
		},
		onShow() {
			this.init()
		}
	}
</script>

<style scoped>
	.choice-bar-wrap {
		background-color: #FFA500;
		width: 100%;
		display: flex;
		height: 100rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}

	.choice-bar {
		background: #eeeeee;
		border-radius: 6px;
		font-size: larger;
		width: 80%;
		height: 60rpx;
		display: flex;
		box-shadow: 0 2px 8px rgba(255, 165, 0, 0.08);
		overflow: hidden;
	}

	.choice-btn {
		background: #eeeeee;
		flex: 1;
		font-size: larger;
		text-align: center;
		line-height: 60rpx;
		font-size: 28rpx;
		color: #999;
		transition: all 0.2s;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.choice-btn.active {
		background: #fff;
		color: #FFA500;
		font-weight: bold;
		display: flex;
		font-size: larger;
		justify-content: center;
		align-items: center;
	}

	.choose-icon {
		display: flex;
		justify-content: center;
		align-items: center;
		margin: 0 5rpx;
		width: 50rpx;
		height: 50rpx;
	}

	.turntable-box {
		background-color: #FFA500;
	}

	.fun-but {
		margin: 12rpx;
	}

	.custom-select-area {
		margin: 24rpx 0 0 0;
		padding: 18rpx 0 0 0;
		border-top: 2rpx solid #f0f0f0;
	}

	.custom-tip {
		color: #e54d42;
		font-size: 26rpx;
		margin-bottom: 12rpx;
		padding-left: 24rpx;
	}

	.custom-btn-list {
		display: flex;
		flex-wrap: wrap;
		align-items: flex-start;
		/* 保证左对齐 */
		justify-content: flex-start;
		width: 100vw;
		padding-left: 24rpx;
		box-sizing: border-box;
	}

	.custom-btn {
		margin-right: 20rpx;
		margin-bottom: 16rpx;
		/* 保证按钮宽度自适应内容 */
		min-width: 120rpx;
		max-width: 60vw;
		white-space: nowrap;
	}

	,
	.turntable-title {
		font-size: 36rpx;
		font-weight: bolder;
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 15rpx;
	}

	.share-btn {
		opacity: 0;
		position: absolute;
	}
</style>