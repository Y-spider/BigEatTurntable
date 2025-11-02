<template>
	<view class="edit-wheel">
		<cu-custom :isBack="true">
			<block slot="backText">返回</block>
			<block slot="content">编辑转盘</block>
		</cu-custom>
		<view v-if="!isCreate" class="edit-title">
			{{tableInfo.title}}
		</view>
		<view v-else class="edit-title">
			新建转盘
		</view>
		<!-- 选项列表 -->
		<view v-for="(item, idx) in prizeList" :key="idx" class="edit-card">
			<!-- 上部：名称 + 删除 + 颜色 -->
			<view class="edit-top">
				<button class="cu-btn round bg-red del-btn" @click="removeItem(idx)">－</button>
				<input :disabled="!tableInfo.canEdit" v-model="item.fonts[0].text" class="edit-input" placeholder="请输入奖项名称(不超过15字)" />
				<view class="color-dot" :style="{background: item.background}" @click="chooseColor(idx)"></view>
			</view>

			<!-- 下部：权重 + 数量 -->
			<view class="edit-bottom">
				<view class="bottom-item">
					<view class="cu-tag bg-yellow sm">权重</view>
					<uni-number-box :disabled="!tableInfo.canEdit" v-model="item.range"></uni-number-box>
				</view>
				<view v-if="!tableInfo.isRepeat" class="bottom-item">
					<view class="cu-tag bg-yellow sm">数量</view>
					<uni-number-box :disabled="!tableInfo.canEdit" v-model="item.count"></uni-number-box>
				</view>
			</view>
		</view>

		<!-- 添加新选项 -->
		<view class="add-row bg-white" style="display: flex; justify-content: space-around; align-items: center;">
			<button :disabled="!tableInfo.canEdit" class="cu-btn round bg-blue" @click="addItem">+ 添加新选项</button>
			<view class="cu-form-group">
				<view class="title">重复抽</view>
				<switch :disabled="!tableInfo.canEdit" class='orange radius' @change="changeRepeate" :class="tableInfo.isRepeat?'checked':''"
					:checked="tableInfo.isRepeat"></switch>
			</view>
			<button :disabled="!tableInfo.canEdit" class="cu-btn round bg-cyan" @click="showBatch = true">批量添加</button>
		</view>
		<!-- 批量添加弹窗 -->
		<view v-if="showBatch" class="batch-modal-mask" style="width: 100vw; height: 100vh;"
			@click.self="showBatch = false">
			<view class="batch-modal">
				<textarea v-model="batchText" style="width: 100%; height: 15vh;" class="batch-input"
					placeholder="每行一个选项">
		 </textarea>
				<view style="display: flex; justify-content: space-around; align-items: center;">
					<button class="cu-btn bg-blue" @click="batchAdd">添加</button>
					<button class="cu-btn bg-gray" @click="showBatch=false">取消</button>
				</view>
			</view>
		</view>
		<!-- 颜色选择弹窗 -->
		<view v-if="colorPickerIdx !== null" class="color-modal-mask" @click.self="colorPickerIdx = null">
			<view class="color-modal">
				<view class="color-list">
					<view v-for="color in colorList" :key="color" class="color-dot"
						:style="{background: color, border: color === prizeList[colorPickerIdx].background ? '2px solid #333' : 'none'}"
						@click="setColor(color)"></view>
				</view>
			</view>
		</view>
		<!-- 完成按钮 -->
		<button :disabled="!tableInfo.canEdit" class="cu-btn bg-yellow finish-btn" @click="finishEdit">完成({{prizeList.length}}项)</button>
	</view>
</template>

<script>
	const COLOR_POOL = [
		'#e9e8fe', '#b8c5f2', '#f7cac9', '#f6eac2', '#b5ead7', '#ffdac1', '#c7ceea',
		'#f2b5d4', '#f9f871', '#b2f7ef', '#f7d6e0', '#f7b2ad', '#b2b7f7', '#f7eab2',
		'#b2f7c1', '#f7b2e0', '#b2f7e0', '#f7b2b2', '#b2e0f7', '#e0b2f7', '#445ff7', "#FFFFFF", "#ff0852"
	]

	function getRandomColor(usedColors) {
		const available = COLOR_POOL.filter(c => !usedColors.includes(c))
		if (available.length === 0) return COLOR_POOL[Math.floor(Math.random() * COLOR_POOL.length)]
		return available[Math.floor(Math.random() * available.length)]
	}
	import {
		updateTurntableAPI,
		getTurntableDetailAPI,
		addTurntableAPI
	} from "@/apis/turntableApi.js"
	export default {
		data() {
			return {
				id: null,
				prizeList: [{
					fonts: [{
						text: '',
						top: '15%'
					}],
					lineClamp: 2,
					background: "#aaaaff",
					range: 1,
					count: 1,
				}],
				colorList: COLOR_POOL,
				colorPickerIdx: null,
				showBatch: false,
				batchText: '',
				tableInfo: {
					isRepeat: true,
					canEdit:true
				},
				isCreate: false // 标识是否是创建转盘
			}
		},
		methods: {
			changeRepeate() {
				this.tableInfo.isRepeat = !this.tableInfo.isRepeat;
			},
			addItem() {
				// 自动分配不重复颜色
				const used = this.prizeList.map(i => i.background)
				this.prizeList.push({
					fonts: [{
						text: '',
						top: '15%',
					}],
					lineClamp: 2,
					background: getRandomColor(used),
					range: 1,
					count: 1
				})
			},
			removeItem(idx) {
				this.prizeList.splice(idx, 1)
			},
			chooseColor(idx) {
				this.colorPickerIdx = idx
			},
			setColor(color) {
				if (this.colorPickerIdx !== null) {
					this.prizeList[this.colorPickerIdx].background = color
					this.colorPickerIdx = null
				}
			},
			batchAdd() {
				const lines = this.batchText.split('\n').map(t => t.trim()).filter(Boolean)
				lines.forEach(text => {
					this.prizeList.push({
						fonts: [{
							text,
							top: '15%',
							"lineClamp": 2
						}],
						range: 1,
						count: 1,
						background: getRandomColor(this.prizeList.map(i => i.background))
					})
				})
				this.showBatch = false
				this.batchText = ''
			},
			async saveNewTurntable() {
				let updateData = {
					content: JSON.stringify(this.prizeList),
					isRepeat:this.tableInfo.isRepeat
				}
				// const emptyPrizeList = this.prizeList.filter(prize => prize.text=='') || [];
				// if(emptyPrizeList.length > 0){
				// 	uni.showModal({
				// 		title:"选项不能为空!",
				// 		showCancel:false,
				// 	})
				// 	return;
				// }
				// 调用创建新转盘api
				uni.showModal({
					title: "新转盘名称",
					editable: true,
					placeholderText: '请输入新转盘名称',
					success: async (res) => {
						if (res.confirm) {
							if (res.content == "") {
								uni.showToast({
									icon: "error",
									title: "名称不能为空!!",
									duration: 2000,
								})
								return;
							}
							updateData.title = res.content
							let saveRes = await addTurntableAPI(updateData)
							uni.setStorageSync("indexSelectId",saveRes.data.id)
							uni.navigateBack()
						}
					}
				})
			},
			// 编辑完成
			async finishEdit() {
				if (this.isCreate) {
					this.saveNewTurntable()
					return;
				}
				// 进行保存
				let updateData = {
					id: this.id,
					content: JSON.stringify(this.prizeList),
					isRepeat:this.tableInfo.isRepeat
				}
				if (this.tableInfo.type != 0 || true) {
					uni.showModal({
						title: "转盘名称",
						editable: true,
						content: this.tableInfo.title,
						placeholderText: "编辑转盘名称",
						success: async (res) => {
							if (res.confirm) {
								if (res.content == "") {
									uni.showToast({
										icon: "error",
										title: "名称不能为空!!",
										duration: 2000,
									})
									return;
								}
								updateData.title = res.content
								let updateRes = await updateTurntableAPI(updateData)
								uni.setStorageSync('editPrizeList', this.prizeList)
								uni.setStorageSync("indexSelectId",updateRes.data.id);
								uni.navigateBack()
							}
						}
					})
				} else {
					let updateRes = await updateTurntableAPI(updateData)
					uni.setStorageSync("indexSelectId",updateRes.data.id);
					uni.setStorageSync('editPrizeList', this.prizeList)
					uni.navigateBack()
				}
			}
		},
		async onLoad(option) {
			// 可从上个页面传递数据过来
			if (option.id == 0) {
				// 表示是创建键盘
				this.isCreate = true
				return
			}
			const list = uni.getStorageSync('editPrizeList')
			this.id = option.id
			let res = await getTurntableDetailAPI(this.id)
			this.tableInfo = res.data
			this.tableInfo.content = JSON.parse(this.tableInfo.content)
			if(!this.tableInfo.isRepeat && this.tableInfo.content.includes("剩余:")){
				this.tableInfo.content.forEach(prize=>{
					prize.fonts[0].text = prize.fonts[0].text.split("-")[1]
				})	
			}
			this.prizeList = this.tableInfo.content

		},
		async onShow() {
			if (this.id) {
				let res = await getTurntableDetailAPI(this.id)
				this.tableInfo = res.data
				this.tableInfo.content = JSON.parse(this.tableInfo.content)
			}
		},
	}
</script>

<style scoped>
	.edit-wheel {
		/* padding: 32rpx; */
	}

	.edit-title {
		font-size: 40rpx;
		font-weight: bold;
		margin-bottom: 32rpx;
		text-align: center;
	}

	.edit-item {
		display: flex;
		align-items: center;
		background: #f7f7f7;
		border-radius: 16rpx;
		margin-bottom: 18rpx;
		padding: 12rpx 18rpx;
	}

	.del-btn {
		margin-right: 18rpx;
	}

	.edit-input {
		flex: 1;
		border: none;
		background: transparent;
		font-size: 32rpx;
		margin-right: 18rpx;
		outline: none;
	}

	.weight-input {
		width: 80rpx;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		padding: 8rpx;
		font-size: 28rpx;
		text-align: center;
		margin-right: 18rpx;
		background: #fff;
	}

	.color-dot {
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
		border: 2rpx solid #eee;
		margin-left: 8rpx;
	}

	.add-row {
		display: flex;
		justify-content: space-between;
		margin: 24rpx 0;
	}

	.batch-modal-mask,
	.color-modal-mask {
		position: fixed;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.15);
		z-index: 1000;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.batch-modal,
	.color-modal {
		background: #fff;
		border-radius: 16rpx;
		padding: 32rpx 24rpx;
		min-width: 500rpx;
		box-shadow: 0 2px 16px rgba(0, 0, 0, 0.08);
	}

	.batch-input {
		width: 400rpx;
		height: 180rpx;
		border-radius: 8rpx;
		border: 1rpx solid #eee;
		margin-bottom: 18rpx;
		font-size: 28rpx;
		padding: 12rpx;
	}

	.color-list {
		display: flex;
		flex-wrap: wrap;
		gap: 18rpx;
		max-width: 400rpx;
	}

	.finish-btn {
		width: 90%;
		margin: 32rpx 5% 0 5%;
		font-size: 36rpx;
		bottom: 18rpx;
	}

	.edit-card {
		background: #fff;
		border-radius: 20rpx;
		padding: 20rpx 24rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
		transition: all 0.2s ease;
	}

	.edit-card:active {
		transform: scale(0.98);
	}

	.edit-top {
		display: flex;
		align-items: center;
		gap: 18rpx;
		margin-bottom: 18rpx;
	}

	.del-btn {
		width: 60rpx;
		height: 60rpx;
		font-size: 40rpx;
		line-height: 60rpx;
		text-align: center;
		flex-shrink: 0;
	}

	.edit-input {
		flex: 1;
		font-size: 32rpx;
		padding: 0rpx 16rpx;
		border-radius: 12rpx;
		border: 1rpx solid #eee;
		background: #fafafa;
		outline: none;
		height: 70rpx;
		line-height: 70rpx;
	}

	.color-dot {
		width: 44rpx;
		height: 44rpx;
		border-radius: 50%;
		border: 2rpx solid #ddd;
		box-shadow: 0 0 6rpx rgba(0, 0, 0, 0.1);
		flex-shrink: 0;
	}

	.edit-bottom {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-top: 6rpx;
	}

	.bottom-item {
		display: flex;
		align-items: center;
		gap: 10rpx;
		font-size: 28rpx;
	}
</style>