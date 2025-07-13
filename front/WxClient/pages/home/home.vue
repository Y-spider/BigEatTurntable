<template>
  <view class="home-page">
    <cu-custom :isBack="false">
      <block slot="content">吃货大转盘</block>
    </cu-custom>
    <!-- 固定头部区域 -->
    <view class="fixed-header">
      <view class="user-info-box animate__animated animate__fadeInDown">
        <view class="user-info">
          <view class="nickname animate__animated animate__fadeInLeft">吃货{{ userName }}
          </view>
          <view class="stat animate__animated animate__fadeInRight">
            <text>创建轮盘：<text class="stat-num">{{ createCount }}</text></text>
            <text style="margin-left: 32rpx;">转动次数：<text class="stat-num">{{ spinCount }}</text></text>
          </view>
        </view>
      </view>
      <view class="switch-bar animate__animated animate__fadeInUp">
        <button class="cu-btn" :class="{ 'bg-orange': showType === 'create' }" @click="showType = 'create'">我创建的轮盘</button>
        <button class="cu-btn" :class="{ 'bg-orange': showType === 'record' }" @click="showType = 'record'">转动记录</button>
      </view>
    </view>
    <!-- 滚动内容区域 -->
    <scroll-view
      class="scroll-list-area"
      scroll-y
      @scrolltolower="onScrollToLower"
    >
      <view v-if="showType === 'create'" class="list-area animate__animated animate__fadeIn">
        <view  @click="goTurntable(item)" v-for="item in myTurntables" :key="item.id" class="row-between list-item animate__animated animate__fadeInUp">
         <view>
			 <view class="title">{{ item.title }}
			 				<view style="margin: 0 10rpx;" class="cu-tag radius sm bg-red">{{item.type==0?"自定义":(item.type==1?"系统":"热门")}}</view>
			  </view>
			 <view class="desc">创建时间：{{ item.createTime }}</view>
		 </view>
		 <view class="arrow">
			 <view class="cuIcon-right arrow"></view>
		 </view>
        </view>
        <view v-if="myTurntables.length === 0" class="empty-tip">暂无创建的轮盘</view>
      </view>
      <view v-else class="list-area animate__animated animate__fadeIn">
        <view v-for="rec in RotationRecords" :key="rec.id" class="list-item animate__animated animate__fadeInUp">
          <view class="title">{{ rec.turntableName }}</view>
          <view class="desc">转动时间：{{ rec.createTime }}</view>
          <view class="desc">结果：{{ rec.result }}</view>
        </view>
        <view v-if="spinLoading" class="empty-tip">加载中...</view>
        <view v-if="spinFinished && RotationRecords.length" class="empty-tip">没有更多了</view>
        <view v-if="!spinLoading && RotationRecords.length === 0" class="empty-tip">暂无转动记录</view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import {listWithPageAPI,selectRecordCountAPI} from "@/apis/rotationRecordApi.js"
import {getUserTurntableInfoAPI} from "@/apis/turntableApi.js"
export default {
  data() {
    return {
      userName:"",
      createCount: 0,
      spinCount: 0,
      showType: 'create',
      myTurntables: [], 
      RotationRecords: [],
      spinPage: 1,
      spinPageSize: 10,
      spinTotal: 0,
      spinLoading: false,
      spinFinished: false
    }
  },
  created(){
	  this.userName = uni.getStorageSync("userName")
  },
  onShow() {
    this.loadSpinRecords(true)
	this.init()
  },
  methods: {
	// 前往转盘
	goTurntable(turntable){
	  uni.navigateTo({
	  	url:`/pages/detail/detail?id=${turntable.id}&tableName=${turntable.title}`
	  })
	},
    // 模拟分页API
    async init() {
      let createRes = await getUserTurntableInfoAPI()
	  this.createCount = createRes.data.length
	  this.myTurntables = createRes.data
	  let recordCountRes = await selectRecordCountAPI()
	  this.spinCount = recordCountRes.data
	},
    // 加载转动记录
    async loadSpinRecords(reset = false) {
      if (this.spinLoading || this.spinFinished) return
      this.spinLoading = true
      if (reset) {
        this.spinPage = 1
        this.RotationRecords = []
        this.spinFinished = false
      }
      const res = await listWithPageAPI(this.spinPage, this.spinPageSize)
      this.RotationRecords = [...this.RotationRecords,...res.data.records]
      this.spinTotal = res.data.total
      if (this.RotationRecords.length >= this.spinTotal) {
        this.spinFinished = true
      } else {
        this.spinPage++
      }
      this.spinLoading = false
    },
    // 触底加载更多
    onScrollToLower() {
      if (this.showType === 'record') {
        this.loadSpinRecords()
      }
    }
  },
  watch: {
    showType(val) {
      if (val === 'record') {
		this.spinFinished = false
        this.loadSpinRecords(true)
      }
    }
  }
}
</script>

<style scoped>
.home-page {
  background: #f8f8f8;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  height: 100vh;
}
.fixed-header {
  position: sticky;
  top: 0;
  z-index: 10;
  background: #f8f8f8;
}
.user-info-box {
  background: #fff;
  border-radius: 16rpx;
  margin: 24rpx 24rpx 0 24rpx;
  padding: 24rpx;
  box-shadow: 0 2px 8px rgba(255,165,0,0.06);
}
.user-info {
  flex: 1;
}
.nickname {
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 8rpx;
  display: flex;
  align-items: center;
}
.fixed-tag {
  background: #FFA500;
  color: #fff;
  font-size: 22rpx;
  border-radius: 8rpx;
  padding: 2rpx 10rpx;
  margin-left: 12rpx;
}
.stat {
  font-size: 28rpx;
  color: #FFA500;
}
.stat-num {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff6600;
  margin: 0 4rpx;
  display: inline-block;
  animation: bounceIn 1s;
}
.switch-bar {
  display: flex;
  justify-content: center;
  margin: 32rpx 0 0 0;
}
.cu-btn {
  margin: 0 12rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
}
.bg-orange {
  background: #FFA500 !important;
  color: #fff !important;
}
.scroll-list-area {
  flex: 1;
  height: 0; /* 让flex:1生效 */
  overflow: hidden;
}
.list-area {
  margin: 24rpx;
}
.list-item {
  background: #fff;
  border-radius: 12rpx;
  margin-bottom: 18rpx;
  padding: 24rpx;
  box-shadow: 0 2px 8px rgba(255,165,0,0.04);
  transition: box-shadow 0.3s;
}
.list-item:active {
  box-shadow: 0 4px 16px rgba(255,165,0,0.12);
}
.title {
  font-size: 32rpx;
  color: #333;
  font-weight: 500;
}
.desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 6rpx;
}
.empty-tip {
  text-align: center;
  color: #ccc;
  font-size: 28rpx;
  margin: 48rpx 0;
}
</style>
