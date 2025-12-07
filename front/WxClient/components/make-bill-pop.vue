<!-- 记一笔账单修改底部弹出组件 -->
<template>
	<view>
		<view class="cu-modal bottom-modal" :class="modalName=='bottomModal'?'show':''">
			<view class="cu-dialog" style="font-weight: bold;">
				<view class="cu-bar bg-white"
					style="display: flex; justify-content: space-between; align-items: center;">
					<view
						style="width: 100vw; display: flex;justify-content: center;align-items: center; color: #ffa500;">
						记一笔</view>
					<view @click="handleClose" class="cuIcon-close xl"
						style="display: flex; justify-content: center;align-items: center; width: 10%; margin-right: 5rpx; position: absolute; right: 5rpx;">
					</view>
				</view>
				<view class="padding">
					<view class="bill-head" style="display: flex; justify-content: space-between;">
						<!-- 头部信息 -->
						<view style="display: flex; align-items: center; gap: 10rpx;">
							<view :class="billRecord.type === 'out' ? 'type-active' : 'type-normal'"
								@click="billRecord.type = 'out'">支出</view>
							<view :class="billRecord.type === 'in' ? 'type-active' : 'type-normal'"
								@click="billRecord.type = 'in'">收入</view>
						</view>
						<view>
							<!-- 时间选择器 -->
							<picker mode="date" :value="billRecord.recordTime" start="2015-09-01" end="2099-12-30"
								@change="dateChange">
								<view>
									<view
										style=" border-radius: 15rpx; display: flex; padding: 16rpx; background-color: #ebebeb;">
										{{transformDate(billRecord.recordTime)}}▼
									</view>
								</view>
							</picker>

						</view>
					</view>
					<!-- 金额显示 -->
					<view style="display: flex; padding: 15rpx; width: 100%; border-bottom: 2rpx solid #ffa500;">
						<view style="font-size: 50rpx; font-weight: bolder;">金额：{{billRecord.amount}}</view>
					</view>
					<!-- 图标区域 -->
					<view class="icon-box"
						style="width: 100%; display: flex; gap: 5rpx;min-height: 300rpx; max-height: 300rpx; overflow-y: scroll; flex-wrap: wrap;">
						<view @click="changeBillType(item)" class="icon-item"
							style="font-size: small; display: flex; padding: 15rpx; flex-direction: column;gap: 5rpx;"
							v-for="(item,index) in iconUrlList" :key="index">
							<image style="padding: 10rpx;"
								:class="chooseType==item.id ? 'cu-avatar round  type-icon-active' : 'cu-avatar round '"
								:src="item.url"></image>
							<view :class="chooseType==item.id ? 'type-label-active' : ''">{{item.label}}</view>
						</view>
					</view>
					<!-- 来源展示 -->
					<scroll-view scroll-x class="resource-scroll">
						<view
							:style="chooseMthodId==item.id?'background-color: #ffa500; color:white;':'background-color: white;'"
							@click="handleChooseMethod(item)" v-for="(item,index) in methodList" :key="index"
							class="resource-item">
							<view class="cu-avatar"
								:style="chooseMthodId==item.id?'background-color: #ffa500;':'background-color: white;'">
								<image :src="item.url" class="cu-avatar sm"
									:style="chooseMthodId==item.id?'background-color: #ffa500;':'background-color: white;'">
								</image>
							</view>
							<view class="label" style="flex: 1;">{{ item.label }}</view>
						</view>
					</scroll-view>

					<!-- 备注区域 -->
					<view class="remark-box">
						<view class="cu-form-group" style="display: flex;">
							<view class="title" style="font-weight: bold;">备注：</view>
							<input style="display: inline;" v-model="billRecord.remark" placeholder="点击输入备注信息" name="input"
								:maxlength="30"></input>
						</view>
					</view>
					<!-- 图片上传区域 -->
					<view class="cu-form-group" style="display: flex; align-items: center;">
						<view class="title" style="font-weight: bold; width: 220rpx;">
							图片上传({{imgList.length}}/{{uploadMax}})</view>
						<view class="grid col-4 grid-square flex-sub">
							<view class="bg-img" v-for="(item,index) in imgList" :key="index" @tap="ViewImage"
								:data-url="imgList[index]">
								<image :src="imgList[index]" style="width: 128rpx; height: 128rpx; position: relative;">
								</image>
								<view class="cu-tag bg-red" @tap.stop="DelImg" :data-index="index">
									<text class='cuIcon-close'></text>
								</view>
							</view>
							<view class="solids" @tap="ChooseImage" v-if="imgList.length < uploadMax">
								<text class='cuIcon-cameraadd'></text>
							</view>
						</view>
					</view>
					<pan-keyboard ref="keyboard" :isDecimal="true" :submitButtonText="'确认'" :toFixed="2" :isCheck="true"
						@onChange="handleInputChange" @onSubmit="handleSubmit" @onError="handleError" />
				</view>
			</view>

		</view>
	</view>
</template>

<script>
	import {
		getBillRecordAPI,
		addBillRecordAPI,
		updateBillRecordAPI
	} from "@/apis/billApi.js";
	import {
		getUserInfoAPI
	} from "@/apis/userApi.js";
	import panKeyboard from "./pan-keyboard/pan-keyboard.vue";
	export default {
		components: {
			panKeyboard
		},
		data() {
			return {
				chooseMthodId: 1,
				uploadMax: 1,
				index: -1,
				imgList: [],
				chooseType: 1,
				show: "",
				modalName: "",
				billRecord: {
					type: "out",
					amount: 0,
					recordTime: "",
					remark: "",
					labelName: "",
					labelUrl: "",
					attachmentUrl: [],
					methodLabel: "",
					methodUrl: ""
				},
				methodList: [{
						id: 1,
						label: "微信支付",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/微信支付.svg"
					},
					{
						id: 2,
						label: "支付宝",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/支付宝.svg"
					},
					{
						id: 3,
						label: "花呗",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/花呗.svg"
					},
					{
						id: 4,
						label: "现金",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/现金.svg"
					},
					{
						id: 5,
						label: "京东白条",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/京东白条.svg"
					},
					{
						id: 6,
						label: "储蓄卡",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/储蓄卡.svg"
					},
					{
						id: 7,
						label: "信用卡",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/信用卡.svg"
					},
					{
						id: 8,
						label: "其他",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/其他方式.svg"
					}
				],
				iconUrlList: [], // 展示icon数组
				iconUrlListOfIn: [{
						id: 1,
						label: "工资",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/工资.svg"
					},
					{
						id: 2,
						label: "理财",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/理财.svg"
					},
					{
						id: 3,
						label: "退款",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/退款.svg"
					},
					{
						id: 4,
						label: "奖金",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/奖金.svg"
					},
					{
						id: 5,
						label: "礼金",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/礼金.svg"
					},
					{
						id: 6,
						label: "兼职",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/兼职.svg"
					},
					{
						id: 7,
						label: "生活费",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/生活费.svg"
					},
					{
						id: 8,
						label: "生意",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/生意.svg"
					}
				],
				iconUrlListOfOut: [{
						id: 1,
						label: "餐饮",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/餐饮.svg"
					},
					{
						id: 2,
						label: "交通",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/交通.svg"
					},
					{
						id: 3,
						label: "蔬菜",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/蔬菜.svg"
					},
					{
						id: 4,
						label: "服饰",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/服饰.svg"
					},
					{
						id: 5,
						label: "购物",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/购物.svg"
					},
					{
						id: 6,
						label: "娱乐",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/娱乐.svg"
					},
					{
						id: 7,
						label: "运动",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/运动.svg"
					},
					{
						id: 8,
						label: "宠物",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/宠物.svg"
					},
					{
						id: 9,
						label: "快递",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/快递.svg"
					},
					{
						id: 10,
						label: "烟酒",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/烟酒.svg"
					},
					{
						id: 11,
						label: "数码",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/数码.svg"
					},
					{
						id: 12,
						label: "保险",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/保险.svg"
					},
					{
						id: 13,
						label: "其他",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/其他.svg"
					},
					{
						id: 14,
						label: "发红包",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/发红包.svg"
					},
					{
						id: 15,
						label: "旅行",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/旅行.svg"
					},
					{
						id: 16,
						label: "住房",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/住房.svg"
					},
					{
						id: 17,
						label: "家电",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/家电.svg"
					},
					{
						id: 18,
						label: "水果",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/水果.svg"
					},
					{
						id: 19,
						label: "学习",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/学习.svg"
					},
					{
						id: 20,
						label: "医疗",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/药品.svg"
					},
					{
						id: 21,
						label: "缴费",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/生活缴费.svg"
					},
					{
						id: 22,
						label: "转账",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/转账.svg"
					},
					{
						id: 23,
						label: "礼物",
						url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/礼物.svg"
					},
					// {
					// 	id: 24,
					// 	label: "新增",
					// 	url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/新增.svg"
					// }
				]
			}
		},
		watch: {
			"billRecord.type": {
				handler(newVal, oldVal) {
					this.iconUrlList = (newVal === 'in' ?
						this.iconUrlListOfIn :
						this.iconUrlListOfOut);
					this.chooseType = this.iconUrlList[0].id;
					this.billRecord.labelName = this.iconUrlList[0].label;
					this.billRecord.labelUrl = this.iconUrlList[0].url;
				},
				immediate: true
			}
		},

		methods: {
			handleChooseMethod(item) {
				this.chooseMthodId = item.id;
			},
			handleClose() {
				this.imgList = [];
				this.modalName = '';
			},
			ChooseImage() {
				uni.chooseImage({
					count: this.uploadMax, //默认9
					sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album', 'camera '], //从相册选择
					success: (res) => {
						for (let i = 0; i < res.tempFilePaths.length; i++) {
							this.imgList.push(res.tempFilePaths[i]);
							if (this.imgList.length == this.uploadMax) {
								return;
							}
						}
					}
				});
			},
			ViewImage(e) {
				uni.previewImage({
					urls: this.imgList,
					current: e.currentTarget.dataset.url
				});
			},
			DelImg(e) {
				this.imgList.splice(e.currentTarget.dataset.index, 1)
			},
			changeBillType(item) {
				this.chooseType = item.id;
				this.billRecord.labelName = item.label;
				this.billRecord.labelUrl = item.url;
			},
			transformDate(dateStr) {
				const splitList = dateStr.split("-");
				return `${splitList[0]}年${splitList[1]}月${splitList[2]}日`
			},
			dateChange(e) {
				this.billRecord.recordTime = e.detail.value;
			},
			async open(recordId,aiRecord) {
				if (recordId) {
					const res = await getBillRecordAPI(recordId);
					if (!res) return;
					this.billRecord = res.data;
					this.imgList = this.billRecord.attachmentUrl ? this.billRecord.attachmentUrl.split(",") : [];
					this.$refs.keyboard.setKeyboard(this.billRecord.amount)
				} else {
					const date = new Date();
					const y = date.getFullYear();
					const m = String(date.getMonth() + 1).padStart(2, '0'); // 月份补零
					const d = String(date.getDate()).padStart(2, '0'); // 日补零
					this.billRecord = {
						type: "out",
						amount: 0,
						recordTime: "",
						remark: "",
						labelName: "",
						labelUrl: "",
						attachmentUrl: [],
						methodLabel: "",
						methodUrl: ""
					}
					if(aiRecord){
						this.billRecord = aiRecord;
						// 保底选择支付方式
						const methodItem = this.methodList.find(item => item.label === this.billRecord.methodLabel)
						this.chooseMthodId = methodItem?.id || this.methodList[0]?.id || 1
						this.billRecord.methodLabel = methodItem?.label || this.methodList[0]?.label || "微信支付"
						
						// 保底选择类别
						const typeItem = this.iconUrlList.find(item => item.label === this.billRecord.labelName)
						this.chooseType = typeItem?.id || this.iconUrlList[0]?.id || 1
						this.billRecord.labelName = typeItem?.label || this.iconUrlList[0]?.label || (this.billRecord.type === "out" ? "餐饮" : "工资")
						this.billRecord.labelUrl = typeItem?.url || this.iconUrlList[0]?.url || ""
						
						// 保底键盘显示金额
						if(this.billRecord.amount){
							this.$refs.keyboard?.setKeyboard(this.billRecord.amount)
						}else{
							this.billRecord.amount = 0;
						}
						}
					this.billRecord.recordTime = `${y}-${m}-${d}`;
				}
				this.modalName = "bottomModal"
				this.billRecord.labelName = this.iconUrlListOfOut[0].label;
				this.billRecord.labelUrl = this.iconUrlListOfOut[0].url;
				const userRes = await getUserInfoAPI();
				if (!userRes) return;
				this.uploadMax = userRes.data.limitUpload > 9 ? 9 : userRes.data.limitUpload;
			},
			handleInputChange(e) {
				this.billRecord.amount = e.valueNumber;
			},
			async handleSubmit() {
				uni.showLoading({
					title: "上传中..."
				})
				if (!this.billRecord.id) {
					// 新增
					const tempAttachmentUrl = []
					for (let i = 0; i < this.imgList.length; i++) {
						let tempFilePath = this.imgList[i];
						let res = await this.$uploadFile(tempFilePath);
						if (!res) return;
						tempAttachmentUrl.push(res.data.url);
					}
					this.billRecord.attachmentUrl = tempAttachmentUrl.join(",");
					const choosedMthod = this.methodList.filter(method => method.id == this.chooseMthodId)
					this.billRecord.methodLabel = choosedMthod[0].label;
					this.billRecord.methodUrl = choosedMthod[0].url;
					const addRes = await addBillRecordAPI(this.billRecord);
					if (!addRes) {
						uni.hideLoading();
						return;
					}
					uni.hideLoading();
					this.$emit('onConfirm', this.billRecord); // 触发父组件回调
					this.handleClose()
				} else {
					// 修改操作
					// 1. 筛选需要上传的图片
					const tempAttachmentUrl = [];
					for (let i = 0; i < this.imgList.length; i++) {
						let tempFilePath = this.imgList[i];
						if (tempFilePath.includes("https:")) {
							// 字符串包含 "https:" 的逻辑
							tempAttachmentUrl.push(tempFilePath);
							continue;
						}
						let res = await this.$uploadFile(tempFilePath);
						if (!res) return;
						tempAttachmentUrl.push(res.data.url);
					}
					this.billRecord.attachmentUrl = tempAttachmentUrl.join(",");
					const choosedMthod = this.methodList.filter(method => method.id == this.chooseMthodId)
					this.billRecord.methodLabel = choosedMthod[0].label;
					this.billRecord.methodUrl = choosedMthod[0].url;
					const addRes = await updateBillRecordAPI(this.billRecord);
					if (!addRes) {
						uni.hideLoading();
						return;
					}
					uni.hideLoading();
					this.$emit('onConfirm', this.billRecord); // 触发父组件回调
					this.handleClose()
				}
			},
			handleError() {

			},
		}
	}
</script>

<style scoped>
	.type-active {
		padding: 16rpx;
		color: white;
		font-weight: bold;
		background-color: #ffa500;
		border-radius: 15rpx;
	}

	.type-normal {
		padding: 16rpx;
		color: lightgray;
		background-color: #ebebeb;
		border-radius: 15rpx;
	}

	.type-icon-active {
		background-color: #ffa500;
	}

	.type-label-active {
		color: #ffa500;
	}

	.resource-scroll {
		white-space: nowrap;
		display: flex;
		flex-direction: row;
		padding: 10rpx 0;
	}

	.resource-item {
		display: inline-flex;
		align-items: center;
		gap: 10rpx;
		padding: 5rpx 20rpx;
		margin-right: 20rpx;
		border: 2rpx solid #ffa500;
		border-radius: 40rpx;
		color: black;
		min-width: 240rpx;
	}

	.resource-item .label {
		font-size: 26rpx;
	}

	.type-icon-active {
		background-color: #ffa500 !important;
		color: #ffffff !important;
		border-color: #ffa500 !important;
	}
</style>