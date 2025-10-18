const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  // 设置打包后的公共路径
  publicPath: '/turntable/'
})
