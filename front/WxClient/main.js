import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
import cuCustom from './colorui/components/cu-custom.vue'
import {uploadAvatar,uploadFile} from "@/apis/commonApi.js";
Vue.component('cu-custom',cuCustom)
Vue.prototype.$uploadAvatar = (tempFilePath) => uploadAvatar(tempFilePath)
Vue.prototype.$uploadFile = (tempFilePath) => uploadFile(tempFilePath)
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}
// #endif