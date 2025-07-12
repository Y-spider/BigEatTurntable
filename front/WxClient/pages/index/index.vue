<template>
	<view>
		<cu-custom :isBack="false">
		    <block slot="content">今天吃什么好呢</block>
		</cu-custom>
		<!-- 按钮区开始 -->
		<view class="choice-bar-wrap">
			<view class="choice-bar">
				<view
					class="choice-btn"
					:class="{ active: mode === 'random' }"
					@click="mode = 'random'"
				>
				<image class="choose-icon" src="../../static/餐饮.png"></image>
				听天由命</view>
				<view
					class="choice-btn"
					:class="{ active: mode === 'nearby' }"
					@click="mode = 'nearby'"
				>
				<image class="choose-icon" src="../../static/附近餐厅.png"></image>
				附近餐饮</view>
			</view>
		</view>
		<!-- 转盘区域 -->
		<view class="turntable-box">
			<Turntable :prizeList="prizeList" style="margin: 50rpx;"></Turntable>
			<view class="fun-button" style="display: flex;justify-content: space-between; align-items: center;">
				<view class="fun-but">
					<button class="cu-btn bg-gradual-green shadow"> <text class="cuIcon-form" style="margin: 0 10rpx;"></text>菜谱</button>
				</view>
				<view class="fun-but">
					<button class="cu-btn bg-red shadow" @click="goEdit"> <text class="cuIcon-edit" style="margin: 0 10rpx;"></text> 编辑</button>
				</view>
			</view>
		</view>
		<!-- 固定类型 -->
		<view class="col-center" style="width: 100vw;">
			<view class="row-center" style="justify-content: space-around; width: 100vw;margin: 10rpx;">
				<button 
					class="cu-btn round shadow"
					:class="selectedType === 'breakfast' ? 'bg-yellow' : 'bg-gray'"
					@click="selectType('breakfast')"
				>早餐<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view></button>
				<button 
					class="cu-btn round shadow"
					:class="selectedType === 'lunch' ? 'bg-yellow' : 'bg-gray'"
					@click="selectType('lunch')"
				>午餐<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view></button>
				<button 
					class="cu-btn round shadow"
					:class="selectedType === 'dinner' ? 'bg-yellow' : 'bg-gray'"
					@click="selectType('dinner')"
				>晚餐<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view></button>
			</view>
			<view class="row-center" style="width: 60vw;margin: 10rpx;gap: 20rpx;">
				<button 
					class="cu-btn round shadow"
					:class="selectedType === 'midnight' ? 'bg-yellow' : 'bg-gray'"
					@click="selectType('midnight')"
				>宵夜<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view></button>
				<button 
					class="cu-btn round shadow"
					:class="selectedType === 'diet' ? 'bg-yellow' : 'bg-gray'"
					@click="selectType('diet')"
				>减脂餐<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view></button>
			</view>
			<view class="row-center" style="width: 100vw;margin: 10rpx; justify-content: space-evenly;">
				<button 
					class="cu-btn round shadow"
					:class="selectedType === 'random' ? 'bg-yellow' : 'bg-gray'"
					@click="selectType('random')"
				>随机吃点<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view></button>
				<button 
					class="cu-btn round shadow"
					:class="selectedType === 'milkTea' ? 'bg-yellow' : 'bg-gray'"
					@click="selectType('milkTea')"
				>喝点奶茶<view class='cu-tag sm bg-red radius' style="margin: 0rpx 15rpx;">系统</view></button>
			</view>
		</view>
		<!-- 自定义菜单区域 -->
		<view class="custom-select-area">
			<view class="custom-tip">
				自定义菜单
			</view>
			<view class="custom-btn-list">
				<button
					v-for="item in customTypes"
					:key="item.value"
					class="cu-btn round shadow custom-btn"
					:class="selectedType === item.value ? 'bg-yellow' : 'bg-gray'"
					@click="selectType(item.value)"
				>
					{{ item.label }}
					<view class='cu-tag sm bg-orange radius' style="margin-left: 10rpx;">自定义</view>
				</button>
			</view>
		</view>
	</view>
</template>

<script>
	import Turntable from "@/components/Turntable.vue"
	export default {
		components:{
			Turntable
		},
		data() {
			return {
				mode: 'random',
				selectedType: 'breakfast', // 当前选中的固定类型，默认选中早餐菜单
				prizeList: [
					{ fonts: [{ text: '炒饭', top: '10%' }], background: '#e9e8fe' },
					{ fonts: [{ text: '炒面', top: '10%' }], background: '#b8c5f2' },
					{ fonts: [{ text: '胡萝卜', top: '10%' }], background: '#e9e8fe' },
					{ fonts: [{ text: '不见得', top: '10%' }], background: '#b8c5f2' },
					{ fonts: [{ text: '哈哈哈哈', top: '10%' }], background: '#e9e8fe' },
					{ fonts: [{ text: '真好', top: '10%' }], background: '#b8c5f2' }
				],
				customTypes: [
					{ label: '早餐', value: 'custom1' },
					{ label: '午餐', value: 'custom2' },
					{ label: '晚餐', value: 'custom1' },
					{ label: '夜宵', value: 'custom2' }
				]
			}
		},
		methods: {
			goEdit() {
				uni.setStorageSync('editPrizeList', this.prizeList)
				uni.navigateTo({ url: `/pages/editWheel/index?turntableName=早餐` })
			},
			selectType(type) {
				this.selectedType = type;
				// 根据不同类型执行不同函数
				switch(type) {
					case 'breakfast':
						this.handleBreakfast();
						break;
					case 'lunch':
						this.handleLunch();
						break;
					case 'dinner':
						this.handleDinner();
						break;
					case 'midnight':
						this.handleMidnight();
						break;
					case 'diet':
						this.handleDiet();
						break;
					case 'random':
						this.handleRandom();
						break;
					case 'milkTea':
						this.handleMilkTea();
						break;
				}
			},
			// 早餐菜单处理函数
			handleBreakfast() {
				console.log('选择了早餐菜单');
				// 这里可以添加你的逻辑，比如跳转页面、显示数据等
			},
			// 午餐菜单处理函数
			handleLunch() {
				console.log('选择了午餐菜单');
			},
			// 晚餐菜单处理函数
			handleDinner() {
				console.log('选择了晚餐菜单');
			},
			// 宵夜加餐处理函数
			handleMidnight() {
				console.log('选择了宵夜加餐');
			},
			// 减脂菜谱处理函数
			handleDiet() {
				console.log('选择了减脂菜谱');
			},
			// 随机吃处理函数
			handleRandom() {
				console.log('选择了随机吃');
			},
			// 喝点奶茶处理函数
			handleMilkTea() {
				console.log('选择了喝点奶茶');
			}
		},
		onShow() {
			const list = uni.getStorageSync('editPrizeList')
			if (list && Array.isArray(list)) {
				this.prizeList = list
			}
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
	box-shadow: 0 2px 8px rgba(255,165,0,0.08);
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
.choose-icon{
	display: flex;
	justify-content: center;
	align-items: center;
	margin: 0 5rpx;
	width: 50rpx;
	height: 50rpx;
}
.turntable-box{
	background-color: #FFA500;
}
.fun-but{
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
