<template>
	<view>
		<cu-custom :isBack="true">
			<block slot="backText">返回</block>
			<block slot="content">{{tableName}}</block>
		</cu-custom>
		<!--  转盘详情页 -->
		<view class="turntable-box">
			<Turntable :turntable="turntableInfo" @routeDone="getRouteResult" :prizeList="prizeList"
				style="margin: 50rpx;"></Turntable>
			<view class="fun-button" style="display: flex;justify-content: space-between; align-items: center;">
				<view class="fun-but">
					<button v-if="prize.isMake" @click="handleShowMake" class="cu-btn bg-gradual-green shadow"> <text
							class="cuIcon-form" style="margin: 0 10rpx;"></text>菜谱</button>
				</view>
				<view v-if="isShowEditButton" class="fun-but">
					<button class="cu-btn bg-red shadow" @click="goEdit"> <text class="cuIcon-edit"
							style="margin: 0 10rpx;"></text> 编辑</button>
				</view>
			</view>
		</view>
		<!-- 分享弹框 -->
		<share-pop-dialog ref="sharePopDialogRef" />
		<!-- 广告区域 -->
		
	</view>
</template>

<script>
	import Turntable from "@/components/Turntable.vue";
	import sharePopDialog from "@/components/share_pop_dialog.vue";
	import {
		getTurntableDetailAPI
	} from "@/apis/turntableApi.js";
	import {
		listDishRandomAPI
	} from "@/apis/dishApi.js";
	export default {
		components: {
			Turntable,
			sharePopDialog
		},
		data() {
			return {
				tableName: "",
				id: null,
				prizeList: [],
				prize: null, // 奖品
				turntableInfo: {},
				dishTypeId: null,
				isShowEditButton:true
			}
		},
		onShow() {
			this.init()
		},
		onShareAppMessage() {
			let expireTime = Date.now() + 30 * 60 * 1000;
			uni.setStorageSync("hasPermissionCheckDetail", {
				expireTime
			})
			return {
				title: '吃货大转盘',
				path: '/pages/index/index',
				withShareTicket: true
			}
		},
		methods: {
			async handleGenerateTurntableInfo() {
				// 根据随机获取的菜品列表，生成转盘信息
				const res = await listDishRandomAPI(6, this.dishTypeId);
				if (!res) return;
				const dishList = res.data;

				// 一些背景色池（可以自定义更多）
				const bgColors = [
					"#e9e8fe", "#b8c5f2", "#cf7e40", "#e2b29d",
					"#947975", "#6abf69", "#ffb347", "#87ceeb"
				];

				// 转换成转盘数据结构
				const turntableInfoContent = dishList.map((dish, index) => {
					return {
						fonts: [{
							text: dish.name, // 菜品名字
							top: "10%",
							lineClamp: 2
						}],
						id:dish.id,
						isMake:dish.isMake,
						background: bgColors[index % bgColors.length], // 循环取颜色
						lineClamp: 2,
						range: 1,
					};
				});
				// 存到本地变量/状态中（比如 this.turntableInfo）
				this.prizeList = turntableInfoContent;
				this.turntableInfo.title = this.tableName +"菜品"; // 设置转盘名称
			},

			handleShowMake() {
				// 查看菜品制作页面
				const checkPermision = uni.getStorageSync("hasPermissionCheckDetail");
				if (!checkPermision || checkPermision?.expireTime <= Date.now()) {
					this.$refs.sharePopDialogRef.open();
				}else{
					uni.navigateTo({
						url: "/pages/dish_detail/dish_detail?id=" + this.prize.id
					})
				}
			},
			goEdit() {
				let isSystem = true
				uni.setStorageSync('editPrizeList', this.prizeList)
				uni.navigateTo({
					url: `/pages/editWheel/index?turntableName=${this.tableName}&id=${this.id}&isSystem=${isSystem}`
				})
			},
			getRouteResult(prize) {
				this.prize = prize
			},
			async init() {
				if (isNaN(this.dishTypeId)) {
					let res = await getTurntableDetailAPI(this.id)
					this.turntableInfo = res.data
					this.prizeList = JSON.parse(res.data.content)
					this.isShowEditButton = true;
				} else {
					this.isShowEditButton = false;
					// 处理随机菜单转盘
					this.handleGenerateTurntableInfo();
				}
			}
		},
		onLoad(option) {
			this.id = parseInt(option.id)
			this.dishTypeId = parseInt(option.dishTypeId)
			this.tableName = option.tableName
		}

	}
</script>

<style>

</style>