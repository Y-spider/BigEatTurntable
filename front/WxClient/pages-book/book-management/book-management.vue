<!-- 账本管理页面 -->
<template>
	<view>
		<cu-custom :isBack="true">
			<block slot="backText">返回</block>
			<block slot="content">账本管理 </block>
		</cu-custom>
		<!-- 账本展示列表 -->
		<view
			style="display: flex; width: 100vw; justify-content: center; flex-direction: column; gap: 30rpx; align-items: center; padding: 30rpx;">
			<view @click="changeBook(item)" @longpress="deleteBillBook(item)"
				class="book-item padding-xl radius shadow shadow-lg bg-white" v-for="(item,index) in bookList"
				:key="index" :style="item.id===chooseBook.id ? 'border: 1rpx solid #ffa500;':''">
				<view class="item-left" style="display: flex; align-items: center;">
					<view>
						<image :src="item.iconUrl" style="width: 100rpx; height: 100rpx;"></image>
					</view>
					<view class="book-name" style="font-size: larger;display: flex;flex-direction: column; gap: 10rpx;">
						<view>{{item.title}} <text v-if="item.type!='默认账本'"
								style="font-size: small; color: #ffa500;">({{item.type}})</text></view>
						<view v-if="item.shareCount" style="font-size: small; color: lightgray;">
							<text>{{item.shareCount}}人</text>
							<text v-if="item.type!='默认账本'" style="color: #ff0000; padding: 0 30rpx ;">
								(创建人{{item.name}})</text>
						</view>
					</view>
				</view>
				<view class="item-right">
					<view v-if="item.type != '默认账本' && chooseBook.id == item.id" class="fun-item"
						@click.stop="inviteFriend">邀请好友</view>
					<view v-if="chooseBook.id != item.id" @click="exportBillRecord(item)" class="fun-item"
						@click.stop="inportRecord">导入该账本账单</view>
				</view>
			</view>
			<view class="add-fun book-item s shadow shadow-lg bg-white"
				style="  gap: 30rpx; display: flex;justify-content: center;align-items: center; padding: 20rpx 60rpx; width: 95vw; background-color: white;"
				@click="handleAdd">
				<view class="cuIcon-add" style="color: #ffa500; font-size: 100rpx;"></view>
				<view style="font-size: 42rpx; color: #ffa500;">添加账单</view>
			</view>
		</view>
		<view class="remark-box" style="font-size: small; padding: 20rpx;">
			注:</br>
			1. 点击账本进行切换，暂不支持生成后修改账单信息。</br>
			2. 点击"导入该账本账单"即可将该账本数据导入当前选择的账本中。</br>
			3. 摊钱账本不可互相导入,导入后所有的记账人为导入人。</br>
			4. 点击邀请加入即可邀请好友加入该账本，默认账本不可邀请用户。</br>
			5. 最多添加20个账本，如有疑问，请联系客服。</br>
			<view style="color: red; font-weight:bolder;">
				6. 删除账本会把该账本内的账单全部删除，请慎重!</br>
				7. 删除共享账本不会通知好友!</br>
				8. 默认账单不可删除!</br>
			</view>
		</view>
		<view class="cu-modal bottom-modal" :class="modalName=='Modal'?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">{{modelTitle}}</view>
					<view class="action" @tap="hideModal">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view style="width: 100%; padding: 20rpx;">
					<view v-if="makeStep=='first'" class="make-book-step-first">
						<view @click="handleToSecond" class="make-book-item radius shadow shadow-lg bg-white">
							<view class="make-book-item-left">
								<image
									src="https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/共享账本.svg"
									class="book-icon"></image>

								<view class="book-info">
									<view class="book-title">共享账本</view>
									<view class="book-desc">多人共同记账，实时同步账单</view>
								</view>
							</view>

							<view class="make-book-item-right">
								<radio></radio>
							</view>
						</view>

						<view
							@click="handleToEndStep('摊钱账本','https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/平摊账本.svg')"
							class="make-book-item radius shadow shadow-lg bg-white">
							<view class="make-book-item-left">
								<image
									src="https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/平摊账本.svg"
									class="book-icon"></image>

								<view class="book-info">
									<view class="book-title">摊钱账本</view>
									<view class="book-desc">智能结算多人AA分摊，支付明细一目了然</view>
								</view>
							</view>

							<view class="make-book-item-right">
								<radio></radio>
							</view>
						</view>
					</view>
					<view v-if="makeStep == 'second'">
						<view @click="handleToEndStep(bookItem.title,bookItem.iconUrl)"
							v-for="(bookItem,index) in iconList" :key="index"
							class="make-book-item radius shadow shadow-lg bg-white">
							<view class="make-book-item-left">
								<image :src="bookItem.iconUrl" class="book-icon"></image>

								<view class="book-info">
									<view class="book-title">{{bookItem.title}}</view>
									<view class="book-desc">{{bookItem.remark}}</view>
								</view>
							</view>

							<view class="make-book-item-right">
								<radio></radio>
							</view>
						</view>
					</view>
					<!-- 最终点 -->
					<view v-if="makeStep == 'end'" style="display: flex; flex-direction: column; gap: 30rpx;">

						<view style="display: flex; align-items: center; gap: 20rpx; padding: 20rpx 0;">
							<image :src="bookData.iconUrl" class="book-icon"
								style="width: 100rpx; height: 100rpx; border-radius: 16rpx;"></image>

							<input class="uni-input" v-model="bookData.title" placeholder="请输入账本名称" focus style="
									flex: 1;
									font-size: 32rpx;
									height: 100rpx;
									border: 1rpx solid #f0f0f0;
									border-radius: 16rpx;
									background-color: #fafafa;
								" />
						</view>
						<view @click="handleSubmit"
							style="padding: 15rpx; display: flex;justify-content: center;align-items: center;">
							<view
								style="display: flex;justify-content: center;align-items: center; padding: 20rpx; background-color: #ffa500; color: white; font-size: large; font-weight: bold; width: 70vw; border-radius: 15rpx;">
								保存</view>
						</view>
					</view>
				</view>
			</view>
		</view>

	</view>
</template>

<style scoped>
	.make-book-item {
		display: flex;
		justify-content: space-between;
		padding: 20rpx;
		margin: 30rpx 0;
		gap: 20rpx;
	}

	.make-book-item-left {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.make-book-item-right {
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.book-icon {
		width: 100rpx;
		height: 100rpx;
	}

	.book-info {
		display: flex;
		flex-direction: column;
		gap: 10rpx;
		justify-content: center;
	}

	.book-title {
		font-weight: bold;
		display: flex;
	}

	.book-desc {
		font-size: small;
		color: lightgray;
	}

	.book-item {
		display: flex;
		gap: 15rpx;
		padding: 20rpx;
		justify-content: space-between;
		align-items: center;
		width: 100%;
		background-color: white;
		border-radius: 16rpx;
		border: 1rpx solid white;
	}

	.item-left {
		display: flex;
		align-items: center;
		gap: 20rpx;
		font-weight: bolder;
	}

	.item-right {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
		flex-wrap: nowrap;
	}

	.fun-item {
		width: 30vw;
		padding: 15rpx;
		color: white;
		background-color: #ffa500;
		border-radius: 15rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: smaller;
	}
</style>

<script>
	import {
		listAllBillBookAPI,
		getChoosedBillBookAPI,
		addBillBookAPI,
		changeCurrentChooseBillBookAPI,
		deleteBillBookAPI,
		mergeBillRecordAPI
	} from "@/apis/billApi.js";
	export default {
		data() {
			return {
				makeStep: "first",
				iconList: [{
						iconUrl: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/旅行账本.svg",
						title: "旅行账本",
						remark: "多人同行，账面清晰明了"
					},
					{
						iconUrl: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/情侣账本.svg",
						title: "情侣账本",
						remark: "用账本记录情侣之间的点点滴滴"
					},
					{
						iconUrl: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/宿舍账本.svg",
						title: "宿舍账本",
						remark: "舍友周末游玩，公共资产支出清晰可见"
					},
					{
						iconUrl: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/家庭账本.svg",
						title: "家庭账本",
						remark: "生活日常开销，柴米油盐酱醋茶"
					},
					{
						iconUrl: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/班级账本.svg",
						title: "班级账本",
						remark: "班级支出全班公开透明"
					},
					{
						iconUrl: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/账本.svg",
						title: "自定义账本",
						remark: "随心所欲，自定义账本"
					}
				],

				modelTitle: "选择创建类型",
				modalName: "",
				bookList: [],
				chooseBook: null,
				bookData: {
					title: "",
					type: "",
					iconUrl: ""
				}
			}
		},
		created() {
			this.init();
		},
		methods: {
			async exportBillRecord(item) {
				const _this = this;
				uni.showModal({
					content: `是否将账本【${item.title}】记录导入到当前账本【${this.chooseBook.title}】中?`,
					success: async (option) => { // ← 这里加 async
						if (option.confirm) {
							uni.showLoading({
								mask: true,
								title: "合并中..."
							})
							const res = await mergeBillRecordAPI(item.id, _this.chooseBook.id).catch(()=>{uni.hideLoading()});
							if (!res) {
								uni.hideLoading()
								return;
							}
							uni.showToast({
								icon: "success",
								title: "导入成功",
								duration: 2000
							})
						}
					},
					finnaly() {
						uni.hideLoading();
					}
				})
			},

			async deleteBillBook(item) {
				// 删除账本
				if (!item.isEdit) {
					uni.showToast({
						icon: "error",
						title: "无权限",
						duration: 2000
					})
					return;
				}
				let _this = this;
				uni.showModal({
					title: "删除",
					content: "是否删除所选账本，注意删除后所有记录将删除!!!",
					async success(option) {
						if (option.confirm) {
							const res = await deleteBillBookAPI(item.id)
							if (!res) return;
							_this.init();
						}
					}
				})
			},
			async handleSubmit() {
				uni.showLoading({
					title: "添加中.."
				})
				// 新增账本
				if (!this.bookData.title) {
					uni.showModal({
						showCancel: false,
						title: "账本名称不能为空!",
						success() {
							return;
						}
					})
				}
				const res = await addBillBookAPI(this.bookData);
				if (!res) {
					uni.hideLoading();
					return;
				}
				uni.hideLoading();
				uni.navigateBack();

			},
			handleToSecond() {
				this.modelTitle = "选择账本";
				this.makeStep = "second";
			},
			handleToEndStep(type, iconUrl) {
				this.modelTitle = type;
				this.bookData.type = type;
				this.bookData.iconUrl = iconUrl;
				this.makeStep = "end";
			},
			hideModal() {
				this.modalName = "";
				this.makeStep = "first";
			},
			handleAdd() {
				// 添加账本
				this.modalName = "Modal";
			},
			inportRecord() {

			},
			inviteFriend() {

			},
			async changeBook(item) {
				if (item.id == this.chooseBook.id) return;
				uni.showLoading({
					title: "切换中..."
				})
				const res = await changeCurrentChooseBillBookAPI({
					id: item.id
				});
				if (!res) {
					uni.hideLoading();
				};
				uni.hideLoading();
				this.chooseBook = item;
			},
			async init() {
				const chooseRes = await getChoosedBillBookAPI();
				const res = await listAllBillBookAPI();
				if (!res || !chooseRes) return;
				this.bookList = res.data;
				this.chooseBook = chooseRes.data;
			}
		}
	}
</script>