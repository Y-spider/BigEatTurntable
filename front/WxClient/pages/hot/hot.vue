<template>
	<view class="hot-turntable-page">
		<cu-custom :isBack="false">
		    <block slot="content">热门转盘</block>
		</cu-custom>
		<view class="hot-list">
			<view
				v-for="(item, idx) in hotList"
				:key="item.id"
				class="hot-item cu-list menu-avatar"
				style="margin-top: 5rpx !important;"
				@click="goDetail(item)"
			>
				<view class="cu-item" style="height: 80rpx;">
					<view class="content" style="display: flex;	 flex-direction: row; gap: 15rpx; align-items: center;left: 30rpx;">
						<view class="num" style="color: #FFA500;">{{ idx + 1 }}.</view>
						<view class="title">{{ item.title }}</view>
					</view>
					<view class="cuIcon-right arrow"></view>
				</view>
			</view>
			<view v-if="hotList.length === 0" class="empty-tip">暂无热门转盘</view>
		</view>
	</view>
</template>

<script>
import {getHotTurtableAPI} from "@/apis/turntableApi.js"
export default {
	data() {
		return {
			hotList: []
		}
	},
	onShow() {
		this.init()
	},
	methods: {
		async init(){
			let res = await getHotTurtableAPI()
			this.hotList = res.data
		},
		goDetail(item) {
			uni.navigateTo({
				// url: `pages/detail/detail?id=${item.id}&tableName=${item.title}`
				url:`/pages/detail/detail?id=${item.id}&tableName=${item.title}`
			})
		}
	}
}
</script>

<style scoped>
.hot-turntable-page {
	background: #f8f8f8;
	min-height: 100vh;
}
.hot-item {
	background: #fff;
	border-bottom: 1rpx solid #f0f0f0;

}

</style>
