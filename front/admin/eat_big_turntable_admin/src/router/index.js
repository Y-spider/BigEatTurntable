import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)


import Layout from '@/layout/layout'

export const constantRoutes = [
  {
    path: '/redirect',
    hidden: true,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/test',
    component: () => import('@/views/index'),
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: 'home',
        component: () => import('@/views/index'),
        name: 'HomePage',
      },
      {
        path: 'dish',
        component: () => import('@/views/dish/index'),
        name: 'DishPage',
      },
      {
        path: 'dish/add-tutorial',
        component: () => import('@/views/dish/tutorial'),
        name: 'AddDishTutorial',
      },
      {
        path: 'dish/edit-tutorial/:id',
        component: () => import('@/views/dish/tutorial'),
        name: 'EditDishTutorial',
        props: true
      },
      {
        path: 'notice',
        component: () => import('@/views/notice/index'),
        name: 'NoticePage',
       
      },
      {
        path: 'turntable',
        component: () => import('@/views/turntable/index'),
        name: 'TurntablePage',
       
      },
      {
        path: 'dishType',
        component: () => import('@/views/dish/dishType'),
        name: 'dishType',
       
      },{
        path: 'apply',
        component: () => import('@/views/apply/index'),
        name: 'aaply',
      }
    ]
  }
]


const createRouter = () => new Router({
  mode: 'history',
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})
const router = createRouter()

// 前置全局路由守卫
router.beforeEach((to, from, next) => {
  if(to.path===from.path){
    next()
  }
  // 如果没有 token 且目标路径不是登录页面
  if (!localStorage.getItem("token") && to.path !== "/login") {
    next({ path: "/login" }); // 使用 next 方法直接跳转到登录页面
  } else {
    next(); // 允许跳转
  }
});


export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}



export default router
