<!-- 菜谱页面，可以查看菜谱信息，需要看视频或者分享给朋友才可以 -->
<template>
	<view>
		<cu-custom :isBack="false">
			<block slot="content">烹饪厨房( ˘▽˘)っ♨</block>
		</cu-custom>
		<div style="height: 160px;">
			<menuListVue :type-list="typeList"></menuListVue>
		</div>
	</view>
</template>

<script>
	import menuListVue from "../../components/menu-list.vue";
	import { listDishWithPageAPI , getAllDishTypeAPI } from "@/apis/dishApi.js"
	export default {
		components:{
			menuListVue
		},
		data() {
			return {
				typeList:[] // 菜品种类列表
			}
		},
		methods: {
			async init(){
				 try {
				  let typePromise = getAllDishTypeAPI()
				  Promise.all([typePromise]).then(([typeRes])=>{
					if (!typeRes) return;		
					this.typeList = typeRes.data;
				  })
				  } catch (err) {
				    console.error('初始化失败', err);
				  }
			}
		},
		onShow(){
			this.init()
		}
	}
</script>

<style>

</style>
