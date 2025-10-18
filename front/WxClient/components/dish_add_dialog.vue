<!-- 菜品申请请求 -->
<template>
  <view>
    <view class="cu-modal" :class="modalName === 'Modal' ? 'show' : ''">
      <view class="cu-dialog">
        <!-- 标题栏 -->
        <view class="cu-bar bg-white justify-end">
          <view class="content">菜品申请</view>
        </view>

        <!-- 内容 -->
        <view class="padding-xl">
          <form @submit.prevent>
            <view style="padding: 30rpx 10rpx; display: flex; gap: 5rpx;">
              <view class="title">菜品名称 </view>
              <input
                placeholder="请输入您想要的菜品名称"
                v-model="dishName"
				style="border-bottom: 3rpx solid #ffa500; width: 80%; color: #ffa500;"
              />
            </view>

            <!-- 按钮 -->
            <view
              style="display: flex; justify-content: center; align-items: center; gap: 50rpx;"
            >
              <button
                type="button"
                class="cu-btn round bg-yellow shadow"
                @click="handleConfirm"
              >
                确定
              </button>
              <button type="button" class="cu-btn round shadow" @click="hideModal">
                取消
              </button>
            </view>
          </form>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { addApplyDishAPI } from "@/apis/applyDishApi.js";

export default {
  name: "dish_add_dialog",
  data() {
    return {
      modalName: "",
      dishName: "",
    };
  },
  methods: {
    hideModal() {
      this.modalName = "";
    },
    open() {
      this.modalName = "Modal";
    },
    async handleConfirm() {
      if (!this.dishName) return;

      uni.showLoading({
        mask: true,
        title: "上传中....",
      });

      try {
        const res = await addApplyDishAPI({
          dishName: this.dishName,
        });

        if (res) {
          uni.showToast({
            title: "反馈成功",
            duration: 2000,
            success: () => {
              
            },
          });
        }
      } finally {
        uni.hideLoading();
      }
    },
  },
};
</script>

<style scoped>
/* 可根据需要增加样式 */
</style>
