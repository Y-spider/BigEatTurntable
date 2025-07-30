<template>
  <div class="dish-type-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>菜品类型管理</span>
        <el-button type="primary" size="small" style="float:right;"  icon="el-icon-plus" @click="openAddDialog">新增类型</el-button>
      </div>
      <el-form :inline="true" class="search-form" @submit.native.prevent>
        <el-form-item label="类型名称">
          <el-input v-model="searchName" placeholder="请输入类型名称进行模糊搜索" clearable @input="handleSearch" style="width: 220px;" />
        </el-form-item>
      </el-form>
      <el-table :data="typeList" style="width: 100%; margin-top: 10px;" v-loading="loading" border stripe>
        <el-table-column label="ID" width="80">
          <template v-slot="scope">
          {{ ++scope.$index }}</template>
        </el-table-column>
        <el-table-column prop="name" label="类型名称" />
        <el-table-column prop="typeUrl" label="类型URL" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="修改时间" width="180" align="center">
          <template v-slot="scope">
            {{ scope.row.updateTime || '\\' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button size="mini" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="400px">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="80px">
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="类型URL" prop="name">
          <el-input v-model="form.typeUrl" placeholder="请输入类型URL" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listAllDishType,
  searchDishTypeByName,
  addDishType,
  updateDishType,
  deleteDishType
} from '@/api/dishType'

export default {
  name: 'dishType',
  data() {
    return {
      loading: false,
      typeList: [],
      searchName: '',
      dialogVisible: false,
      dialogTitle: '新增类型',
      form: {
        id: null,
        name: '',
        typeUrl:""
      },
      rules: {
        name: [{ required: true, message: '请输入类型名称', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        let res
        if (this.searchName) {
          res = await searchDishTypeByName(this.searchName)
        } else {
          res = await listAllDishType()
        }
        this.typeList = res.data || []
      } catch (e) {
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.getList()
    },
    openAddDialog() {
      this.dialogTitle = '新增类型'
      this.form = { id: null, name: '' }
      this.dialogVisible = true
    },
    openEditDialog(row) {
      this.dialogTitle = '编辑类型'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确认删除该类型吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await deleteDishType(row.id)
        this.$message.success('删除成功')
        this.getList()
      }).catch(()=>{
        // 取消
      })
    },
    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (valid) {
          if (this.form.id) {
            await updateDishType(this.form)
            this.$message.success('修改成功')
          } else {
            await addDishType(this.form)
            this.$message.success('添加成功')
          }
          this.dialogVisible = false
          this.getList()
        }
      })
    }
  }
}
</script>

<style scoped>
.dish-type-container { padding: 20px; }
.search-form { margin-bottom: 16px; }
</style>