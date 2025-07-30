<template>
  <div class="error-page">
    <div class="error-code">404</div>
    <div class="error-desc">抱歉，你访问的页面不存在</div>
    <div class="error-handle">
      <el-button type="primary" @click="goHome">返回首页</el-button>
      <el-button @click="goBack">返回上一页</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ErrorPage404Page',
  methods: {
    goHome() {
      this.$router.push('/')
    },
    goBack() {
      this.$router.go(-1)
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
    color: #409EFF;
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