<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>个人信息</span>
          </div>
          <div class="user-profile">
            <div class="avatar-container">
              <el-avatar :size="100" :src="userInfo.avatar">
                <i class="el-icon-user"></i>
              </el-avatar>
              <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <el-button size="small" style="margin-top: 10px;">更换头像</el-button>
              </el-upload>
            </div>
            <div class="user-info">
              <h3>{{ userInfo.name }}</h3>
              <p>{{ userInfo.role }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>基本信息</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="handleEdit">编辑</el-button>
          </div>
          
          <el-form ref="profileForm" :model="profileForm" :rules="profileRules" label-width="100px">
            <el-form-item label="用户名" prop="account">
              <el-input v-model="profileForm.account" disabled />
            </el-form-item>
            
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" :disabled="!isEditing" />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" :disabled="!isEditing" />
            </el-form-item>
            
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" :disabled="!isEditing" />
            </el-form-item>
            
            <el-form-item label="角色" prop="role">
              <el-input v-model="profileForm.role" disabled />
            </el-form-item>
            
            <el-form-item label="注册时间" prop="createTime">
              <el-input v-model="profileForm.createTime" disabled />
            </el-form-item>
            
            <el-form-item v-if="isEditing">
              <el-button type="primary" @click="handleSave">保存</el-button>
              <el-button @click="handleCancel">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>修改密码</span>
          </div>
          
          <el-form ref="passwordForm" :model="passwordForm" :rules="passwordRules" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>登录日志</span>
          </div>
          
          <el-table :data="loginLogs" style="width: 100%" size="small">
            <el-table-column prop="loginTime" label="登录时间" width="180" />
            <el-table-column prop="ip" label="IP地址" width="120" />
            <el-table-column prop="location" label="登录地点" />
            <el-table-column prop="device" label="设备信息" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'ProfilePage',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      isEditing: false,
      userInfo: {
        name: '管理员',
        role: '超级管理员',
        avatar: ''
      },
      profileForm: {
        account: 'admin',
        nickname: '管理员',
        email: 'admin@example.com',
        phone: '13800138000',
        role: '超级管理员',
        createTime: '2024-01-01 10:00:00'
      },
      profileRules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' }
        ]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      loginLogs: [
        {
          loginTime: '2024-01-15 14:30:25',
          ip: '192.168.1.100',
          location: '北京市',
          device: 'Chrome 120.0.0.0'
        },
        {
          loginTime: '2024-01-14 09:15:10',
          ip: '192.168.1.100',
          location: '北京市',
          device: 'Chrome 120.0.0.0'
        }
      ]
    }
  },
  mounted() {
    this.getUserInfo()
  },
  methods: {
    getUserInfo() {
      // 获取用户信息
      console.log('获取用户信息')
    },
    handleEdit() {
      this.isEditing = true
    },
    handleSave() {
      this.$refs.profileForm.validate((valid) => {
        if (valid) {
          this.$message.success('保存成功')
          this.isEditing = false
        }
      })
    },
    handleCancel() {
      this.isEditing = false
      this.$refs.profileForm.resetFields()
    },
    handleChangePassword() {
      this.$refs.passwordForm.validate((valid) => {
        if (valid) {
          this.$message.success('密码修改成功')
          this.passwordForm = {
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
          }
        }
      })
    },
    handleAvatarSuccess(res, file) {
      this.userInfo.avatar = URL.createObjectURL(file.raw)
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 20px;
  
  .user-profile {
    text-align: center;
    
    .avatar-container {
      margin-bottom: 20px;
    }
    
    .user-info {
      h3 {
        margin: 10px 0 5px 0;
        color: #303133;
      }
      
      p {
        margin: 0;
        color: #909399;
      }
    }
  }
}
</style> 