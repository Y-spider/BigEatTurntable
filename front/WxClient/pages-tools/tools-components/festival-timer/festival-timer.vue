<template>
	<view>
		<!-- 顶部导航 -->
		<cu-custom :isBack="true" bgColor="bg-color-festival">
			<block slot="content">🎉 节日百科</block>
		</cu-custom>

		<view class="festival-page">
			<!-- 节日分类 -->
			<view class="category-tabs">
				<view v-for="(cat, idx) in categoryList" :key="idx" class="tab-item"
					:class="{ active: currentType === cat.value }" @click="switchType(cat.value)">
					{{ cat.label }}
				</view>
			</view>

			<!-- 自定义节日提示 -->
			<view v-if="currentType === 'custom'" class="custom-tip">
				系统将在您设置的节日时间当天的7:00AM与1：00PM提醒您哦 🎉
			</view>

			<!-- 新增自定义节日按钮 -->
			<view v-if="currentType === 'custom'" class="add-custom-btn" @click="handleShowAddPop">
				➕ 添加自定义活动
			</view>

			<!-- 节日卡片列表 -->
			<scroll-view scroll-y class="festival-list">
				<view class="festival-card" v-for="(item, index) in festivalList" :key="index"
					@click="showDetail(item)"
					@longpress="handleDeleteCustom(item)">
					<view class="card-header">
						<text class="festival-name">{{ item.name }}</text>
						<text class="festival-date">{{ formatDate(item.time) }}</text>
					</view>
					<view class="card-body">
						<text class="festival-cal">{{ item.calender || '—' }}</text>
						<view v-if="item.remaining > 0" class="countdown">
							<text class="days">{{ item.remaining }}</text>
							<text class="label">天后</text>
						</view>
						<view v-else-if="item.remaining == 0" class="countdown">
							<text class="label">今天</text>
						</view>
						<view v-else class="countdown">
							<text class="label">已过</text>
							<text class="days">{{ 0 - item.remaining }}</text>
						</view>
					</view>
				</view>
			</scroll-view>

			<!-- 节日详情弹窗 -->
			<view v-if="showPopup" class="popup-mask" @click="closePopup">
				<view class="popup-content" @click.stop>
					<view class="popup-header">
						<text class="popup-title">{{ currentFestival.name }}</text>
						<text class="popup-date">{{ formatDate(currentFestival.time) }}</text>
					</view>
					<scroll-view scroll-y class="popup-body">
						<view v-if="currentFestival.des && currentFestival.des.length">
							<view v-for="(desc, idx) in currentFestival.des" :key="idx" class="desc-item">
								<text class="label">{{ desc.label }}</text>
								<text class="value">{{ desc.value }}</text>
							</view>
						</view>
						<view v-else class="empty-text">
							暂无内容
						</view>
					</scroll-view>
					<view class="popup-footer">
						<button class="close-btn" @click="closePopup">我知道了</button>
					</view>
				</view>
			</view>

			<!-- 添加自定义节日弹窗 -->
			<view v-if="showAddPopup" class="popup-mask" @click="showAddPopup = false">
				<view class="popup-content" @click.stop>
					<view class="popup-header">
						<text class="popup-title">添加自定义活动</text>
					</view>
					<scroll-view scroll-y class="popup-body">
						<!-- 节日名称 -->
						<view class="cu-form-group">
							<view class="title">标题</view>
							<input @input="handleNameInput" placeholder="请输入标题" name="input"></input>
						</view>

						<view class="cu-form-group">
							<view class="title">日期选择</view>
							<picker mode="date" :value="saveForm.time" :start="startTime" end="2099-01-01"
								@change="DateChange">
								<view class="picker">
									{{saveForm.time}}
								</view>
							</picker>
						</view>

						<!-- 节日描述 -->
						<view class="cu-form-group align-start">
							<view class="title">描述</view>
							<textarea maxlength="-1" @input="textareaBInput" placeholder="多行文本输入框"></textarea>
						</view>
						
						<view class="cu-form-group align-start">
							<view class="title">tips</view>
							<textarea maxlength="-1" :disabled="true"  placeholder="1.系统会在选择的时间早上7:00与下午1:00提醒您\n 2. 长按活动可以删除创建的活动"></textarea>
						</view>
					</scroll-view>
					<view class="popup-footer">
						<button class="close-btn" @click="addCustomFestival">保存</button>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getFestivalAPI,
		saveCustomFestivalAPI,
		deleteByIdAPI
	} from "../../apis/festivalApi.js";

	export default {
		data() {
			return {
				startTime: "",
				saveForm: {
					des: [{label: "描述",value: ""}],
					name: "",
					time: ""
				},
				currentType: "custom",
				showPopup: false,
				currentFestival: {},
				showAddPopup: false,
				startDate: "2000-01-01",
				endDate: "2099-12-31",
				categoryList: [
					{
						label: "自定义",
						value: "custom"
					},
					{
						label: "二十四节气",
						value: "solar_term"
					},
					{
						label: "传统节日",
						value: "tradition"
					},
					{
						label: "国际日",
						value: "international"
					}
				],
				festivalList: [],
				tmplIds:["eV1m7anh8wsli5zjfehBzUmzBiFNkt2fOzviSkj727M"]
			};
		},
		methods: {
			handleDeleteCustom(item){
				let _this = this
				if(item.type == "custom"){
					uni.showModal({
						title:"删除",
						content:"是否删除所选活动？",
						async success(option){
							if(option.confirm){
								await deleteByIdAPI(item.id);
								_this.init();
							}else{
								console.log("取消删除")
							}
						}
					})
				}
			},
			handleShowAddPop(){
				  wx.requestSubscribeMessage({
				    tmplIds: this.tmplIds,
				    success: (res) => {
				      if (res.eV1m7anh8wsli5zjfehBzUmzBiFNkt2fOzviSkj727M === 'accept') {
				        // wx.showToast({
				        //   title: '订阅成功',
				        //   icon: 'success'
				        // });
				        this.showAddPopup = true
				      } else {
				        wx.showToast({
				          title: '用户已拒绝订阅',
				          icon: 'none'
				        });
				      }
				    },
				    fail: (err) => {
				      console.error("订阅失败：", err);
				      wx.showToast({
				        title: '订阅失败',
				        icon: 'none'
				      });
				    }
				  });
			},
			handleNameInput(input) {
				this.saveForm.name = input.detail.value
			},
			textareaBInput(input) {
				console.log()
				this.saveForm.des[0].value = input.detail.value
			},
			DateChange(picker) {
				this.saveForm.time = picker.detail.value
			},
			// 查看节日详情
			showDetail(item) {
				uni.navigateTo({
					url: "/pages-tools/tools-components/festival-timer/festival-detail?id=" + item.id
				});
			},

			// 切换分类
			switchType(type) {
				this.currentType = type;
				this.init();
			},

			// 格式化日期
			formatDate(dateStr) {
				const d = new Date(dateStr);
				return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`;
			},

			// 关闭弹窗
			closePopup() {
				this.showPopup = false;
				this.showAddPopup = false;
			},

			// 初始化节日列表
			async init() {
				try {
					const res = await getFestivalAPI(this.currentType);
					this.festivalList = res.data;
				} catch (err) {
					console.error(err);
					uni.showToast({
						title: "加载失败",
						icon: "none"
					});
				}
			},

			// 保存自定义节日
			async addCustomFestival() {
				if (!this.saveForm.name || !this.saveForm.time) {
					uni.showToast({
						title: "请填写完整信息",
						icon: "none"
					});
					return;
				}
				try {
					this.saveForm.des = JSON.stringify(this.saveForm.des);
					const res = await saveCustomFestivalAPI(this.saveForm);
					if (res) {
						uni.showToast({
							title: "添加成功",
							icon: "success"
						});
						this.showAddPopup = false;
						this.saveForm = {
							name: "",
							time: this.startDate,
							des: [{label: "描述",value: ""}]
						};
						this.init();
					} else {
						uni.showToast({
							title: "添加失败，请重试",
							icon: "none"
						});
					}
				} catch (error) {
					console.error(error);
					uni.showToast({
						title: "网络异常",
						icon: "none"
					});
				}
			},

			// 计算倒计时天数
			calculateRemaining(dateStr) {
				const now = new Date();
				const target = new Date(dateStr);
				const diff = target - now;
				return diff > 0 ? Math.ceil(diff / (1000 * 60 * 60 * 24)) : 0;
			}
		},
		async onLoad() {
			this.startDate = new Date().toISOString().split("T")[0];
			this.saveForm.time = this.startDate
			this.init();
		}
	};
</script>

<style>
	.festival-page {
		padding: 20rpx;
		background: linear-gradient(to bottom, #f8fbf8, #eef6ef);
	}

	.category-tabs {
		display: flex;
		justify-content: space-around;
		margin-bottom: 20rpx;
	}

	.tab-item {
		padding: 12rpx 30rpx;
		border-radius: 40rpx;
		background: #fff;
		font-size: 28rpx;
		color: #666;
		box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
	}

	.tab-item.active {
		background: #9be2b3;
		color: #fff;
	}

	/* 自定义节日提示和按钮 */
	.custom-tip {
		text-align: center;
		font-size: 28rpx;
		color: #666;
		margin-bottom: 20rpx;
	}

	.add-custom-btn {
		background: #9be2b3;
		color: #fff;
		text-align: center;
		padding: 12rpx 0;
		border-radius: 40rpx;
		font-size: 28rpx;
		margin-bottom: 20rpx;
	}

	/* 卡片样式 */
	.festival-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.festival-card {
		background: #fff;
		border-radius: 20rpx;
		padding: 24rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
		transition: transform 0.2s;
	}

	.festival-card:active {
		transform: scale(0.98);
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.festival-name {
		font-size: 34rpx;
		font-weight: 600;
		color: #333;
	}

	.festival-date {
		font-size: 28rpx;
		color: #6bbf59;
	}

	.card-body {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.festival-cal {
		color: #999;
		font-size: 26rpx;
	}

	.countdown {
		display: flex;
		align-items: baseline;
		gap: 6rpx;
	}

	.days {
		font-size: 40rpx;
		font-weight: bold;
		color: #6bbf59;
	}

	.label {
		color: #6bbf59;
		font-size: 26rpx;
	}

	/* 弹窗样式 */
	.popup-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.4);
		display: flex;
		justify-content: center;
		align-items: center;
		z-index: 99;
	}

	.popup-content {
		background: #fff;
		width: 85%;
		max-height: 80%;
		border-radius: 20rpx;
		display: flex;
		flex-direction: column;
		padding: 30rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
	}

	.popup-header {
		text-align: center;
		margin-bottom: 20rpx;
	}

	.popup-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}

	.popup-date {
		font-size: 26rpx;
		color: #999;
	}

	.popup-body {
		flex: 1;
		overflow-y: auto;
	}

	.desc-item {
		margin-bottom: 20rpx;
	}

	.value {
		display: block;
		color: #555;
		font-size: 28rpx;
		line-height: 1.6;
		margin-top: 8rpx;
	}

	.empty-text {
		text-align: center;
		color: #aaa;
		font-size: 30rpx;
		margin-top: 40rpx;
	}

	.popup-footer {
		text-align: center;
		margin-top: 20rpx;
	}

	.close-btn {
		background: #9be2b3;
		color: #fff;
		border-radius: 40rpx;
		padding: 12rpx 0;
	}

	/* 表单样式 */
	.form-item {
		margin-bottom: 20rpx;
	}

	.picker-display {
		width: 100%;
		padding: 12rpx;
		border: 1rpx solid #ddd;
		border-radius: 12rpx;
		font-size: 28rpx;
		color: #333;
	}

	.cu-form-group .title {
		min-width: calc(4em + 15px);
	}
</style>