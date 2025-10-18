<template>
  <div class="tutorial-container">
    <el-card v-loading="loading">
      <div slot="header" class="tutorial-header">
        <span class="tutorial-title">{{ isEdit ? '编辑' : '新增' }}菜品教程</span>
      </div>
      <div style="display: flex;">
        <el-button size="mini" type="primary" @click="openUrlDialog">地址上传</el-button>
      </div>
      <el-form :model="form" ref="formRef" label-width="100px" :rules="rules" class="tutorial-form">
        <el-row :gutter="32">
          <el-col :span="12">
            <el-form-item label="菜品名称">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜品描述" prop="desc">
              <el-input type="textarea" v-model="form.desc" :autosize="{ minRows: 2, maxRows: 6 }"
                placeholder="请输入菜品描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 用料表 -->
        <el-form-item label="用料">
          <div style="display: flex;justify-content: space-evenly;">
            <div class="ingredient-table-wrapper ingredient-table-full" style="width: 50%;">
              <el-table :data="form.ingredients" border class="ingredient-table">
                <el-table-column prop="name" label="名称">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.name" placeholder="如苦菊" />
                  </template>
                </el-table-column>
                <el-table-column prop="amount" label="用量">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.amount" placeholder="如1把" />
                  </template>
                </el-table-column>
                <el-table-column label="操作">
                  <template slot-scope="scope">
                    <el-button type="danger" size="mini" icon="el-icon-delete" circle
                      @click="removeIngredient(scope.$index)" />
                  </template>
                </el-table-column>
              </el-table>
              <div style="text-align:right;margin-top:8px;">
                <el-button type="primary" size="mini" @click="addIngredient">添加用料</el-button>
              </div>
            </div>
            <div style="width: 100%;display: flex;flex-direction: column;">
              <el-form-item label="友情提示" prop="tips" style="flex: 1;">
                <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 6 }" v-model="form.tips"
                  placeholder="贴心小提示" />
              </el-form-item>
              <el-form-item label="菜品封面" prop="tips" style="flex: 1;">
                <el-upload class="avatar-uploader" :action="uploadUrl"
                  :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeStepImgUpload">
                  <img v-if="form.coverUrl" :src="form.coverUrl" class="avatar" style="width: 128px; height: 128px;">
                  <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
              </el-form-item>
            </div>
          </div>
        </el-form-item>
        <!-- 步骤 -->
        <el-form-item label="操作步骤">
          <draggable v-model="form.steps" handle=".drag-handle" animation="200" class="step-list">
            <transition-group>
              <div v-for="(step, idx) in form.steps" :key="idx" class="step-item"
                :class="{ 'step-item-highlight': step._hover }">
                <i class="el-icon-rank drag-handle" title="拖拽排序"></i>
                <el-input type="textarea" v-model="step.desc" :placeholder="`步骤${idx + 1}描述`" rows="2" class="step-desc"
                  @focus="step._hover = true" @blur="step._hover = false" />
                <div class="step-img-upload-group">
                  <el-upload class="step-upload" :action="uploadUrl" :show-file-list="false"
                    :on-success="(res, file) => handleStepImgSuccess(res, file, idx)"
                    :before-upload="beforeStepImgUpload">
                    <div class="step-img-wrapper">
                      <img v-if="step.imgUrl" :src="step.imgUrl" class="step-img-thumb" />
                      <el-button v-else size="mini" icon="el-icon-plus">上传图片</el-button>
                    </div>
                  </el-upload>
                </div>
                <el-button type="danger" size="mini" icon="el-icon-delete" circle class="step-delete-btn"
                  @click="removeStep(idx)" />
              </div>
            </transition-group>
          </draggable>
          <el-button type="primary" size="mini" @click="addStep" style="margin-top:8px;">添加步骤</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
      <!-- 地址上传弹窗 -->
      <el-dialog title="菜品地址" :visible.sync="urlDialogVisible" width="400px">
        <el-input v-model="urlInput" placeholder="支持https://www.xiachufang.com 网站" />
        <div slot="footer" class="dialog-footer">
          <el-button @click="urlDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUrlUpload">确定</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { getDishMake, updateDishMake, addDishMake } from '@/api/dish'
import draggable from 'vuedraggable'
export default {
  name: 'DishTutorial',
  components: { draggable },
  data() {
    return {
      id: null,
      form: {
        name: '',
        desc: '',
        ingredients: [],
        steps: []
      },
      dishMake: {
        id: null,
        dishId: null,
        content: null,
        lookCount: 0
      },
      isEdit: true,
      rules: {
        // desc: [{ required: true, message: '请输入菜品描述', trigger: 'blur' }]
      },
      uploadUrl: 'https://www.sunnygo.chat/turntable/api/file/upload',
      urlDialogVisible: false,
      urlInput: '',
      urlStepIdx: null,
      loading: true,
    }
  },
  created() {
    this.id = parseInt(this.$route.params.id)
    this.form.name = this.$route.params.dishName
    this.isEdit = this.$route.params.isEdit
    if (this.isEdit) {
      this.fetchDetail()
    } else {
      this.dishMake.dishId = this.id
    }
  },
  methods: {
    handleAvatarSuccess(res){
      this.form.coverUrl = res.data.url
    },
    async fetchDetail() {
      // 请求接口获取菜品制作信息
      this.loading = true
      const res = await getDishMake(this.id).finally(()=>this.loading = false)
      if(res.data?.content){
        this.form = JSON.parse(res.data.content)
       this.dishMake = res.data   
      };
    
    },
    addIngredient() {
      this.form.ingredients.push({ category: '', name: '', amount: '' })
    },
    removeIngredient(idx) {
      this.form.ingredients.splice(idx, 1)
    },
    addStep() {
      this.form.steps.push({ desc: '', imgUrl: '' })
    },
    removeStep(idx) {
      this.form.steps.splice(idx, 1)
    },
    handleStepImgSuccess(res, file, idx) {
      this.form.steps[idx].imgUrl = res.data.url || URL.createObjectURL(file.raw)
    },
    beforeStepImgUpload(file) {
      const isImg = file.type.startsWith('image/')
      if (!isImg) this.$message.error('只能上传图片')
      return isImg
    },
    openUrlDialog() {
      this.urlInput = ''
      this.urlDialogVisible = true
    },
    handleUrlUpload() {
      if (!/^https?:\/\//.test(this.urlInput)) {
        this.$message.error('请输入合法的图片URL')
        return
      }
      this.form.steps[this.urlStepIdx].imgUrl = this.urlInput
      this.urlDialogVisible = false
    },
    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (valid) {
          this.dishMake.content = JSON.stringify(this.form)
          let tipTitle = ""
          if (!this.isEdit) {
            let res = await addDishMake(this.dishMake)
            if (!res) return
            tipTitle = "新增成功"
          }
          else {
            let res = await updateDishMake(this.dishMake)
            if (!res) return
            tipTitle = "修改成功"
          }
          this.$message.success(tipTitle)
          this.$router.back()
        }
      })
    }
  }
}
</script>

<style scoped>
.tutorial-container {
  padding: 24px;
  background: #f7f8fa;
  min-height: 100vh;
}

.tutorial-header {
  padding: 8px 0;
}

.tutorial-title {
  font-size: 22px;
  font-weight: bold;
  color: #bfa76a;
}

.tutorial-form {
  background: #fff;
  border-radius: 8px;
  padding: 24px 0;
}

.ingredient-table-wrapper {
  background: #fafbfc;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
  padding: 16px;
}

.ingredient-table-full {
  width: 100%;
}

.ingredient-table {
  margin-bottom: 0;
}

.step-list {
  margin-bottom: 8px;
}

.step-item {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  background: #f9f9f9;
  border-radius: 6px;
  padding: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
  transition: box-shadow 0.2s;
}

.step-item-highlight {
  box-shadow: 0 2px 8px rgba(255, 0, 0, 0.12);
  border: 1px solid #ff4d4f;
}

.step-desc {
  width: 60%;
  margin-right: 12px;
}

.step-img-upload-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 10px;
}

.step-img-wrapper {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #eee;
  overflow: hidden;
  margin-bottom: 4px;
}

.step-img-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.step-upload {
  display: inline-block;
}

.step-url-btn {
  margin-top: 2px;
  padding: 0 8px;
}

.drag-handle {
  cursor: move;
  color: #bfa76a;
  font-size: 20px;
  margin-right: 10px;
}

.step-delete-btn {
  margin-left: 10px;
}
</style> 