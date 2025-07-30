<template>
  <div class="auth-redirect-container">
    <div class="auth-redirect-content">
      <i class="el-icon-loading"></i>
      <p>认证中...</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AuthRedirect',
  created() {
    // 处理认证重定向逻辑
    this.$router.push('/')
  }
}
</script>

<style lang="scss" scoped>
.auth-redirect-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  
  .auth-redirect-content {
    text-align: center;
    
    i {
      font-size: 40px;
      color: #409EFF;
      margin-bottom: 20px;
    }
    
    p {
      font-size: 16px;
      color: #606266;
    }
  }
}
</style> 