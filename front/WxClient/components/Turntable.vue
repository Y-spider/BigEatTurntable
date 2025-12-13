<template>
	<view>
		<view class="turntable-title">
			<view @click="showErCode" style="position: absolute; left: 20rpx; color: #79c241;" class="cuIcon-qr_code text-xxl"></view>
			{{turntable.title}}   <text style="color: #e54d42; font-size: small;" v-if="turntable.limitCount > 0">--剩余({{spinCount}})次数</text>
			<view @click="showTips" style="position: absolute; right: 20rpx; color: #e54d42;" class="cuIcon-question text-xxl"></view>
		</view>
		<LuckyWheel :key="count" ref="myLucky" width="700rpx" height="700rpx" offsetDegree=10 :blocks="blocks"
			:prizes="prizes" :buttons="buttons" :defaultStyle="defaultStyle" :default-config="defaultConfig"
			@start="startCallBack" @end="endCallBack" />
		<!-- 结果弹框 -->
		<view class="cu-modal" :class="modalName=='DialogModal2'?'show':''">
			<!-- <view class="cu-modal" class="DialogModal2"> -->
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">抽奖结果</view>
					<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					{{showResultPrizeText}}
				</view>
				<view class="cu-bar bg-white">
					<!-- <view v-if="turntable.isRepeat" class="action margin-0 flex-sub text-yellow " @tap="playAgain()">
						<text></text>再来一次
					</view> -->
					<!-- <view class="action margin-0 flex-sub text-green solid-left">
						<button open-type="share" class="share-btn">分享</button>
						<text>分享</text>
					</view> -->
					<view class="action margin-0 flex-sub text-red solid-left" @tap="handConfim()">确定</view>
				</view>
			</view>
		</view>
		<view class="cu-modal" :class="modalName=='Modal'?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">转盘二维码</view>
					<view class="action" @tap="modalName = ''">
						<text class="cuIcon-close text-red"></text>
					</view> 
				</view>
				<view class="padding-xl">
					<view @click="previewImage">
						<image :src="erCodeUrl" style="width: 320rpx; height: 360rpx;"></image>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		saveRecordAPI,
		getSpinCountAPI
	} from "@/apis/rotationRecordApi.js";
	import {getErCodeUrlAPI} from "@/apis/turntableApi.js";
	import LuckyWheel from '@/components/@lucky-canvas/uni/lucky-wheel'
	export default {
		components: {
			LuckyWheel
		},
		props: {
			prizeList: {
				type: Array
			},
			// 转盘信息
			turntable: {
				type: Object
			}
		},
		data() {
			return {
				erCodeUrl:"",
				modalName:"",
				spinCount:undefined,
				showResultPrizeText: "",
				openMusic: true,
				roatingDuration: 2,
				modalName: "",
				resultPrize: null,
				count: 0,
				LuckyWheel: null,
				defaultStyle: {
					fontSize: 16,
					wordWrap: true,
					lengthLimit: "90%",
					lineClamp:2
				},
				defaultConfig: {
					accelerationTime: 2000,
					decelerationTime: 1600,
				},
				audioPlay: null,
				audioEnd: null,
				blocks: [{
					padding: '13px',
					background: 'red'
				}],
				prizes: [],
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
							text: '好运\n开奖',
							top: '-20px'
						}]
					},
				],
			}
		},
		created() {
			this.init()
		},
		destroyed() {
			this.audioPlay.destroy() // 释放资源
			this.audioEnd.destroy()
		},
		watch: {
			prizeList: {
				deep: true,
				immediate: true,
				handler(newVal, oldVal) {
					this.count++
					if (!this.turntable.isRepeat) {
						newVal.forEach(prize => {
							if (prize.count == 0 || prize.count == undefined) {
								prize.range = 0
							}
						})
					}
					this.$set(this.prizeList, newVal)
					this.prizes = this.prizeList
				}
			},
			turntable:{
				async handler(newVal){
					if(newVal.limitCount > 0){
						const spinCountRes = await getSpinCountAPI(newVal.id);
						if(!spinCountRes) return;
						this.spinCount = spinCountRes.data.spinCount;
					}
				}
			}
		},
		methods: {
			downloadErCode(){
				// 下载二维码
			},
			previewImage(){
				uni.previewImage({
					urls:[this.erCodeUrl]
				})
			},
			async showErCode(){
				uni.showLoading({
					mask:true,
					title:"加载中..."
				})
				const res = await getErCodeUrlAPI(this.turntable.id);
				if(!res){
					uni.hideLoading()
					return
					};
				this.erCodeUrl = res.data.url;
				this.modalName = "Modal";
				uni.hideLoading()
			},
			showTips() {
				uni.showModal({
					title: "提示",
					showCancel: false,
					content: "1. 不重复抽转盘页面数据会有延迟，具体以抽奖时刻显示的为准!\n 2. 如果修改后页面信息不刷新退出转盘重新进入即可！\n3. 点击二维码可放大预览，长按可以保存。\n 4. 生成的二维码即用户扫描即可进入到当前页面，建议打印线下使用。"
				})
			},
			checkSetting() {
				let duration = uni.getStorageSync("roatingDuration")
				let openMusic = uni.getStorageSync("openMusic")
				this.roatingDuration = duration ? duration : this.roatingDuration
				this.openMusic = openMusic != '' || openMusic != undefined ? openMusic : this.openMusic
			},
			hideModal() {
				this.modalName = ""
				uni.setStorageSync("routing", false)
			},
			playAgain() {
				// 再来一次
				this.modalName = ""
				uni.setStorageSync("routing", false)
				this.startCallBack()
			},
			randomInt(min, max) {
				// 确保 min 和 max 都是整数，并且 min <= max
				min = Math.ceil(min);
				max = Math.floor(max);
				return Math.floor(Math.random() * (max - min + 1)) + min;
			},
			init() {
				this.audioPlay = uni.createInnerAudioContext({
					useWebAudioImplement: true
				});
				this.audioEnd = uni.createInnerAudioContext({
					useWebAudioImplement: true
				});
				this.audioPlay.src = "https://www.sunnygo.chat/images/eat-big-turntable/audioPlayForce_1.MP3"; // 本地或网络音频
				this.audioPlay.loop = true
				this.audioEnd.src = "https://www.sunnygo.chat/images/eat-big-turntable/audioEnd.mp3"
				this.LuckyWheel = this.$refs.myLucky
			},
			// 点击抽奖按钮触发回调
			async startCallBack() {
				if(this.turntable.limitCount > 0){
					const res = await getSpinCountAPI(this.turntable.id);
					if(!res) return;
					if(res.data.spinCount <= 0){
						uni.showModal({
							content:"抽签次数已用完！",
							showCancel:false,
						})
						return;
					}
				}
				const allZero = this.prizeList.every(item => item.range === 0);
				if (allZero) {
					uni.showModal({
						showCancel: false,
						content: "奖品已抽完！",
					})
					return;
				}

				// 先开始旋转
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
			forceFlush() {
				// 强制刷新转盘
				this.$refs.myLucky.play()
				this.$refs.myLucky.stop(-1)
			},
			// 抽奖结束触发回调
			endCallBack(prize) {
				console.log(prize)
				if (!prize.range) return;
				this.resultPrize = prize
				if (this.turntable.type == 0 && !this.turntable.isRepeat) {
					this.showResultPrizeText = this.resultPrize.fonts[0].text.split("-")[1];
				} else {
					this.showResultPrizeText = this.resultPrize.fonts[0].text
				}
				this.audioPlay.stop()
				if (this.openMusic) {
					this.audioEnd.play()
				}
				this.modalName = "DialogModal2"
				if(this.turntable.limitCount > 0){
					this.spinCount--;
				}
			},
			async handConfim() {
				const turntableName = this.turntable.type == 0 ? this.turntable.title + "-自定义" : (this.turntable
					.type == 2 ? this.turntable.title + "-热门" : this.turntable.title);

				let saveRecordData = {
					turntableId: this.turntable.id,
					turntableName: turntableName,
					result: this.showResultPrizeText,
					type: this.turntable.type
				}
				await saveRecordAPI(saveRecordData)
				this.$emit("routeDone", this.resultPrize)
				this.modalName = ""
				uni.setStorageSync("routing", false)
			}
		}
	}
</script>

<style scoped>
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