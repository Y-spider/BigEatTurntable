<template>
	<div>
		<LuckyWheel  ref="myLucky" width="200rpx" height="200rpx" offsetDegree=10 :blocks="blocks"
			:prizes="prizeList" :buttons="buttons" 
			 />
	</div>
</template>

<script>
/* eslint-disable */ 
export default {
	name:"LuckTurntable",
	props: {
		prizeList: {
			type: Array
		},
	},
	data() {
		return {
			resultPrize: null,
			LuckyWheel: null,
			defaultConfig: {
				accelerationTime: 2000,
				decelerationTime: 1600,
			},
			blocks: [{ padding: '13px', background: 'red' }],
			buttons: [
				{ radius: '20px', background: '#FFA500' },
				{ radius: '15px', background: '#FFA500' },
				{
					radius: '10px', background: '#FFA500',
					pointer: true,
					fonts: [{ text: '', top: '-20px' }]
				},
			],
		}
	},
	watch: {
		prizeList: {
			deep: true,
			handler(newVal, oldVal) {
				console.log("newVal",newVal)
				this.prizes = [...newVal]
			}
		}
	},
	methods: {
		checkSetting() {
			this.roatingDuration = 2000
		},

		randomInt(min, max) {
			// 确保 min 和 max 都是整数，并且 min <= max
			min = Math.ceil(min);
			max = Math.floor(max);
			return Math.floor(Math.random() * (max - min + 1)) + min;
		},
		// 点击抽奖按钮触发回调
		startCallBack() {
			setTimeout(() => {
				// 调用stop停止旋转并传递中奖奖品  不传入小标则可以使用range 权重了
				this.$refs.myLucky.stop()
			}, 2000)
		},
		// 抽奖结束触发回调
		endCallBack(prize) {
			this.resultPrize = prize
			this.audioPlay.stop()
			if (this.openMusic) {
				this.audioEnd.play()
			}
			this.modalName = "DialogModal2"
		},

	}
}
</script>

<style scoped>

</style>