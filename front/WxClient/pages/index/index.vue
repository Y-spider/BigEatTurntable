<template>
	<view>
		<!-- 公告弹出框 -->
		<view class="cu-modal" :class="modalName=='Modal'?'show':''" style="z-index: 9999999 !important;">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content" style="font-size: large;font-weight: bold;color:#ffa500;">ฅ( ̳• · • ̳ฅ)公告
					</view>
					<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl" v-html="noticeContent"></view>
			</view>
		</view>
		<cu-custom :isBack="false">
			<block slot="content">好运来૮₍ ˃ ⤙ ˂ ₎ა</block>
		</cu-custom>
		<!-- 按钮区开始 -->
		<view class="choice-bar-wrap">
			<view class="choice-bar">
				<view class="choice-btn" :class="{ active: mode === 'random' }" @click="handleRandomClick()">
					<image class="choose-icon" src="../../static/餐饮.png"></image>
					鸿运自来
				</view>
			</view>
		</view>
		<!-- 转盘区域 -->
		<view v-if="mode!='nearby'">
			<view class="turntable-box" style="z-index: 99999;">
				<view class="turntable-title">
					{{turntable.title}}  <text style="color: #e54d42; font-size: small;" v-if="turntable.limitCount > 0">--剩余({{spinCount}})次数</text>
				</view>
				<LuckyWheel :default-style="defaultStyle" ref="myLucky" style="font-size: smaller;" width="700rpx"
					height="700rpx" offsetDegree=10 :blocks="blocks" :prizes="prizeList" :buttons="buttons"
					:defaultStyle="defaultStyle" :default-config="defaultConfig" @start="startCallBack"
					@end="endCallBack" />
				<!-- 结果弹框 -->
				<view class="cu-modal" :class="modalName=='DialogModal2'?'show':''">
					<view class="cu-dialog">
						<view class="cu-bar bg-white justify-end">
							<view class="content">抽奖结果</view>
							<!-- <view class="action" @tap="handConfim">
								<text class="cuIcon-close text-red"></text>
							</view> -->
						</view>
						<view class="padding-xl">
							{{randomEmotion()}}{{resultPrize.fonts[0].text}}
						</view>
						<view class="cu-bar bg-white">
							<!-- <view v-if="turntable.limitCount == 0" class="action margin-0 flex-sub text-yellow " @tap="playAgain()">
								<text></text>不算~再来一次
							</view> -->
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
						<button open-type="share" class="cu-btn bg-gradual-green shadow"> <text class="cuIcon-share"
								style="margin: 0 10rpx;"></text>分享</button>
					</view>
					<view class="fun-but">
						<button class="cu-btn bg-red shadow" @click="goEdit"> <text class="cuIcon-edit"
								style="margin: 0 10rpx;"></text> 编辑</button>
					</view>
				</view>
			</view>
			<!-- 固定类型 -->
			<view style="width: 100vw;position: relative; top: 50rpx;">
				<view class="section-title-system">
					系统转盘
				</view>
				<swiper class="swiper" circular :duration="500">
					<swiper-item v-for="types,index in typeList" :key="index">
						<!-- 按钮容器：横向排列、自动换行、居中、等间距 -->
						<view class="btn-wrapper"
							style="display: flex;gap: 15rpx;justify-content: space-evenly;flex-wrap: wrap;">
							<button v-for="item in types" :key="item.id" class="cu-btn round shadow" style="width: 30%;"
								:class="selectedType === item.id ? 'bg-red' : 'bg-gray'"
								@click="selectType(item.id,item)">
								{{ item.title }}
								<!-- <view class="cu-tag sm bg-red radius tag" style="position: relative;left: 15px;">系统
								</view> -->
							</button>
						</view>
					</swiper-item>
				</swiper>
			</view>
			<!-- 自定义菜单区域 -->
			<view class="custom-select-area">
				<view v-if="customTypes.length > 0" class="section-title-custom">
					自定义转盘
				</view>
				<view class="custom-btn-list">
					<button v-for="item in customTypes" :key="item.id" class="cu-btn round shadow custom-btn"
						:class="selectedType === item.id ? 'bg-yellow' : 'bg-gray'"
						@click="getTuratableDetail(item.id)">
						{{ item.title }}
						<!-- <view class='cu-tag sm bg-orange radius' style="margin-left: 10rpx;">自定义</view> -->
					</button>
				</view>
			</view>
			<!-- 分享弹框 -->
			<share-pop-dialog ref="sharePopDialogRef" />
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
	import {
		getActiveNoticeAPI
	} from "@/apis/noticeApi.js";
	import {
		getOpenidAPI
	} from "@/apis/userApi.js";
	import {getSpinCountAPI} from "@/apis/rotationRecordApi.js";
	import LuckyWheel from '@/components/@lucky-canvas/uni/lucky-wheel';
	import sharePopDialog from "../../components/share_pop_dialog.vue";
	export default {
		components: {
			LuckyWheel,
			sharePopDialog
		},
		data() {
			return {
				spinCount:0,
				noticeContent: "", // 公告信息
				typeList: [], // 接口返回的餐类
				resultEmotionList: ["₍ᐢ..ᐢ₎♡", "૮(˶ᵔ ᵕ ᵔ˶)ა", "૮꒰ ˶• ༝ •˶꒱ა", "꒰ᐢ⸝⸝•༝•⸝⸝ᐢ꒱ ​​", "°꒰๑'ꀾ'๑꒱°", "(ᕑᗢᓫ∗)",
					"₍ᐢ.ˬ.⑅ᐢ₎", "ଘ(੭ˊ꒳​ˋ)੭ "
				],
				resultPrize: null,
				defaultStyle: {
					fontSize:16,
					wordWrap:true,
					lengthLimit:"90%",
					lineClamp:2
				},
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
							text: '好运\n开奖',
							top: '-20px'
						}]
					},
				],
				modalName: "",
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
				pageShowSize: 9, // 系统swiper-item每页展示数量
				openid: null,
			}
		},
		// 分享逻辑
		async onShareAppMessage() {
			let expireTime = Date.now() + 30 * 60 * 1000;
			uni.setStorageSync("hasPermissionCheckDetail", {
				expireTime
			})
			if (!this.openid) {
				const res = await getOpenidAPI();
				this.openid = res.data;
			}
			this.getTuratableDetail(this.turntable.id)
			return {
				title: this.turntable.title,
				path: "/pages/detail/detail?id=" + this.turntable.id + "&tableName=" + this.turntable.title +
					"&backUrl=/pages/index/index&shareOpenid=" + this.openid,
				withShareTicket: true
			}
		},
		async onLoad(option) {
			this.initAudio()
			this.luckWheel = this.$refs.myLucky
			if(option.shareOpenid){
				// 表示当前用户为用户邀请的用户(也可能是老用户,只管传递至于新老用户由后端判断)
				uni.setStorageSync("shareOpenid",option.shareOpenid);
			}
		},
		destroyed() {
			this.audioPlay.destroy() // 释放资源
			this.audioEnd.destroy()
		},
		async created() {
			this.initActiveNotice();
		},
		methods: {
		
			async initActiveNotice() {
				let noticeRes = await getActiveNoticeAPI()
				this.noticeContent = noticeRes.data?.content || "暂无公告信息 ₍ᐢ.ˬ.⑅ᐢ₎"
				if (this.noticeContent == '暂无公告信息 ₍ᐢ.ˬ.⑅ᐢ₎') {
					return;
				}
				this.modalName = "Modal"
			},
			handleRandomClick() {
				this.mode = 'random';
				this.initActiveNotice();
			},
			hideModal() {
				this.modalName = ""
			},

			randomEmotion() {
				const list = this.resultEmotionList;
				return list[Math.floor(Math.random() * list.length)];
			},
			hideModal() {
				this.modalName = ""
				uni.setStorageSync("routing", false)
			},
			async handConfim() {
				let saveRecordData = {
					turntableId: this.turntable.id,
					turntableName: this.turntable.type == 0 ? this.turntable.title + "-自定义" : this.turntable.title,
					result: this.resultPrize.fonts[0].text,
					type: this.turntable.type
				}
				if(this.turntable.type == 0 && !this.turntable.isRepeat){
					saveRecordData.result = this.resultPrize.fonts[0].text.split("-")[1];
				}else{
					saveRecordData.result = this.resultPrize.fonts[0].text
				}
				await saveRecordAPI(saveRecordData)
				this.modalName = ""
				uni.setStorageSync("routing", false)
				this.getTuratableDetail(this.turntable.id)				
			},
			// 抽奖结束触发回调
			endCallBack(prize) {
				if (this.isCheckMenu) return;
				this.resultPrize = prize
				this.audioPlay.stop()
				if (this.openMusic) {
					this.audioEnd.play()
				}
				this.modalName = "DialogModal2"
			},
			// 点击抽奖按钮触发回调
			async startCallBack() {
				if(this.turntable.limitCount > 0){
					const spinCounntRes = await getSpinCountAPI(this.turntable.id);
					 if(!spinCounntRes) return;
					if(spinCounntRes.data.spinCount <= 0){
						uni.showModal({
							content:"抽奖次数已用完",
							showCancel:false
						})
						return;
					}
				}
				const allZero = this.prizeList.every(item => item.range === 0);
				if(allZero){
					uni.showModal({
						showCancel:false,
						content:"奖品已抽完！",
					})
					return;
				}
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
				uni.setStorageSync("indexSelectId",id)
				this.prizeList = JSON.parse(res.data.content)
				this.turntable = res.data
				if(this.turntable.limitCount > 0){
					setTimeout(async ()=>{
						const spinCountRes = await getSpinCountAPI(id);
						this.spinCount = spinCountRes.data.spinCount;
					},300)
				}
				let tempList = this.prizeList
				if (this.turntable.type == 0 && !this.turntable.isRepeat) {
				 tempList.forEach(prize => {
					if(prize.count == 0 || prize.count==undefined){
						prize.range = 0
					}
				    prize.fonts.forEach(f => {
				      f.text = `剩余:${prize.count || 0}  -` + f.text 
				    })
				  })
				}
				// console.log("tempList",tempList)
				// 下面是为了强制刷新轮盘内容
				this.isCheckMenu = true
				this.$refs.myLucky?.play?.();
				this.$refs.myLucky?.stop?.(-1);
			},
			async init() {
				uni.setStorageSync("routing", false)
				let res = await getUserTurntableInfoAPI()
				let turntableInfoRes = await getAllSystemTurntableAPI()
				this.typeList = []; // 先清空
				let indexSelectedId = uni.getStorageSync("indexSelectId");
				if(indexSelectedId){
					this.selectedType = indexSelectedId
				}else{
					this.selectedType = turntableInfoRes.data[0].id
				}
				for (let i = 0; i < Math.ceil(turntableInfoRes.data.length / this.pageShowSize); i++) {
					this.$set(this.typeList, i, turntableInfoRes.data.slice(i * this.pageShowSize, (i + 1) * this
						.pageShowSize));
				}

				this.customTypes = res.data.splice(0, 6) // 只	展示前6个
				this.getTuratableDetail(this.selectedType)
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
				this.audioPlay.src = "https://www.sunnygo.chat/images/eat-big-turntable/audioPlayForce_1.MP3"; // 本地或网络音频
				this.audioPlay.loop = true
				this.audioEnd.src = "/static/audio/audioEnd.mp3"
			},
			goEdit() {
				uni.setStorageSync('editPrizeList', this.prizeList)
				uni.navigateTo({
					url: `/pages/editWheel/index?id=${this.selectedType}`
				})
			},
			selectType(id, type) {
				if (uni.getStorageSync("routing")) {
					// 当前正在转动无法切换
					return;
				}
				this.selectedType = type;
				uni.setStorageSync("indexSelectId",id);
				this.turntable = {
					"id": id,
					type,
					"title": type.title,
					type: 1
				}
				this.getTuratableDetail(id)
			},
		},
		onShow() {
			this.init()
		}
	}
</script>

<style scoped>
	.section-title-system {
		font-size: 34rpx;
		font-weight: 700;
		color: #333;
		margin: 20rpx 0 30rpx 40rpx;
		padding-left: 20rpx;
		border-left: 8rpx solid #ff5a5f;
		letter-spacing: 2rpx;
	}

	.section-title-custom {
		font-size: 34rpx;
		font-weight: 700;
		color: #333;
		margin: 20rpx 0 30rpx 40rpx;
		padding-left: 20rpx;
		border-left: 8rpx solid #fbbd08;
		letter-spacing: 2rpx;
	}

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
		gap: 10rpx;
		width: 100vw;
		padding-left: 24rpx;
		box-sizing: border-box;
		margin: 0rpx 5rpx;
	}

	.custom-btn {
		margin-right: 20rpx;
		margin-bottom: 16rpx;
		/* 保证按钮宽度自适应内容 */
		width: 28vw;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		text-align: center;
		padding: 0 10rpx;
		box-sizing: border-box;
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