<!-- 自己封装的一个菜单展示组件 -->
<template>
	<view style="display: flex;flex-direction: column; gap: 5rpx;">
		<!-- 左侧菜品类型上下滑动 -->
		<view class="search">
				<uni-search-bar class="uni-mt-10" radius="5" 
				placeholder="请输入菜品名称" 
				clearButton="auto"
				cancelButton="none"
				@confirm="search" />
		</view>
		<view style="display: flex; gap: 5rpx;">
			<view class="dish-type-box">
				<scroll-view :style="{ height: scrollViewHeight() + 'px' }" scroll-y="true" class="scroll-Y" >
					<view :id="index"
					@click="changeType(type)"
					:class="selectedId==type.id?'active-item':''"
					v-for="type,index in typeList" 
					:key="index"
					class="scroll-view-item dish-type">
					  {{type.name}}
					</view>
				</scroll-view>
			</view>
			<!-- 右侧具体菜品展示 -->
			<view class="dish-item-box">
				<scroll-view :style="{ height: scrollViewHeight() + 'px' }" scroll-y="true" class="scroll-Y">
					<view :id="index" @click="changeType(type)"  v-for="dish in dishList" :key="dish.id" class="scroll-view-item dish-item">
					 <view class="cu-card article no-card" style="width: 70vw;padding:1rpx 15rpx;">
					 	<view class="cu-item shadow">
					 		<view class="content" style="display: flex;align-items: center;">
					 			<image class="dish-cover-image" style="width: 128rpx; height: 128rpx; border-radius: 15rpx;" :src="dish.coverUrl" mode="aspectFill"></image>
					 			<view class="desc" style="display: flex; flex-direction: column;gap: 5rpx;">
					 				<view style="flex: 1;color: #ffa500;font-weight: bolder;" class="text-content"> {{handleDishName(dish.name)}} </view>
					 				<view style="display: flex;flex-wrap: nowrap;"  class="dish-des">
					 					{{dish.dishDes || ''}}
					 				</view>
									<view style="flex: 1;display: flex;flex-direction: row-reverse;">
										<view style="position: relative;right: 30rpx; color: #ffa500;" class="cu-tag light sm round cuIcon-likefill">{{dish.likeCount}}</view>
									</view>
					 			</view>
					 		</view>
					 	</view>
					 </view>

					</view>
					<view :id="index" @click="changeType(type)" class="scroll-view-item dish-item">
					 <view class="cu-card article no-card" style="width: 70vw;padding:1rpx 15rpx;">
					 	<view class="cu-item shadow">
					 		<view class="content" style="display: flex;align-items: center;min-height: 160rpx;align-items: center;">
					 			<div class="cuIcon-deliver_fill"></div>
								没有心仪菜谱，点击反馈，说不定下次就会出现哦~~~ ૮(˶ᵔ ᵕ ᵔ˶)ა
					 		</view>
					 	</view>
					 </view>
					
					</view>
				</scroll-view>
			</view>
		</view>
		<!-- 弹框加载 -->
		<view class="cu-load load-modal" v-if="loading">
		    <view class="gray-text">°꒰๑'ꀾ'๑꒱°加载中...</view>
		</view>
	</view>
</template>

<script>
	import { listDishWithPageAPI } from "@/apis/dishApi.js"
	export default {
		name:"menu-list",
		props:{
			typeList:{
				type:Array,
				require:true
			}
			
		},
		data() {
			return {
				pages:0,
				currentPage:1,
				loading:false,
				queryParams:{
					page:1,
					limit:10,
					queryMap:{
						name:"",
						type: 0
					},
				},
				isBottom:false,
				dishList:[], // 菜品列表
				selectedId:0,
				scrollViewHeight(){
					return  uni.getSystemInfoSync().windowHeight - this.CustomBar
				}
			};
		},
		onReachBottom(){
			// 上拉刷新
			console.log("触发")
			if(this.currentPage<=this.pages){
				return;
			}
			this.currentPage+=1;
			this.queryParams.page = this.currentPage;
			getDishList();
		},
		created(){
			this.getDishList()
		},
		methods:{
			handleDishName(name){
				return name.split('-')[0];
			},
			restFilds(){
				this.queryParams.page = 1
				this.queryParams.queryMap.type = ""
				this.dishList = []
				this.queryParams.queryMap.name = ""
				this.isBottom = false
			},
			noop(){},
			search(val){
				this.restFilds()
				this.queryParams.queryMap.name = val.value
				this.getDishList()
			},
			changeType(type){
				if(type.id==this.selectedId) return;
				this.selectedId = type.id
				this.restFilds()
				this.queryParams.queryMap.type = type.id
				this.getDishList()
			},
			async getDishList(){
				this.loading  = true
				let res = await listDishWithPageAPI(this.queryParams)
				if(!res) return;
				this.dishList = [...this.dishList,...res.data.records]
				this.loading = false
				this.pages = res.data.pages;
				
			}
		},
	}
</script>

<style scoped>
	.dish-type-box,.dish-item-box{
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 30rpx;
	}
	.dish-type{
		width: 30vw;
		height: 100rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		background-color: #f3f3f7;
	}
	.active-item{
		background-color: #ffa500;
	}
	.dish-box{
		flex: 1;
	}
	.dish-cover-image{
		width: 128rpx;
		height: 128rpx;
		border-radius: 15rpx;
	}
	.dish-des{
		  height: 80rpx;
		  overflow: hidden;
		  display: -webkit-box;
		  -webkit-box-orient: vertical;
		  -webkit-line-clamp: 1;   /* ← 把 2 换成你需要的行数 */
		  /* 下面两行可要可不要，取决于你是否允许自动折行 */
		 /* word-break: break-all;
		  line-height: 1.5; */
	}
</style>