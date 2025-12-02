<template>
	<view>

		<!-- 顶部导航栏 -->
		<cu-custom :isBack="true">
			<block slot="content">{{bookName}}</block>
		</cu-custom>

		<!-- 选择用户 -->
		<view class="cu-form-group">
			<view class="title">用户：</view>
			<view class="user-select" @click="changeShowUser">
				{{showUserInfo.name}} ▼
			</view>
		</view>

		<!-- 分类 -->
		<view class="cu-form-group" @click="openTypeDialog">
			<view class="title">分类：</view>
			<view class="category-box">
				{{searchParams.type}} ▼
			</view>
		</view>

		<!-- 时间 -->
		<view class="cu-form-group">
		  <view class="title">时间：</view>
		  <view class="time-box flex align-center gap">
		    <!-- 开始日期 picker -->
		    <picker mode="date" :value="searchParams.startTime" @change="onStartDateChange" :start="startDateMin" :end="startDateMax">
		      <view class="picker-display">{{ searchParams.startTime || '不限制' }}</view>
		    </picker>
		
		    <text style="font-size: large; font-weight: bold;">-</text>
		
		    <!-- 结束日期 picker -->
		    <picker mode="date" :value="searchParams.endTime" @change="onEndDateChange" :start="endDateMin" :end="endDateMax">
		      <view class="picker-display">{{ searchParams.endTime || '不限制' }}</view>
		    </picker>
		  </view>
		</view>



		<!-- 账户 -->
		<view class="cu-form-group">
			<view class="title">账户：</view>
			
				<scroll-view scroll-x class="resource-scroll">
					<view
						:style="chooseMthodId==item.id?'background-color: #ffa500; color:white;':'background-color: white;'"
						@click="handleChooseMethod(item)" v-for="(item,index) in methodList" :key="index"
						class="resource-item">
						<view class="cu-avatar"
							:style="chooseMthodId==item.id?'background-color: #ffa500;':'background-color: white;'">
							<image  :src="item.url" class="cu-avatar sm"
								:style="chooseMthodId==item.id?'background-color: #ffa500;':'background-color: white;'">
							</image>
						</view>
						<view class="label" style="flex: 1;">{{ item.label }}</view>
					</view>
				</scroll-view>
			
		</view>

		<!-- 备注 -->
		<view class="cu-form-group">
			<view class="title">备注：</view>
			<view class="remark-box">
				<input placeholder="请输入备注" name="input" v-model="searchParams.remark" />
			</view>
		</view>

		<!-- 搜索按钮 -->
		<view class="padding-tb-lg">
			<button class="cu-btn round block" style="width: 80vw; margin: 30rpx 10vw; padding: 40rpx; font-size: large; color: white; border-radius: 15rpx; background-color: #ffa500;" @click="showList">搜索</button>
		</view>

		<!-- 选择用户弹窗 -->
		<change-show-user-dialog ref="changeShowUserDialog" @onChange="userChangeClllBack"></change-show-user-dialog>

		<!-- 分类弹窗 -->
		<in-out-type-dialog ref="inOutTypeDialog" @onChange="typeChangeCallBack"></in-out-type-dialog>

	</view>
</template>


<script>
	import changeShowUserDialog from '../../components/change_show_user_dialog.vue';
	import inOutTypeDialog from '../../components/in_out_type_dialog.vue';
	export default {
		components: {
			changeShowUserDialog,
			inOutTypeDialog
		},
		data() {
			return {
				showUserInfo: {
					id: -1,
					name: "全部用户"
				},
				bookName: "账单",
				searchParams: {
					userId: -1,
					type: "全部分类",
					startTime: "",
					endTime: "",
					methodType: "全部账户",
					remark: ""

				},
				methodList: [{
						id: -1,
						label: "全部账户",
						url: ""
					},
					{
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
				chooseMthodId: -1,
			}
		},
		onLoad(option) {
			this.bookName = option.bookName || "账单";
		},
		methods: {
			handleChooseMethod(item) {
				this.chooseMthodId = item.id;
				this.searchParams.methodType = item.label;
			},
			onStartDateChange(e) {
				this.searchParams.startTime = e.detail.value;
			},
			onEndDateChange(e) {
				this.searchParams.endTime = e.detail.value;
			},
			openTypeDialog() {
				this.$refs.inOutTypeDialog.open();
			},
			typeChangeCallBack(type) {
				this.searchParams.type = type.label;
			},
			userChangeClllBack(chooseUser) {
				this.showUserInfo = chooseUser;
				this.searchParams.userId = chooseUser.id;
			},
			changeShowUser() {
				this.$refs.changeShowUserDialog.open();
			},
			showList() {
				const paramObj = JSON.stringify(this.searchParams);
				uni.navigateTo({
					url: `/pages-book/bill-search/bill-search-show-list?paramObj=${paramObj}&bookName=${this.bookName}`
				})
			},
			goBack() {
				uni.navigateBack()
			}
		}
	}
</script>

<style scoped>
	.page-container {
		padding: 10rpx 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	/* 表单分组 */
	.cu-form-group {
		background-color: #fff;
		padding: 20rpx;
		border-radius: 10rpx;
		display: flex;
		align-items: center;
	}

	.title {
		flex-shrink: 0;
		width: 160rpx;
		font-size: larger;
		/* font-weight: bold; */
		color: #333;
	}

	/* 用户选择：绿色底 */
	.user-select {
		background-color: #ffa500;
		color: #fff;
		padding: 15rpx 20rpx;
		border-radius: 15rpx;
		font-weight: bold;
	}

	/* 分类：灰白底 */
	.category-box {
		background-color: #ffa500;
		color: #fff;
		padding: 15rpx 20rpx;
		border-radius: 15rpx;
	}

	/* 时间：两按钮区域 */
	/* 账户按钮区域 */
	.account-box {
		row-gap: 20rpx;
	}

	/* 备注输入框 */
	.remark-box {
		flex: 1;
	}

	.cu-form-input {
		border: 1px solid #eee;
		border-radius: 6rpx;
		padding: 12rpx 15rpx;
		font-size: 26rpx;
		width: 100%;
		box-sizing: border-box;
	}

	picker::after {
		display: none !important;
	}

	picker {
		padding: 0;
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
	.picker-display {
	  width: 300rpx;        /* 固定宽度 */
	  background-color: #fff; 
	  border-radius: 12rpx; /* 圆角，可根据需求调整 */
	  text-align: center;
	  white-space: nowrap;   /* 不换行 */
	  overflow: hidden;      /* 超出隐藏 */
	  text-overflow: ellipsis; /* 超出显示省略号 */
	  line-height: 60rpx;    /* 根据按钮高度调整垂直居中 */
	}

</style>