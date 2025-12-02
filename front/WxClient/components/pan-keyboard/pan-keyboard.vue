<template>
	<view>
		<!-- 金额键盘 -->
		<view class="m-amount" v-if="type === 'amount'">
			<view class="fingerboard" :class="{isDecimal_:!isDecimal}">
				<view class="flex_c_c fingerboard-item"
					:class="[`fingerboard-item-${index}`,item.name == 'submit' ? isCheck ? 'fingerboard_item_color' : 'fingerboard_item_color_' : '']"
					hover-class="hover_class" :hover-stay-time="50" v-for="(item, index) in fingerboardListAmount"
					:key="index" @click="KeyboardAmount(item.name)">
					<view class="fingerboard-img" v-if="item.name === 'x'">
						<image class="img"
							src="data:image/svg+xml;base64,PHN2ZyBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEzNTUgMTAyNCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCI+PHBhdGggZD0iTTQxMC4xNjYgMTIxLjQ2N2g3NzUuMzIzdjc4MS4wNjZINDEwLjE2NkwxNTkuOTk1IDUxMmwyNTAuMTctMzkwLjUzM3ptNTUzLjM2MiAxOTQuNzM4Yy04LjcwNy0xMS42OTMtMjguOTkxLTExLjY5My0zNy42OTggMEw3NjkuMTU3IDQ3NC4wMjZsLTE1Ni42MjYtMTU3LjgyYy04LjcwNy0xMS42OTQtMjYuMTItOC43NzYtMzcuNzIxIDAtMTEuNjAxIDguNzc1LTExLjYwMSAyNi4zMDMgMCAzNy45OTZMNzMxLjQ4MyA1MTIgNTc0LjgxIDY2OS44MjFjLTExLjYwMSA4Ljc1My0xMS42MDEgMjYuMjggMCAzNy45NzQgOC43MyAxMS42OTMgMjYuMTIgMTEuNjkzIDM3LjcyIDBsMTU2LjY1LTE1Ny44MjEgMTU2LjY1IDE1Ny44MmM4LjcwNyAxMS42OTQgMjYuMDk3IDExLjY5NCAzNy42OTggMCAxMS42MDEtOC43NzUgMTEuNjAxLTI2LjMwMyAwLTM3Ljk5Nkw4MDYuOTAxIDUxMmwxNTYuNjczLTE1Ny44MjFjMTEuNTc4LTguNzUzIDExLjU3OC0yNi4yOCAwLTM3Ljk3NHoiIGRhdGEtc3BtLWFuY2hvci1pZD0iYTMxM3guc2VhcmNoX2luZGV4LjAuaTAuMTU2NjNhODFoUEZCc0EiIGNsYXNzPSJzZWxlY3RlZCIvPjwvc3ZnPg=="
							mode="aspectFill"></image>
					</view>
					<view v-else-if="item.name === 'submit'">{{submitButtonText}}</view>
					<view v-else>{{ item.name }}</view>
				</view>
			</view>
		</view>

		<!-- 数字键盘 -->
		<view class="m-numeral" v-if="type === 'numeral'">
			<view class="input-content" v-if="isPassword">
				<view class="flex_c_c input">
					<view class="input-box" v-for="(item, index) in 6" :key="index"></view>
				</view>
				<view class="flex_r fa_c input2">
					<view class="flex_c_c input-box" v-for="(item, index) in password" :key="index">
						<view class="input-box-str"></view>
					</view>
				</view>
			</view>
			<view class="fingerboard">
				<view class="size_white flex_c_c fingerboard-item" :class="[`fingerboard-item-${index}`]"
					hover-class="hover-class" :hover-stay-time="60" v-for="(item, index) in fingerboardListNumeral"
					:key="index" @click="KeyboardNumeral(item.name)">
					<view class="text_42" v-if="item.name != 'delete'">{{ item.name }}</view>
					<view class="flex_c_c fingerboard-icon" v-else>
						<image class="wh_100"
							src="data:image/svg+xml;base64,PHN2ZyBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEzNTUgMTAyNCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCI+PHBhdGggZD0iTTQxMC4xNjYgMTIxLjQ2N2g3NzUuMzIzdjc4MS4wNjZINDEwLjE2NkwxNTkuOTk1IDUxMmwyNTAuMTctMzkwLjUzM3ptNTUzLjM2MiAxOTQuNzM4Yy04LjcwNy0xMS42OTMtMjguOTkxLTExLjY5My0zNy42OTggMEw3NjkuMTU3IDQ3NC4wMjZsLTE1Ni42MjYtMTU3LjgyYy04LjcwNy0xMS42OTQtMjYuMTItOC43NzYtMzcuNzIxIDAtMTEuNjAxIDguNzc1LTExLjYwMSAyNi4zMDMgMCAzNy45OTZMNzMxLjQ4MyA1MTIgNTc0LjgxIDY2OS44MjFjLTExLjYwMSA4Ljc1My0xMS42MDEgMjYuMjggMCAzNy45NzQgOC43MyAxMS42OTMgMjYuMTIgMTEuNjkzIDM3LjcyIDBsMTU2LjY1LTE1Ny44MjEgMTU2LjY1IDE1Ny44MmM4LjcwNyAxMS42OTQgMjYuMDk3IDExLjY5NCAzNy42OTggMCAxMS42MDEtOC43NzUgMTEuNjAxLTI2LjMwMyAwLTM3Ljk5Nkw4MDYuOTAxIDUxMmwxNTYuNjczLTE1Ny44MjFjMTEuNTc4LTguNzUzIDExLjU3OC0yNi4yOCAwLTM3Ljk3NHoiIGRhdGEtc3BtLWFuY2hvci1pZD0iYTMxM3guc2VhcmNoX2luZGV4LjAuaTAuMTU2NjNhODFoUEZCc0EiIGNsYXNzPSJzZWxlY3RlZCIvPjwvc3ZnPg=="
							mode="aspectFill"></image>
					</view>
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	let enterAmount = '';
	export default {
		props: {
			// amount：金额
			// numeral：数字
			type: {
				type: String,
				default: 'amount'
			},
			// 以下参数是金额输入相关------------------------------
			// 是否可以输入小数
			isDecimal: {
				type: Boolean,
				default: true
			},
			// 提交按钮文本
			submitButtonText: {
				type: String,
				default: '确定'
			},
			/**
			 * 保留小数，最大3，
			 */
			toFixed: {
				type: [String, Number],
				default: 2
			},
			/**
			 * 输入最大值
			 */
			maxValue: {
				type: [String, Number],
				default: 100000000 //1亿
			},
			// 提交按钮是否通过校验
			isCheck: {
				type: Boolean,
				default: true
			},
			// -----------------------------------------------------
			// 以下是数字模式
			/**
			 * 是否密码模式
			 * 固定6位数密码
			 */
			isPassword: {
				type: Boolean,
				default: true
			},
		},
		data() {
			return {
				password: [],
				fingerboardListAmount: [{
						name: '1'
					},
					{
						name: '2'
					},
					{
						name: '3'
					},
					{
						name: 'x'
					},
					{
						name: '4'
					},
					{
						name: '5'
					},
					{
						name: '6'
					},
					{
						name: 'submit'
					},
					{
						name: '7'
					},
					{
						name: '8'
					},
					{
						name: '9'
					},
					{
						name: '0'
					},
					{
						name: '.'
					}
				],
				fingerboardListNumeral: [{
						name: '1'
					},
					{
						name: '2'
					},
					{
						name: '3'
					},
					{
						name: '4'
					},
					{
						name: '5'
					},
					{
						name: '6'
					},
					{
						name: '7'
					},
					{
						name: '8'
					},
					{
						name: '9'
					},
					{
						name: 'delete'
					},
					{
						name: '0'
					}
				]
			};
		},
		watch: {
			isDecimal: {
				handler: function(newV) {
					if (!newV) {
						this.fingerboardListAmount.pop()
					}
				},
				immediate: true
			}
		},
		created() {
			enterAmount = ''
		},
		methods: {
			cleanAll() {
				enterAmount = ''
				this.password = []
			},


			// 以下参数是金额输入相关----------------------
			/**
			 * 调用此方法直接修改enterAmount的值，同时会回调
			 * 使用场景：给输入框默认值时
			 */
			setKeyboard(e) {
				enterAmount = `${e}`
				const value = this.setValue()
				this.$emit('onChange', value)
			},
			KeyboardAmount(key) {
				let lastIndexOf = enterAmount.lastIndexOf('.');
				switch (key) {
					case 'x':
						if (!enterAmount.length) return; //已经没有数了
						enterAmount = enterAmount.substr(0, enterAmount.length - 1);
						if (enterAmount[enterAmount.length - 1] === '.') { //如果删掉的前一个是点.那么就把点也删掉
							enterAmount = enterAmount.substr(0, enterAmount.length - 1);
						}
						break;
					case '.':
						if (lastIndexOf > 0 && key === '.') return; //只能有一个点.
						if (!enterAmount.length && key === '.') return; //第一个数不能是点.
						enterAmount = `${enterAmount}${key}`;
						break;
					case 'submit':
						if (!enterAmount) return;
						break;
					default:
						if (lastIndexOf > 0 && enterAmount.length - lastIndexOf > Number(this.toFixed))
							return; //只能有toFixed位小数
						if ((enterAmount[0] === '0' && enterAmount.length === 1) && key != '.') return;
						if (Number(`${enterAmount}${key}`) > Number(this.maxValue)) return this.$emit('onError',
							'maxValue'), console.warn(
							`输入不通过，大于了maxValue的限定`);
						enterAmount = `${enterAmount}${key}`;
						break;
				};
				const value = this.setValue()
				if (key === 'submit') {
					if (this.isCheck) {
						this.$emit('onSubmit', value)
					} else {
						this.$emit('onError', 'isCheck')
					}
				} else {
					this.$emit('onChange', value)
				}
			},
			setValue() {
				let numberEnterAmount = Number(enterAmount);
				numberEnterAmount = enterAmount === '' ? '' : numberEnterAmount
				if (enterAmount.slice(-3) === '.00') {
					enterAmount = `${enterAmount.slice(0,-3)}`
				}
				let value = {
					valueToLocaleString: numberEnterAmount.toLocaleString(),
					valueNumber: numberEnterAmount,
					valueText: this.enterAmountCapitalFn(enterAmount),
					enterAmount
				}
				// 最后一个是0
				if (enterAmount[enterAmount.length - 1] === '.') {
					value['valueToLocaleString'] = `${value['valueToLocaleString'] }.`
				}
				if (enterAmount.slice(-2) === '.0') {
					value['valueToLocaleString'] = `${value['valueToLocaleString'] }.0`
				}
				if (enterAmount === '0.0') {
					value['valueToLocaleString'] = `${enterAmount}`
				}
				return value;
			},

			enterAmountCapitalFn(enterAmount) {
				let lastIndexOf = enterAmount.lastIndexOf('.')
				let str = ''
				if (lastIndexOf > 0 && lastIndexOf === enterAmount.length - 1) {
					enterAmount = enterAmount.slice(0, lastIndexOf)
				}
				if (lastIndexOf > 0) {
					let ending = enterAmount.slice(lastIndexOf + 1)
					if (!ending.length) return this.toChineseBig(enterAmount) + '点';
					str = "点"
					for (let i = 0; i < ending.length; i++) {
						str = str + numToChinese(ending[i])
					}

					function numToChinese(n) {
						var chineseBigNum = '零壹贰叁肆伍陆柒捌玖'
						return chineseBigNum[n]
					}
					enterAmount = enterAmount.substring(0, lastIndexOf) //.前面的
				}
				return this.toChineseBig(enterAmount) + str
			},

			toChineseBig(num) {
				var strNum = String(num)
				var unit = ['拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟']
				var result = ['@']
				var unitNo = 0
				for (let i = strNum.length - 1;; i--) {
					result.unshift(numToChinese(strNum[i]))
					if (i <= 0) {
						break
					}
					result.unshift(unit[unitNo])
					unitNo++
				}
				return result.join('').replace(/(零[仟佰拾]){1,3}/g, '零').replace(/零{2,}/g, '零').replace(/零([万亿])/g, '$1')
					.replace(/亿万/g, '亿').replace(/零*@/g, '')

				function numToChinese(n) {
					var chineseBigNum = '零壹贰叁肆伍陆柒捌玖'
					return chineseBigNum[n]
				}
			},
			// ----------------------------------------

			// 以下参数是数字键盘相关----------------------
			KeyboardNumeral(value) {
				if (value === 'delete') {
					// 删除
					try {
						this.password = this.password.slice(0, this.password.length - 1);
					} catch (e) {}
				} else {
					if (this.password.length < 6 && this.isPassword) {
						this.password.push(value);
						this.determinePassword(value);
					}
					if (!this.isPassword) {
						this.password.push(value);
					}
				}
				this.$emit('onChange', {
					value: value,
					allValueArr: this.password,
					allValueStr: this.password.join('')
				});

			},
			// 密码输入完成
			determinePassword() {
				try {
					if (this.password.length === 6) {
						const value = this.password.join('');
						this.$emit('passwordSuccess', value);
					}
				} catch (e) {}
			}
		}
	};
</script>

<style scoped lang="scss">
	.icon_ {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.hover_class::after {
		position: absolute;
		z-index: 2;
		content: '';
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.1);
	}

	.m-amount {
		width: 100%;

		.fingerboard {
			width: 100%;
			display: grid;
			grid-template-columns: 1fr 1fr 1fr 1fr;
			grid-template-rows: 90rpx 90rpx 90rpx 90rpx;
			grid-gap: 8px;

			.fingerboard-item {
				position: relative;
				overflow: hidden;
				background-color: #fff;
				border-radius: 10rpx;
				text-align: center;
				line-height: 90rpx;
				color: #000;
				font-size: 36rpx;
				font-weight: bold;

				.delete {
					width: 60rpx;
					height: 60rpx;
				}
			}

			.fingerboard-item-7 {
				line-height: 310rpx;
				grid-column: 4 / 4;
				grid-row: 2 / 5;
			}

			.fingerboard-item-11 {
				grid-column: 1 / 3;
				grid-row: 4 / 4;
			}

			.fingerboard_item_color {
				background-color:#ffa500;
				color: #fff;
			}

			.fingerboard_item_color_ {
				opacity: 0.4;
				background-color: #ffa500;
				color: #fff;
			}

			.hover-class {
				background-color: #dcdcdc;
			}

			.fingerboard-img {
				box-sizing: border-box;
				padding-right: 6rpx;
				width: 60rpx;
				height: 60rpx;

				.img {
					width: 100%;
					height: 100%;
				}
			}
		}

		.isDecimal_ {
			.fingerboard-item-11 {
				grid-column: 1 / 4;
				grid-row: 4 / 4;
			}
		}
	}






	// 下方是数字键盘-----------------------------------------
	.m-numeral {
		margin: 0 auto;
		width: 100%;

		.input-content {
			position: relative;
			width: 100%;
			height: 100rpx;
			margin-bottom: 36rpx;

			.input {
				width: calc(100% - 100rpx);
				height: 100rpx;
				margin: 0 auto;
				display: grid;
				grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
				grid-template-rows: 100rpx;
				grid-gap: 10rpx 0rpx;

				.input-box {
					width: 80rpx;
					height: 80rpx;
					border-radius: 10rpx;
					background-color: #f2f2f2;
				}
			}

			.input2 {
				position: absolute;
				z-index: 2;
				top: 0;
				left: 50rpx;
				width: calc(100% - 100rpx);
				height: 100rpx;
				margin: 0 auto;
				display: grid;
				grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
				grid-template-rows: 100rpx;
				grid-gap: 10rpx 0rpx;

				.input-box {
					width: 80rpx;
					height: 80rpx;
					border-radius: 10rpx;

					.input-box-str {
						width: 20rpx;
						height: 20rpx;
						background-color: #272727;
						border-radius: 50%;
					}
				}
			}
		}





		.fingerboard {
			width: 100%;
			display: grid;
			grid-template-columns: 1fr 1fr 1fr;
			grid-template-rows: 100rpx 100rpx 100rpx 100rpx;
			grid-gap: 8px;

			.fingerboard-item {
				background-color: #f1f1f1;
				border-radius: 10rpx;
				text-align: center;
				line-height: 100rpx;
				color: #000;
				font-weight: bold;

				.delete {
					width: 60rpx;
					height: 60rpx;
				}
			}

			.fingerboard-item-10 {
				grid-column: 1 / 3;
				grid-row: 4 / 4;
			}

			.fingerboard_item_color {
				background-color: #58be6b;
				color: #fff;
			}

			.hover-class {
				background-color: #dcdcdc;
			}

			.fingerboard-icon {
				width: 60rpx;
				height: 60rpx;
			}
		}
	}
	// -----------------
	.text_42{
		font-size: 43rpx;
	}
	.wh_100{
		width: 100%;
		height: 100%;
	}
	.flex_r {
		display: flex;
		flex-direction: row;
	}
	
	.flex_c {
		display: flex;
		flex-direction: column;
	}
	
	.fj_b {
		justify-content: space-between;
	}
	
	.fj_a {
		justify-content: space-around;
	}
	
	.fj_c {
		justify-content: center;
	}
	
	.fa_c {
		align-items: center;
	}
	
	.flex_c_c {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
	
	.flex_r_c {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: center;
	}
	
	.flex1 {
		flex: 1;
	}
</style>