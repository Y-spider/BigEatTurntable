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
    <view v-for="(item, idx) in prizeList" :key="idx" class="edit-item">
      <button class="cu-btn round bg-red del-btn" @click="removeItem(idx)">-</button>
      <input v-model="item.fonts[0].text" class="edit-input" placeholder="请输入选项" />
      <view style="display: flex; justify-content: center;align-items: center; gap: 15rpx;">
		  <view class='cu-tag bg-yellow sm'>权重</view>
		  <uni-number-box v-model="item.range"></uni-number-box>
	  </view>
      <view class="color-dot" :style="{background: item.background}" @click="chooseColor(idx)"></view>
    </view>
    <!-- 添加新选项 -->
    <view class="add-row" style="display: flex; justify-content: space-around;">
      <button class="cu-btn round bg-blue" @click="addItem">+ 添加新选项</button>
      <button class="cu-btn round bg-cyan" @click="showBatch = true">批量添加</button>
    </view>
    <!-- 批量添加弹窗 -->
    <view v-if="showBatch" class="batch-modal-mask" style="width: 100vw; height: 100vh;" @click.self="showBatch = false">
      <view class="batch-modal">
        <textarea v-model="batchText" style="width: 100%; height: 15vh;"
		 class="batch-input" placeholder="每行一个选项">
		 </textarea>
       <view style="display: flex; justify-content: space-around; align-items: center;">
		   <button class="cu-btn bg-blue" @click="batchAdd">批量添加</button>
		   <button class="cu-btn bg-gray" @click="showBatch=false">取消</button>
	   </view>
      </view>
    </view>
    <!-- 颜色选择弹窗 -->
    <view v-if="colorPickerIdx !== null" class="color-modal-mask" @click.self="colorPickerIdx = null">
      <view class="color-modal">
        <view class="color-list">
          <view
            v-for="color in colorList"
            :key="color"
            class="color-dot"
            :style="{background: color, border: color === prizeList[colorPickerIdx].background ? '2px solid #333' : 'none'}"
            @click="setColor(color)"
          ></view>
        </view>
      </view>
    </view>
    <!-- 完成按钮 -->
    <button class="cu-btn bg-yellow finish-btn" @click="finishEdit">完成({{prizeList.length}}项)</button>
  </view>
</template>

<script>
const COLOR_POOL = [
  '#e9e8fe', '#b8c5f2', '#f7cac9', '#f6eac2', '#b5ead7', '#ffdac1', '#c7ceea',
  '#f2b5d4', '#f9f871', '#b2f7ef', '#f7d6e0', '#f7b2ad', '#b2b7f7', '#f7eab2',
  '#b2f7c1', '#f7b2e0', '#b2f7e0', '#f7b2b2', '#b2e0f7', '#e0b2f7','#445ff7',"#FFFFFF","#ff0852"
]
function getRandomColor(usedColors) {
  const available = COLOR_POOL.filter(c => !usedColors.includes(c))
  if (available.length === 0) return COLOR_POOL[Math.floor(Math.random() * COLOR_POOL.length)]
  return available[Math.floor(Math.random() * available.length)]
}
import {updateTurntableAPI,getTurntableDetailAPI,addTurntableAPI } from "@/apis/turntableApi.js"
export default {
  data() {
    return {
	  id:null,
      prizeList: [
        // { fonts: [{ text: '炒饭', top: '15%' }], background: '#e9e8fe' },
      ],
      colorList: COLOR_POOL,
      colorPickerIdx: null,
      showBatch: false,
      batchText: '',
	  tableInfo:{},
	  isCreate:false // 标识是否是创建转盘
    }
  },
  methods: {
    addItem() {
      // 自动分配不重复颜色
      const used = this.prizeList.map(i => i.background)
      this.prizeList.push({
       fonts: [{ text:'', top: '15%',"lineClamp": 2}],
        background: getRandomColor(used),
		"range":1
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
          fonts: [{ text, top: '15%',"lineClamp": 2}],
		  "range":1,
          background: getRandomColor(this.prizeList.map(i => i.background))
        })
      })
      this.showBatch = false
      this.batchText = ''
    },
	async saveNewTurntable(){
		let updateData = {
		  content: JSON.stringify(this.prizeList),
		}
		// 调用创建新转盘api
		uni.showModal({
		  title: "新转盘名称",
		  editable: true,
		  placeholderText: '请输入新转盘名称',
		  success: (res) => {
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
		      addTurntableAPI(updateData)
		      uni.navigateBack()
		    }
		  }
		})
	},
    async finishEdit() {
		if(this.isCreate){
			this.saveNewTurntable()
			return;
		}
      // 返回并传递数据
      // 进行保存
      let updateData = {
        id: this.id,
        content: JSON.stringify(this.prizeList),
      }
      if (this.tableInfo.type!=0) {
        uni.showModal({
          title: "新转盘名称",
          editable: true,
          placeholderText: '请输入新转盘名称',
          success: (res) => {
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
              updateTurntableAPI(updateData)
              uni.setStorageSync('editPrizeList', this.prizeList)
              uni.navigateBack()
            }
          }
        })
      } else {
        updateTurntableAPI(updateData)
        uni.setStorageSync('editPrizeList', this.prizeList)
        uni.navigateBack()
      }
    }
  },
  async onLoad(option) {
    // 可从上个页面传递数据过来
	if(option.id==0){
		// 表示是创建键盘
		this.isCreate = true
		return
	}
	const list = uni.getStorageSync('editPrizeList')
	if (list && Array.isArray(list)) {
	  this.prizeList = list
	}
	this.id = option.id
	let res =  await getTurntableDetailAPI(this.id)
	this.tableInfo = res.data
	this.tableInfo.content = JSON.parse(this.tableInfo.content)
  }
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
.batch-modal-mask, .color-modal-mask {
  position: fixed;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.15);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}
.batch-modal, .color-modal {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx 24rpx;
  min-width: 500rpx;
  box-shadow: 0 2px 16px rgba(0,0,0,0.08);
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
}
</style> 