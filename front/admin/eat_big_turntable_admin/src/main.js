import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store/store'

// reset-css 是样式库，直接引入即可
import "reset-css"

// lucky-canvas
import { LuckyWheel, LuckyGrid } from '@lucky-canvas/vue'

// ElementUI
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI)

// 注册组件（而不是 Vue.use）
Vue.component('LuckyWheel', LuckyWheel)
Vue.component('LuckyGrid', LuckyGrid)

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
