<template>
	<view>
		<view class="cu-modal" :class="modalName=='DialogModal1'?'show':''">
			<view class="cu-dialog">
				<view class="cu-bar bg-white justify-end">
					<view class="content">提示</view>
					<view class="action" @click="hideModal">
						<text class="cuIcon-close text-red"></text>
					</view>
				</view>
				<view class="padding-xl">
					分享到任意微信群后即可查看所有菜品信息(有效期30分钟)
				</view>
				<view class="cu-bar bg-white justify-end">
					<view class="action">
						<button class="cu-btn line-green text-green" @tap="hideModal">取消</button>
						<button open-type="share" class="cu-btn bg-green margin-left" @tap="hideModal">确定</button>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "share_pop_dialog",
		data() {
			return {
				modalName: undefined
			};
		},
		methods: {
			confirmShare(){
				uni.setStorageSync("")
			},
			hideModal() {
				// DialogModal1
				this.modalName = "";
			},
			open(){
				this.modalName = "DialogModal1";
			}
		}
	}
</script>

<style>

</style>