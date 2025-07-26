# 吃货大转盘管理系统

基于 Vue2 + Element UI 的后台管理系统

## 功能特性

- 🎯 **大转盘管理**：转盘配置、奖品管理、抽奖记录
- 👥 **用户管理**：用户列表、用户状态管理
- 🔧 **系统管理**：个人中心、密码修改
- 🎨 **现代化UI**：基于 Element UI 的美观界面
- 📱 **响应式设计**：支持多种设备访问
- 🔐 **权限控制**：基于角色的权限管理
- 🌍 **多环境支持**：开发、测试、生产环境配置

## 技术栈

- **前端框架**：Vue 2.6.14
- **UI组件库**：Element UI 2.15.14
- **路由管理**：Vue Router 3.5.1
- **状态管理**：Vuex 3.6.2
- **HTTP客户端**：Axios 1.6.2
- **样式预处理**：Sass
- **构建工具**：Vue CLI 5.0

## 项目结构

```
src/
├── api/                    # API接口
│   ├── user.js            # 用户相关接口
│   └── turntable.js       # 大转盘相关接口
├── components/            # 公共组件
│   ├── Breadcrumb/        # 面包屑导航
│   └── Hamburger/         # 汉堡包菜单
├── layout/                # 布局组件
│   ├── components/        # 布局子组件
│   └── index.vue          # 主布局
├── router/                # 路由配置
│   └── index.js           # 路由主文件
├── store/                 # Vuex状态管理
│   ├── modules/           # 状态模块
│   ├── getters.js         # 全局getters
│   └── index.js           # 状态主文件
├── styles/                # 样式文件
│   ├── variables.scss     # SCSS变量
│   └── index.scss         # 全局样式
├── utils/                 # 工具函数
│   ├── auth.js            # 认证相关
│   ├── request.js         # HTTP请求封装
│   └── validate.js        # 验证工具
├── views/                 # 页面组件
│   ├── dashboard/         # 首页
│   ├── login/             # 登录页
│   ├── turntable/         # 大转盘管理
│   ├── user/              # 用户管理
│   ├── system/            # 系统管理
│   └── error-page/        # 错误页面
├── App.vue                # 根组件
└── main.js                # 入口文件
```

## 环境配置

项目支持多环境配置：

- **开发环境**：`.env.development`
- **测试环境**：`.env.test`
- **生产环境**：`.env.production`

## 安装和运行

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run serve
```

### 构建项目

```bash
# 开发环境构建
npm run build

# 测试环境构建
npm run build:test

# 生产环境构建
npm run build:prod
```

### 代码检查

```bash
npm run lint
```

## 功能模块

### 1. 大转盘管理
- **转盘配置**：设置转盘标题、描述、每日抽奖次数等
- **奖品管理**：添加、编辑、删除奖品，设置中奖概率
- **抽奖记录**：查看用户抽奖历史，管理奖品发放状态

### 2. 用户管理
- **用户列表**：查看所有用户信息，支持搜索和筛选
- **用户操作**：添加、编辑、禁用用户，重置密码

### 3. 系统管理
- **个人中心**：修改个人信息，更换头像
- **密码管理**：修改登录密码
- **登录日志**：查看登录历史记录

## 开发说明

### 路由配置
项目使用静态路由配置，所有路由在 `src/router/index.js` 中定义。

### API接口
所有API接口封装在 `src/api/` 目录下，使用 Axios 进行HTTP请求。

### 状态管理
使用 Vuex 进行状态管理，按模块划分：
- `app`：应用状态（侧边栏、设备类型等）
- `user`：用户状态（登录信息、用户数据等）
- `permission`：权限状态（路由权限等）

### 样式规范
- 使用 SCSS 进行样式开发
- 全局变量定义在 `src/styles/variables.scss`
- 组件样式使用 scoped 作用域

## 部署说明

1. 执行构建命令生成生产文件
2. 将 `dist` 目录下的文件部署到Web服务器
3. 配置服务器支持 history 模式路由

## 浏览器支持

- Chrome >= 60
- Firefox >= 60
- Safari >= 12
- Edge >= 79

## 许可证

MIT License
