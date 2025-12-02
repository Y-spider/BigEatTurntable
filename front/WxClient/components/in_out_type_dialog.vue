<template>
	<view>
		<view 
			class="cu-modal drawer-modal justify-start" 
			:class="modalName=='DrawerModalL'?'show':''" 
			@tap="hideModal"
		>
			<view class="cu-dialog basis-lg" @tap.stop="">
				
				<!-- 可滚动分类区域 -->
				<scroll-view scroll-y="true" class="scroll-box">
					<view class="cu-list menu text-left">

						<!-- 支出 -->
						<view class="group-title">支出：</view>
						<view 
							class="cu-item arrow" 
							v-for="(outItem,index) in iconUrlListOfOut" 
							:key="index"
							@click="changeType(outItem)"
						>
							<view class="content">
								<view>{{ outItem.label }}</view>
							</view>
						</view>

						<!-- 收入 -->
						<view class="group-title">收入：</view>
						<view 
							class="cu-item arrow" 
							v-for="(inItem,index) in iconUrlListOfIn" 
							:key="index"
							@click="changeType(inItem)"
						>
							<view class="content">
								<view>{{ inItem.label }}</view>
							</view>
						</view>

					</view>
				</scroll-view>

			</view>
		</view>
	</view>
</template>


<script>
	export default {
		data() {
			return {
				modalName: "",
				chooseType: { id: -1, label: "全部分类", url: "" },

				// 收入分类
				iconUrlListOfIn: [
					{ id: 1, label: "工资", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/工资.svg" },
					{ id: 2, label: "理财", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/理财.svg" },
					{ id: 3, label: "退款", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/退款.svg" },
					{ id: 4, label: "奖金", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/奖金.svg" },
					{ id: 5, label: "礼金", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/礼金.svg" },
					{ id: 6, label: "兼职", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/兼职.svg" },
					{ id: 7, label: "生活费", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/生活费.svg" },
					{ id: 8, label: "生意", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/生意.svg" }
				],

				// 支出分类
				iconUrlListOfOut: [
					{ id: 1, label: "餐饮", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/餐饮.svg" },
					{ id: 2, label: "交通", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/交通.svg" },
					{ id: 3, label: "蔬菜", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/蔬菜.svg" },
					{ id: 4, label: "服饰", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/服饰.svg" },
					{ id: 5, label: "购物", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/购物.svg" },
					{ id: 6, label: "娱乐", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/娱乐.svg" },
					{ id: 7, label: "运动", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/运动.svg" },
					{ id: 8, label: "宠物", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/宠物.svg" },
					{ id: 9, label: "快递", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/快递.svg" },
					{ id: 10, label: "烟酒", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/烟酒.svg" },
					{ id: 11, label: "数码", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/数码.svg" },
					{ id: 12, label: "保险", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/保险.svg" },
					{ id: 13, label: "其他", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/其他.svg" },
					{ id: 14, label: "发红包", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/发红包.svg" },
					{ id: 15, label: "旅行", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/旅行.svg" },
					{ id: 16, label: "住房", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/住房.svg" },
					{ id: 17, label: "家电", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/家电.svg" },
					{ id: 18, label: "水果", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/水果.svg" },
					{ id: 19, label: "学习", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/学习.svg" },
					{ id: 20, label: "医疗", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/药品.svg" },
					{ id: 21, label: "缴费", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/生活缴费.svg" },
					{ id: 22, label: "转账", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/转账.svg" },
					{ id: 23, label: "礼物", url: "https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/礼物.svg" }
				]
			};
		},

		methods: {
			// 选择类型
			changeType(type) {
				this.chooseType = type;
				this.hideModal();
			},

			// 打开弹窗
			open() {
				this.modalName = "DrawerModalL";
			},

			// 关闭弹窗并回传选中
			hideModal() {
				this.$emit("onChange", this.chooseType);
				this.modalName = "";
			}
		}
	};
</script>


<style>
/* 滚动区域最大高度 */
.scroll-box {
  max-height: 99vh;  /* 弹框最大高度 ≈ 2/3 屏幕 */
  overflow-y: auto;
  padding: 20rpx 0;
  margin-top: 120rpx;
}

/* 组标题 */
.group-title {
  padding: 20rpx;
  font-weight: bold;
  font-size: larger;
  color: #ffa500;
}
</style>
