<template>
  <el-menu
    :default-active="$route.path"
    class="el-menu-vertical-demo"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409EFF"
    :router="true"
    unique-opened
  >
    <el-menu-item index="/home">
      <i class="el-icon-s-home menu-icon"></i>
      <span class="menu-title">首页</span>
    </el-menu-item>
    <el-menu-item index="/dish">
      <i class="el-icon-dish menu-icon"></i>
      <span class="menu-title">菜品管理</span>
    </el-menu-item>
    <el-menu-item index="/dishType">
      <i class="el-icon-s-platform menu-icon"></i>
      <span class="menu-title">类别管理</span>
    </el-menu-item>
    <el-menu-item index="/turntable">
      <i class="el-icon-s-help menu-icon"></i>
      <span class="menu-title">转盘管理</span>
    </el-menu-item>
    <el-menu-item index="/notice">
      <i class="el-icon-message-solid menu-icon"></i>
      <span class="menu-title">公告管理</span>
    </el-menu-item>
    <el-menu-item index="/apply">
      <i class="el-icon-s-promotion menu-icon"></i>
      <span class="menu-title">申请管理</span>
    </el-menu-item>
    
  </el-menu>
</template>
<script>
export default { name: "MenuViewPage" }
</script>
<style scoped>
.el-menu-vertical-demo {
  border-right: none;
  height: 100%;
  background: #304156;
  display: flex;
  flex-direction: column;
  padding-top: 8px;
}
.el-menu-item.is-active {
  background: #263445 !important;
  color: #409EFF !important;
}
.el-menu-item, .el-submenu__title {
  height: 48px !important;
  line-height: 48px !important;
  font-size: 15px;
  display: flex;
  align-items: center;
  padding-left: 24px !important;
  box-sizing: border-box;
}
.el-submenu .el-menu-item {
  padding-left: 48px !important;
}
.menu-icon {
  font-size: 18px;
  margin-right: 10px;
  vertical-align: middle;
  display: inline-block;
}
.menu-title {
  vertical-align: middle;
  display: inline-block;
}
.submenu-title {
  padding-left: 0;
  font-size: 15px;
  color: #bfcbd9;
}
</style>