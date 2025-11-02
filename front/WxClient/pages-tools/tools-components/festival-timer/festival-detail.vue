<template>
	<view>
		<cu-custom :isBack="true" :backUrl="backUrl" bgColor="bg-color-festival">
			<block slot="content">{{festivalInfo.name}}</block>
		</cu-custom>
		<view class="festival-detail">
			<view  @click="copyInfo(festivalInfo.calender)"  v-if="festivalInfo.calender" class="detail-section">
			  <view class="detail-title">农历</view>
			  <view class="detail-content">{{ festivalInfo.calender }}</view>
			</view>
		  <view @click="copyInfo(item.value)" v-for="(item, index) in detailList" :key="index" class="detail-section">
		    <view class="detail-title">{{ item.label }}</view>
		    <view  class="detail-content">{{ item.value }}</view>
		  </view>
		</view>
	</view>
</template>

<script>
import {
		getFestivalDesAPI
	} from "../../apis/festivalApi.js";
export default {
  data() {
    return {
      detailList: [], // 由接口或路由参数传入
	  festivalInfo:null,
	  backUrl:""
    }
  },
  onLoad(options) {
    // options.id 可用来获取对应节日详情
    this.loadFestivalDetail(options.id)
	this.backUrl = options.backUrl || ""
  },
  methods: {
	  copyInfo(content){
		uni.setClipboardData({
			data:content
		})
	  },
    async loadFestivalDetail(id) {
      // 从服务端或本地加载 JSON 数据
     const res = await getFestivalDesAPI(id);
      if(res.data){
		  this.detailList = JSON.parse(res.data.des)
	  }
	  this.festivalInfo = res.data
    }
  }
}
</script>

<style scoped>
.festival-detail {
  padding: 20rpx;
}
.detail-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 8rpx rgba(0,0,0,0.05);
}
.detail-title {
  font-weight: bold;
  font-size: 32rpx;
  margin-bottom: 12rpx;
  color: #333;
}
.detail-content {
  font-size: 28rpx;
  color: #666;
  white-space: pre-line;
}
</style>
