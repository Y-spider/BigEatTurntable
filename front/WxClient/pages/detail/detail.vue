<template>
	<view>
		<cu-custom :isBack="true">
		    <block slot="backText">返回</block>
		    <block slot="content">{{tableName}}</block>
		</cu-custom>
		<!--  转盘详情页 -->
		<view class="turntable-box">
			<Turntable :turntable="turntableInfo" @routeDone="getRouteResult" :prizeList="prizeList" style="margin: 50rpx;"></Turntable>
			<view class="fun-button" style="display: flex;justify-content: space-between; align-items: center;">
				<view class="fun-but">
					<button v-if="prize.isMaker" class="cu-btn bg-gradual-green shadow"> <text class="cuIcon-form" style="margin: 0 10rpx;"></text>菜谱</button>
				</view>
				<view class="fun-but">
					<button class="cu-btn bg-red shadow" @click="goEdit"> <text class="cuIcon-edit" style="margin: 0 10rpx;"></text> 编辑</button>
				</view>
			</view>
		</view>
		<!-- 广告区域 -->
	</view>
</template>

<script>
import Turntable from "@/components/Turntable.vue"
import {getTurntableDetailAPI} from "@/apis/turntableApi.js"
	export default {
		components:{
			Turntable
		},
		data() {
			return {
				tableName:"",
				id:null,
				prizeList:[],
				prize:null, // 奖品
				turntableInfo:{}
			}
		},
		onShow(){
			this.init()
		},
		methods: {
			goEdit() {
				let isSystem = true
				uni.setStorageSync('editPrizeList', this.prizeList)
				uni.navigateTo({ url: `/pages/editWheel/index?turntableName=${this.tableName}&id=${this.id}&isSystem=${isSystem}`
				})
			},
			getRouteResult(prize){
				this.prize = prize
			},
			async init(){
				let res = await getTurntableDetailAPI(this.id)
				this.turntableInfo = res.data
				this.prizeList = JSON.parse(res.data.content)
			}
		},
		onLoad(option) {
			this.id = parseInt(option.id)
			this.tableName = option.tableName
		}
		
	}
</script>

<style>

</style>
