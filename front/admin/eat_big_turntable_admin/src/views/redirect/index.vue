<template>
  <div class="redirect-container">
    <div class="redirect-content">
      <i class="el-icon-loading"></i>
      <p>页面跳转中...</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PageRedirect',
  created() {
    setInterval(()=>{
      const { params, query } = this.$route
      const { path } = params
      this.$router.replace({ path: '/' + path, query })
    },30000)
   
  }
}
</script>

<style lang="scss" scoped>
.redirect-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  
  .redirect-content {
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