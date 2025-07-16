<template>
	<view>
		<cu-custom :isBack="false">
			<block slot="content">今天吃什么好呢</block>
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
			<Turntable :turntable="turntable" :key="key_count" @routeDone="getRouteResult" :prizeList="prizeList"
				style="margin: 50rpx;"></Turntable>
			<view class="fun-button" style="display: flex;justify-content: space-between; align-items: center;">
				<view class="fun-but">
					<button v-if="true" class="cu-btn bg-gradual-green shadow"> <text class="cuIcon-form"
							style="margin: 0 10rpx;"></text>菜谱</button>
				</view>
				<view class="fun-but">
					<button class="cu-btn bg-red shadow" @click="goEdit"> <text class="cuIcon-edit"
							style="margin: 0 10rpx;"></text> 编辑</button>
				</view>
			</view>
		</view>
		<!-- 固定类型 -->
		<view class="col-center" style="width: 100vw;">
			<view class="row-center" style="justify-content: space-around; width: 100vw;margin: 10rpx;">
				<button class="cu-btn round shadow" :class="selectedType === 1 ? 'bg-yellow' : 'bg-gray'"
					@click="selectType(1)">早餐<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view>
				</button>
				<button class="cu-btn round shadow" :class="selectedType === 2 ? 'bg-yellow' : 'bg-gray'"
					@click="selectType(2)">午餐<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view>
				</button>
				<button class="cu-btn round shadow" :class="selectedType === 3 ? 'bg-yellow' : 'bg-gray'"
					@click="selectType(3)">晚餐<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view>
				</button>
			</view>
			<view class="row-center" style="width: 60vw;margin: 10rpx;gap: 20rpx;">
				<button class="cu-btn round shadow" :class="selectedType === 4 ? 'bg-yellow' : 'bg-gray'"
					@click="selectType(4)">宵夜<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view>
				</button>
				<button class="cu-btn round shadow" :class="selectedType === 5 ? 'bg-yellow' : 'bg-gray'"
					@click="selectType(5)">减脂餐<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统
					</view></button>
			</view>
			<view class="row-center" style="width: 100vw;margin: 10rpx; justify-content: space-evenly;">
				<button class="cu-btn round shadow" :class="selectedType === 6 ? 'bg-yellow' : 'bg-gray'"
					@click="selectType(6)">随机吃点<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统
					</view></button>
				<button class="cu-btn round shadow" :class="selectedType === 7 ? 'bg-yellow' : 'bg-gray'"
					@click="selectType(7)">喝点奶茶<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统
					</view></button>
			</view>
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
		getTurntableDetailAPI
	} from "@/apis/turntableApi.js"
	import Turntable from "@/components/Turntable.vue"
	export default {
		components: {
			Turntable
		},
		data() {
			return {
				modalName:"",
				key_count: 0,
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
				}
			}
		},
		methods: {
			getRouteResult(data) {
				this.selectedItem = data
				if (this.selectedItem.isMake) {
					this.isShowMaker = true
				} else {
					this.isShowMaker = false
				}
				uni.setStorageSync("routing", false)
			},
			async getTuratableDetail(id) {
				if(uni.getStorageSync("routing")){
					// 当前正在转动无法切换
					return;
				}
				this.selectedType = id
				let res = await getTurntableDetailAPI(id)
				this.prizeList = JSON.parse(res.data.content)
				this.turntable = res.data
				this.key_count++
			},
			async init() {
				uni.setStorageSync("routing", false)
				let res = await getUserTurntableInfoAPI()
				this.customTypes = res.data.splice(0, 6) // 只展示前6个
				this.getTuratableDetail(1)
			},
			goEdit() {
				uni.setStorageSync('editPrizeList', this.prizeList)
				uni.navigateTo({
					url: `/pages/editWheel/index?id=${this.selectedType}`
				})
			},
			selectType(type) {
				if(uni.getStorageSync("routing")){
					// 当前正在转动无法切换
					console.log("无法切换")
					return;
				}
				this.selectedType = type;
				// 根据不同类型执行不同函数
				switch (type) {
					case 1:
						this.turntable = {
							"id": type,
							"title": "早餐",
							type: 1
						}
						this.handleBreakfast();
						break;
					case 2:
						this.turntable = {
							"id": type,
							"title": "午餐",
							type: 1
						}
						this.handleLunch();
						break;
					case 3:
						this.turntable = {
							"id": type,
							"title": "晚餐",
							type: 1
						}
						this.handleDinner();
						break;
					case 4:
						this.turntable = {
							"id": type,
							"title": "宵夜",
							type: 1
						}
						this.handleMidnight();
						break;
					case 5:
						this.turntable = {
							"id": type,
							"title": "减脂餐",
							type: 1
						}
						this.handleDiet();
						break;
					case 6:
						this.turntable = {
							"id": type,
							"title": "随机吃点",
							type: 1
						}
						this.handleRandom();
						break;
					case 7:
						this.turntable = {
							"id": type,
							"title": "喝点奶茶",
							type: 1
						}
						this.handleMilkTea();
						break;
				}
			},
			// 早餐菜单处理函数
			handleBreakfast() {
				// 这里可以添加你的逻辑，比如跳转页面、显示数据等
				this.getTuratableDetail(1)
			},
			// 午餐菜单处理函数
			handleLunch() {
				console.log('选择了午餐菜单');
				this.getTuratableDetail(2)
			},
			// 晚餐菜单处理函数
			handleDinner() {
				console.log('选择了晚餐菜单');
				this.getTuratableDetail(3)
			},
			// 宵夜加餐处理函数
			handleMidnight() {
				console.log('选择了宵夜加餐');
				this.getTuratableDetail(4)
			},
			// 减脂菜谱处理函数
			handleDiet() {
				console.log('选择了减脂菜谱');
				this.getTuratableDetail(5)
			},
			// 随机吃处理函数
			handleRandom() {
				console.log('选择了随机吃');
				this.getTuratableDetail(6)
			},
			// 喝点奶茶处理函数
			handleMilkTea() {
				console.log('选择了喝点奶茶');
				this.getTuratableDetail(7)
			}
		},
		watch: {
			prizeList: {
				handler(newVal, oldVal) {
					// 当奖品数据变化时，强制更新转盘
					this.$nextTick(() => {
						// console.log('奖品数据已更新:', newVal)
						// 强制重新渲染转盘组件
						this.$forceUpdate()
					})
				},
				deep: true
			},
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
</style>