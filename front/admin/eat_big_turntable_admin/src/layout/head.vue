<template>
  <div class="head-bar" style="display: flex;flex-direction: row-reverse;">
    <div class="user-info" style="width:100% ;display: flex !important; flex-direction: row-reverse;">
     
      <el-dropdown>
        <span class="el-dropdown-link">
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-avatar shape="circle" :size="36" :src="url" />
      <span class="user-name">管理员</span>
    </div>
  </div>
</template>

<script>
export default {
  name: "HeadPagePage",
  data() {
    return {
      url: "https://img95.699pic.com/element/40125/8333.png_860.png"
    }
  },
  methods: {
    logout() {
      localStorage.removeItem("token");
      this.$router.push("/login");
      this.$message.success("退出成功!")
    }
  }
}
</script>

<style scoped>
.head-bar {
width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
  padding: 0 24px;
  background: #409EFF;
}
.head-left {
  display: flex;
  align-items: center;
}
.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}
.system-title {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  letter-spacing: 2px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-name { color: #fff; font-size: 15px; }
.el-dropdown-link { color: #fff; cursor: pointer; }
</style>