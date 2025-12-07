<!-- 具体搜索展示页面 -->
<template>
	<view>
		<cu-custom :isBack="true">
			<block slot="content">{{bookName}}</block>
		</cu-custom>
		<view class="summary-box">
			<view class="summary-item">
				<text>支出：</text>
				<text class="value">{{showInfo.outTotal}}￥</text>
			</view>
			<view class="summary-item">
				<text>收入：</text>
				<text class="value">{{showInfo.inTotal}}￥</text>
			</view>
			<view class="summary-item">
				<text>结余：</text>
				<text class="value">{{showInfo.surPlus}}￥</text>
			</view>
			<view class="summary-item">
				<text>记账笔数：</text>
				<text class="value">{{showInfo.makeCount}}笔</text>
			</view>
		</view>

		<view class="record-box">
			<view v-if="billRecordList.length > 0 " class="day-swiper-box"
				style="width: 100vw; ">
				<view class="record-card" v-for="(records,outerIndex) in billRecordList" :key="outerIndex"
					:title="records.date + records.week">
					<view class="card-head">
						<view>{{records.date }} {{records.week}} </view>
						<view class="extra" style="color: #b9b9b9; font-size: small; font-weight: bold;">{{handleExtra(records)}}</view>
					</view>
					<view class="card-body">
						<view style="display: flex; flex-direction: column;">
							<view @click="showDetaile(record.id)" class="record-item"
								v-for="(record,index) in itemRecordList[outerIndex]" :key="record.id">
								<view class="first-line">
									<view class="item-left">
										<!-- 消费类型ICON -->
										<view v-if="record.type == 'in'">
											<image :src="record.labelUrl" style="padding: 10rpx;"
												class="cu-avatar round bg-yellow">
											</image>
										</view>
										<view v-if="record.type == 'out'">
											<image :src="record.labelUrl" style="padding: 10rpx;"
												class="cu-avatar round bg-green">
											</image>
										</view>
										<!-- 消费类描述 -->
										<view>{{record.des}}</view>
										<!-- 消费用户名称 (共享账单才显示)-->
									</view>

									<view class="item-right flex justify-between align-center" style="font-weight: bold;flex: 1; gap:30rpx; border-bottom: 1rpx solid #c8c8c8;">
										<view class="item-right-r">
											<view>
												<text style="font-weight: bold; padding: 0 10rpx;">{{record.labelName}}</text>
												<text style="color: #ffa500;"> ({{record.name}}) </text>
											</view>
											<view>
												<view class="remark"
													style=" color: #8b8b8b; font-size: small;  padding: 5rpx 10rpx;">
													备注：{{record.remark || '无'}}</view>
											</view>
										</view>
										<view class="item-left-r flex align-center" style="gap: 10rpx;">
											<view :style="record.type === 'in' ? 'color:#ffa500;' : 'color:black;'">
												<text v-if="record.type==='out'">-</text>
												￥{{record.amount}}
											</view>
											<view>
												<image class="cu-avatar sm" style="background-color: white;"
													:src="record.methodUrl"></image>
											</view>
										</view>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>
				<!-- 底部提示 -->
				<view v-if="billRecordList.length > 0" class="foot-box"
					style="display: flex; padding: 30rpx; justify-content: center; align-items: center;">
					已经到底部了~~~
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getSearchListAPI
	} from '../../apis/billApi';
	export default {
		data() {
			return {
				bookName: "账单",
				searchParams: {},
				showInfo: {},
				billRecordList: [],
				bookName: "",
				itemRecordList: [], // 二层消费记录列表
			}
		},
		onLoad(option) {
			this.searchParams = JSON.parse(option.paramObj);
			this.bookName = option.bookName || "搜索结果";
		},
		onShow(){
			this.init();
		},
		methods: {
			showDetaile(id) {
				uni.navigateTo({
					url: `/pages-book/bill-record-detail/bill-record-detail?id=${id}&bookName=${this.bookName}`
				})
			},
			handleExtra(dateRecord) {
				let res = "";
				if (dateRecord.inAmount) {
					res += `收入:` + dateRecord.inAmount + " "
				}
				if (dateRecord.outAmount) {
					res += `支出:` + dateRecord.outAmount + " "
				}
				if (dateRecord.inAmount && dateRecord.outAmount) {
					res += `结余:` + (dateRecord.inAmount - dateRecord.outAmount).toFixed(2)
				}
				return res;
			},
			async init() {
				this.billRecordList = [];
				this.itemRecordList = [];
			    const res = await getSearchListAPI(this.searchParams);
			    if (!res) return;
			    // 深拷贝
			    const data = JSON.parse(JSON.stringify(res.data));
			    // 复制给 showInfo（包括 totals）
			    this.showInfo = res.data;
			    // 删除 totals
			    delete data.inTotal;
			    delete data.outTotal;
			    delete data.surPlus;
				delete data.makeCount;
			
			    // 作为记录列表
				for (let key in data) {
					this.billRecordList.push(data[key]);
					this.itemRecordList.push(data[key].recordList);
				}
			}

		}
	}
</script>

<style scoped>
/* 顶部汇总卡片 */
.summary-box {
    background-color: #ffa500;
    padding: 40rpx 30rpx;
    border-radius: 0 0 32rpx 32rpx;
    color: #fff;
    display: flex;
    flex-direction: column;
    gap: 24rpx;
    box-shadow: 0 12rpx 24rpx rgba(0, 0, 0, 0.1);
}

.summary-item {
    display: flex;
    justify-content: space-between;
    font-size: 30rpx;
    letter-spacing: 1rpx;
}

.summary-item .value {
    font-size: 38rpx;
    font-weight: 600;
}

/* 外层记录容器 */
.record-box {
    margin-top: 20rpx;
}

/* 每个日期的大卡片 */
.record-card {
    background: #ffffff;
    border-radius: 20rpx;
    margin-bottom: 30rpx;
    padding-bottom: 10rpx;
    overflow: hidden;
    box-shadow: 0 10rpx 20rpx rgba(0,0,0,0.06);
}

/* 日期头部 */
.card-head {
    display: flex;
    padding: 26rpx 20rpx;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #f0f0f0;
    font-size: 30rpx;
    font-weight: 500;
}

/* 主体内容 */
.card-body {
    width: 100%;
    padding: 10rpx 20rpx;
}

/* 单条消费记录 */
.record-item {
    display: flex;
    flex-direction: column;
    padding: 20rpx 0;
    gap: 10rpx;
    border-bottom: 1px solid #f5f5f5;
}

.record-item:last-child {
    border-bottom: none;
}

/* 第一行 */
.first-line {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.item-left {
    display: flex;
    align-items: center;
    gap: 15rpx;
}

.item-right {
    display: flex;
    align-items: center;
    gap: 15rpx;
    font-weight: bold;
}

/* 第二行备注 */
.second-line {
    display: flex;
    justify-content: space-between;
    padding: 10rpx 5rpx;
    color: #ffa500;
}

.remark {
    font-size: 24rpx;
    border-radius: 10rpx;
    color: #8b8b8b;
}

/* 底部提示 */
.foot-box {
    text-align: center;
    padding: 40rpx 0;
    color: #a5a5a5;
    font-size: 28rpx;
}

</style>