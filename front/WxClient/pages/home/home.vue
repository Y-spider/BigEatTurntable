<template>
	<view>
		<cu-custom :isBack="false">
		    <!-- <block slot="backText">返回</block> -->
		    <block slot="content">吃货大转盘</block>
		</cu-custom>
		热门转盘
		<Turntable></Turntable>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				
			}
		},
		methods: {
			
		}
	}
</script>

<style>

</style>
