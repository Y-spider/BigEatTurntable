<!-- 切换展示用户 -->
<template>
	<view>
		<view class="cu-modal" :class="modalName=='RadioModal'?'show':''" @tap="hideModal">
			<view class="cu-dialog" @tap.stop="">
				<radio-group class="block" @change="radioChange">
					<view class="cu-list menu text-left">
						<view class="cu-item" v-for="(item,index) in userList" :key="index">
							<label class="flex justify-between align-center flex-sub">
								<view class="flex-sub">{{item.name}}</view>
								<radio class="round" :class="item.id==chooseUser.id?'checked':''"
									:checked="item.id==chooseUser.id?true:false" :value="index"></radio>
							</label>
						</view>
					</view>
				</radio-group>
			</view>
		</view>

	</view>
</template>

<script>
	import {
		getCurrentBookAllUserAPI
	} from "@/apis/billApi.js";
	export default {
		data() {
			return {
				modalName: "",
				dishName: "",
				userList: [],
				chooseUser:{id:-1,name:"全部用户"}
				
			};
		},
		methods: {
			radioChange(item) {
				const index = parseInt(item.detail.value);
				this.chooseUser = this.userList[index];
				this.hideModal();
			},
			async open() {
				const res = await getCurrentBookAllUserAPI();
				if (!res) {
					this.userList.unshift({
						id: -1,
						name: "全部用户"
					});
					this.modalName = "RadioModal";
					return;
				}
				this.userList = res.data;
				this.userList.unshift({
					id: -1,
					name: "全部用户"
				})
				this.modalName = "RadioModal";
				console.log("this.userList", this.userList)

			},
			hideModal() {
				this.$emit('onChange', this.chooseUser); // 触发父组件回调
				this.modalName = "";
			},
		}
	}
</script>

<style>
</style>