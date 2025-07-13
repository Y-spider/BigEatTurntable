# q-turntable

# 插件市场导入  转盘图片是网上找的，请开发时自行替换为自己项目的转盘图

# vue2事例代码
```
<template>
	<view><q-turntable ref="turntable" @start="turntableStart" @success="turntableSuccess"></q-turntable></view>
</template>
<script>

export default {
  data() {
    return {
      award: 1,
      awardList: [
      	{
      		title: '特等奖'
      	},
      	{
      		title: '一等奖'
      	},
      	{
      		title: '二等奖'
      	},
      	{
      		title: '三等奖'
      	},
      	{
      		title: '四等奖'
      	},
      	{
      		title: '啥也没有'
      	}
      ] // 顺时针对应每个奖项 

    };
  },

  methods: {
    // 用户点击开始抽奖
    turntableStart() {
      let index = Math.floor(Math.random() * 6 + 1) //前端随机数，这里应该后台返回中奖结果
      this.award = index
      this.$refs.turntable.begin(this.award);
    },

    // 抽奖完成后操作
    turntableSuccess() {
      const index = this.award - 1;
      console.log('bind:success', this.awardList[index]);
      uni.showToast({
        title: `恭喜你获得${this.awardList[index].title}`,
        icon: 'none'
      });
    },

  }
}
</script>

```
# vue3示例代码

```
<template>
	<view><q-turntable ref="turntable" @start="turntableStart" @success="turntableSuccess"></q-turntable></view>
</template>

<script setup>
import { reactive, toRefs, ref } from 'vue';
const data = reactive({
	award: 1,
	awardList: [
		{
			title: '特等奖'
		},
		{
			title: '一等奖'
		},
		{
			title: '二等奖'
		},
		{
			title: '三等奖'
		},
		{
			title: '四等奖'
		},
		{
			title: '啥也没有'
		}
	] // 顺时针对应每个奖项
});
const turntable = ref(null);
const { award, awardList } = toRefs(data);
// 用户点击开始抽奖
const turntableStart = () => {
	let index = Math.floor(Math.random() * 6 + 1);//前端随机数，这里应该后台返回中奖结果
	data.award = index;
	turntable.value.begin(data.award);
};

// 抽奖完成后操作
const turntableSuccess = () => {
	const index = data.award - 1;
	uni.showToast({
		title: `恭喜你获得${data.awardList[index].title}`,
		icon: 'none'
	});
};
</script>

<style>


</style>

```
# 如果插件对你们有帮助的话，请给个五星鼓励一下，谢谢！如果遇到问题或者有更好的建议可以加我qq 965969604