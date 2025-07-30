import axios from 'axios'
import { Message,confirm } from 'element-ui'
import store from '@/store/store'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_URL,
  timeout: 60000  // 60s连接超时
})
console.log("baseurl",process.env)

// 请求拦截器
service.interceptors.request.use(
  // 给请求头添加上token信息
  config => {
    if (localStorage.getItem("token")) {
      // 修正：使用 config.headers['token'] 而不是 config.headers.set
      config.headers['token'] = localStorage.getItem("token"); // 添加请求头
    }
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，说明接口有问题，把错误信息显示给用户
    if (res.code !== 200) {
      Message({
        message: res.errMsg || '系统错误',
        type: 'error',
        duration: 5 * 1000
      })

      // 50008: 非法的token; 50012: 其他客户端登录; 50014: Token过期了;
      if (res.code === -99 || res.code === 50012 || res.code === 50014) {
        // 这个地方有问题，后续需要进行处理
        confirm(
          '您已被登出，可以取消继续留在该页面，或者重新登录',
          '确定登出',
          {
            confirmButtonText: '重新登录',
            type: 'warning'
          },

        ).then(() => {
          store.commit('clearAllStateData').then(() => {
            location.reload()
          })
        })
        .catch((err)=>{
          Message.error("系统发送错误" + err)
          location.reload()
        })
      }
      return false
    } else {
      return res
    }
  },
  error => {
    console.log('err' + error)
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return false
  }
)

export default service 