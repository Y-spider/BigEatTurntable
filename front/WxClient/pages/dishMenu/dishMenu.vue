<!-- 菜谱页面，可以查看菜谱信息，需要看视频或者分享给朋友才可以 -->
<template>
	<view>
		<cu-custom :isBack="false">
			<block slot="content">烹饪厨房( ˘▽˘)っ♨</block>
		</cu-custom>
		<div style="position: fixed;background-color: #f3f3f7;">
			<menuListVue :type-list="typeList"></menuListVue>
		</div>
	</view>
</template>

<script>
	import menuListVue from "../../components/menu-list.vue";
	import {
		listDishWithPageAPI,
		getAllDishTypeAPI
	} from "@/apis/dishApi.js"
	export default {
		components: {
			menuListVue
		},
		data() {
			return {
				typeList: [] // 菜品种类列表
			}
		},
		// onShareAppMessage 页面级别监听，所以只能写在页面中
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
			async init() {
				try {
					let typePromise = getAllDishTypeAPI()
					Promise.all([typePromise]).then(([typeRes]) => {
						if (!typeRes) return;
						this.typeList = typeRes.data;
						// this.typeList = this.typeList.unshift({
						// 	id: -1,
						// 	name: "收藏"
						// })
					})
				} catch (err) {
					console.error('初始化失败', err);
				}
			}
		},
		onShow() {
			this.init()
		}
	}
</script>

<style>

</style>