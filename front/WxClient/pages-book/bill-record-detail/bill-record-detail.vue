<template>
	<view class="detail-page"> <!-- 顶部导航 -->
		<cu-custom :isBack="true">
			<block slot="content">记账</block>
		</cu-custom> <!-- 内容卡片 -->
		<view class="card"> <!-- 标签图标 + 名称 -->
			<view class="label-box">
				<image
					:class="detail.type == 'in'?'cu-avatar round bg-yellow label-icon' : 'cu-avatar round bg-green label-icon'"
					:src="detail.labelUrl"></image> <text class="label-name">{{ detail.labelName }}</text>
			</view> <!-- 金额 -->
			<view class="amount" :class="detail.type === 'out' ? 'amount-out' : 'amount-in' ">
				{{ detail.type === 'out' ? '-' : '+' }}¥{{ detail.amount }}
			</view> <!-- 信息内容列表 -->
			<view class="info-line"> <text class="title">所属账本</text> <text class="value">{{bookName }}</text>
			</view>
			<view class="info-line"> <text class="title">类型</text> <text
					class="value">{{ detail.type === 'out' ? '支出' : '收入' }}</text> </view>
			<view class="info-line"> <text class="title">资产账户</text>
				<view>
					<image :src="detail.methodUrl" class="cu-avatar sm" style="background-color: white;"></image>
					<text class="value">{{ detail.methodLabel }}</text>
				</view>
			</view>
			<view class="info-line"> <text class="title">记账人</text> <text class="value">{{ detail.name }}</text>
			</view>
			<view class="info-line"> <text class="title">日期</text> <text class="value">{{ detail.recordTime }}</text>
			</view>
			<view class="info-line"> <text class="title">备注</text> <text class="value">{{ detail.remark || '无' }}</text>
			</view> <!-- 图片附件：横向滑动 --> <scroll-view scroll-x class="attachment-box">
				<view class="attachment-inner">
					<image v-for="(img, idx) in attachmentList" :key="idx" :src="img" class="attachment-img"
						mode="aspectFill" @click="previewImage" />
				</view>
			</scroll-view>
		</view> <!-- 底部按钮 -->
		<view class="bottom-btns"> <button class="edit-btn" @click="editBill">编辑</button> <button class="delete-btn"
				@click="deleteBill">删除</button> </view> <!-- 编辑弹框 --> <make-bill-pop ref="makeBillPop"
			@onConfirm="handleConfirm"></make-bill-pop>
	</view>
</template>
<script>
	import {
		getBillRecordAPI,
		deleteBillRecordAPI
	} from "@/apis/billApi.js";
	import makeBillPop from "../../components/make-bill-pop.vue";
	export default {
		components: {
			makeBillPop
		},
		data() {
			return {
				id: null,
				bookName: "默认账本",
				detail: {}, // 单条账单详情 
				attachmentList: [], // 图片数组 
			};
		},
		onLoad(option) {
			this.id = option.id;
			this.bookName = option.bookName;
			this.init(this.id);
		},
		methods: {
			previewImage() {
				uni.previewImage({
					urls: this.attachmentList
				})
			},
			handleConfirm() {
				this.init(this.id);
			}, // 1. 加载账单详情 
			async init(id) {
				const res = await getBillRecordAPI(id);
				if (!res) return;
				this.detail = res.data;
				this.attachmentList = res.data.attachmentUrl ? res.data.attachmentUrl.split(",") : [];
			}, // 2. 编辑 
			editBill() {
				if (!this.detail.isEdit) {
					uni.showToast({
						icon: "error",
						title: "无权限",
						duration: 2000
					})
					return;
				}
				this.$refs.makeBillPop.open(this.id);
			}, // 3. 删除 
			deleteBill() {
				if (!this.detail.isEdit) {
					uni.showToast({
						icon: "error",
						title: "无权限",
						duration: 2000
					})
					return;
				}
				uni.showModal({
					title: '提示',
					content: '确定要删除该账单吗？删除后无法恢复！！!',
					success: async (res) => {
						if (res.confirm) {
							const resDelete = await deleteBillRecordAPI(this.id);
							if (!resDelete) return;
							uni.navigateBack();
						} else {
							// 取消
						}
					}
				});
			}
		}
	};
</script>
<style lang="scss">
	.detail-page {
		padding-bottom: 120rpx;
	}

	.card {
		margin: 30rpx;
		padding: 30rpx;
		background: #fff;
		border-radius: 20rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
	}

	/* 标签图标名称 */
	.label-box {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 30rpx;
	}

	.label-icon {
		width: 80rpx;
		height: 80rpx;
		padding: 10rpx;
	}

	.label-name {
		margin-top: 10rpx;
		font-size: 30rpx;
	}

	/* 金额 */
	.amount {
		text-align: center;
		font-size: 54rpx;
		margin-bottom: 20rpx;
	}

	.amount-out {
		color: #d9534f;
	}

	.amount-in {
		color: #ffa500;
	}

	/* 信息行 */
	.info-line {
		display: flex;
		justify-content: space-between;
		margin: 20rpx 0;
		font-size: 30rpx;
	}

	.title {
		color: #777;
		width: 180rpx;
		font-weight: bolder;
	}

	.attachment-box {
		white-space: nowrap;
		width: 100%;
		padding: 10rpx 0;
	}

	.attachment-inner {
		display: inline-flex;
		gap: 20rpx;
	}

	.attachment-img {
		width: 160rpx;
		/* 统一宽度 */
		height: 160rpx;
		/* 统一高度 */
		border-radius: 12rpx;
		background: #f2f2f2;
		object-fit: cover;
		/* 关键：裁剪成统一尺寸 */
	}

	/* 按钮组 */
	.bottom-btns {
		display: flex;
		justify-content: space-around;
		margin-top: 50rpx;
		bottom: 0;
		width: 100%;
		padding: 20rpx 0;
	}

	.edit-btn {
		width: 40%;
		background-color: white;
		color: #ffa500;
		border: 2rpx solid #ffa500;
	}

	.delete-btn {
		width: 40%;
		background-color: white;
		border: 2rpx solid #f00000;
		color: #f00000;
	}
</style>