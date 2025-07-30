<template>
  <div class="error-page">
    <div class="error-code">401</div>
    <div class="error-desc">抱歉，您没有权限访问此页面</div>
    <div class="error-handle">
      <el-button type="primary" @click="goLogin">重新登录</el-button>
      <el-button @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ErrorPage401',
  methods: {
    goLogin() {
      this.$router.push('/login')
    },
    goHome() {
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.error-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  
  .error-code {
    font-size: 120px;
    font-weight: bold;
    color: #F56C6C;
    margin-bottom: 20px;
  }
  
  .error-desc {
    font-size: 24px;
    color: #606266;
    margin-bottom: 30px;
  }
  
  .error-handle {
    .el-button {
      margin: 0 10px;
    }
  }
}
</style> 