import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state:{
        token:"", // 用户token
        userInfo:"",// 用户信息 
    },
    // 用于对state进行一些列算数运算
    getters:{
        getToken(){
            return localStorage.getItem("token")
        }
    },
    // 接收来自用户的操作数据请求
    mutations: {
        setToken(state,token){
            state.token = token;
            // 本地localhost预存一份
            localStorage.setItem("token",token)
        },
        setUserInfo(state,userInfo){
            state.userInfo = userInfo;
        },
        clearAllStateData(state){
            state = {
                token:"",
                userInfo:""
            }
            console.log(state)
            return Promise.resolve()
        }
    },
    // 可以发送网络请求在这里 然后再mutations中进行数据的操作
   actions:{
        
   }
})