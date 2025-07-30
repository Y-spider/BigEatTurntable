<template>
  <div class="turntable-config">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>大转盘配置</span>
      </div>
      
      <el-form ref="configForm" :model="configForm" :rules="configRules" label-width="120px">
        <el-form-item label="转盘标题" prop="title">
          <el-input v-model="configForm.title" placeholder="请输入转盘标题" />
        </el-form-item>
        
        <el-form-item label="转盘描述" prop="description">
          <el-input v-model="configForm.description" type="textarea" :rows="3" placeholder="请输入转盘描述" />
        </el-form-item>
        
        <el-form-item label="每日抽奖次数" prop="dailyLimit">
          <el-input-number v-model="configForm.dailyLimit" :min="1" :max="100" />
        </el-form-item>
        
        <el-form-item label="转盘状态" prop="status">
          <el-radio-group v-model="configForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="背景图片" prop="backgroundImage">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :on-success="handleBackgroundSuccess"
            :before-upload="beforeBackgroundUpload"
          >
            <img v-if="configForm.backgroundImage" :src="configForm.backgroundImage" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="转盘颜色" prop="colors">
          <el-color-picker v-model="configForm.colors.primary" />
          <el-color-picker v-model="configForm.colors.secondary" />
          <el-color-picker v-model="configForm.colors.accent" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="saveConfig">保存配置</el-button>
          <el-button @click="resetConfig">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'TurntableConfig',
  data() {
    return {
      configForm: {
        title: '吃货大转盘',
        description: '欢迎参与我们的抽奖活动！',
        dailyLimit: 3,
        status: 1,
        backgroundImage: '',
        colors: {
          primary: '#409EFF',
          secondary: '#67C23A',
          accent: '#E6A23C'
        }
      },
      configRules: {
        title: [
          { required: true, message: '请输入转盘标题', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入转盘描述', trigger: 'blur' }
        ],
        dailyLimit: [
          { required: true, message: '请设置每日抽奖次数', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getConfig()
  },
  methods: {
    getConfig() {
      // 获取配置信息
      console.log('获取转盘配置')
    },
    saveConfig() {
      this.$refs.configForm.validate((valid) => {
        if (valid) {
          this.$message.success('配置保存成功')
        }
      })
    },
    resetConfig() {
      this.$refs.configForm.resetFields()
    },
    handleBackgroundSuccess(res, file) {
      this.configForm.backgroundImage = URL.createObjectURL(file.raw)
    },
    beforeBackgroundUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传背景图片只能是 JPG/PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传背景图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    }
  }
}
</script>

<style lang="scss" scoped>
.turntable-config {
  padding: 20px;
  
  .avatar-uploader {
    .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      
      &:hover {
        border-color: #409EFF;
      }
    }
  }
  
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
}
</style> 