import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store/store'
import resetCss from "reset-css"
import VueLuckyCanvas from '@lucky-canvas/vue' // 大转盘依赖

// 引入Element UI
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI)
Vue.use(resetCss)
Vue.use(VueLuckyCanvas)
Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

