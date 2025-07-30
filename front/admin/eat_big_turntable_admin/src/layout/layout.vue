<template>
  <el-container class="layout-root">
    <el-aside width="220px" class="layout-aside">
      <div class="logo-bar">
        <img src="https://img95.699pic.com/element/40125/8333.png_860.png" class="logo-img" />
        <span class="logo-title">吃货大转盘</span>
      </div>
      <div class="menu-wrapper">
        <menuView />
      </div>
    </el-aside>
    <el-container class="main-container">
      <el-header class="layout-header">
        <headView />
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import headView from './head.vue';
import menuView from './menu.vue';
export default {
  components: { headView, menuView },
  name: "LayoutPage",
  created() {
    if (this.$route.path === '/') {
      this.$router.push('/home');
    }
  }
}
</script>

<style scoped>
.layout-root {
  min-height: 100vh;
  max-height: 100vh;
  overflow: hidden;
}
.layout-header {
  background: #409EFF;
  color: #fff;
  padding: 0;
  height: 56px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  z-index: 10;
}
.layout-main {
  background: #f4f6fa;
  min-height: calc(100vh - 56px);
  padding: 24px;
  overflow: auto;
}
.layout-aside {
  background: #304156;
  color: #fff;
  min-height: 100vh;
  box-shadow: 2px 0 6px rgba(0,21,41,.08);
  padding: 0;
  display: flex;
  flex-direction: column;
}
.logo-bar {
  height: 56px;
  display: flex;
  align-items: center;
  padding-left: 24px;
  background: #263445;
  margin-bottom: 8px;
  flex-shrink: 0;
}
.menu-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}
.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}
.logo-title {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  letter-spacing: 2px;
}
.main-container {
    flex: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
}
</style>