<template>
  <div class="notice-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>通知管理</span>
        <el-button type="primary" size="small" style="float:right;" @click="openAddDialog">新增通知</el-button>
      </div>
      <el-table :data="noticeList" style="width: 100%; margin-top: 10px;" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80">
          <template v-slot="scope">
            {{ ++scope.$index }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="通知内容">
          <template slot-scope="scope">
            <div v-html="scope.row.content" class="notice-content-preview"></div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="修改时间" width="180" />
        <el-table-column prop="active" label="是否生效" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.active ? 'success' : 'info'">{{ scope.row.active ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button size="mini" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDeleteNotice(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top: 16px; text-align: right;" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" :current-page="pagination.page" :page-sizes="[5, 10, 20]"
        :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="pagination.total" />
    </el-card>
    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="80px">
        <el-form-item label="通知内容" prop="content">
          <VueEditor ref="myEditor" useCustomImageHandler @image-added="handleImageAdded" v-model="form.content"
            :editorToolbar="customToolbar"></VueEditor>
        </el-form-item>
        <el-form-item label="是否生效" prop="active">
          <el-switch v-model="form.active" active-text="是" inactive-text="否" />
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
import { listNoticeByPage, addNotice, updateNotice, deleteNotive } from '@/api/notice'
import { VueEditor } from 'vue2-editor'
import { uploadFile } from "@/api/common"
export default {
  name: 'NoticePage',
  components: { VueEditor },
  data() {
    return {
      customToolbar: [
        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        [{ 'font': [] }],
        [{ 'size': ['small', false, 'large', 'huge'] }],
        ['bold', 'italic', 'underline', 'strike'],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'script': 'sub' }, { 'script': 'super' }],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        [{ 'indent': '-1' }, { 'indent': '+1' }],
        [{ 'direction': 'rtl' }],
        [{ 'align': [] }],
        ['blockquote', 'code-block'],
        ['link', 'image', 'video', 'formula'],
        ['clean']
      ],
      loading: false,
      noticeList: [],
      pagination: {
        page: 1,
        pageSize: 10,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '新增通知',
      form: {
        content: '',
        active: false
      },
      rules: {
        content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }]
      },
      tinymceInit: {
        height: 300,
        menubar: false,
        branding: false,
        promotion: false,
        // 关键：全部指向刚才复制出来的 public/tinymce
        base_url: '/tinymce',                       // 指向 public/tinymce
        skin_url: '/tinymce/skins/ui/oxide',
        content_css: '/tinymce/skins/content/default/content.css',
        // 中文包
        language: 'zh_CN',
        language_url: '/tinymce/langs/zh_CN.js',
        // 插件/工具栏按需写
        plugins: 'lists link image table code',
        toolbar: 'undo redo | bold italic | alignleft aligncenter alignright | code',
        toolbar_mode: 'wrap',
      }
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    handleDeleteNotice(notice) {
      this.$confirm('确定删除所选通知？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const res = await deleteNotive(notice.id);
        if (!res) return;
        this.$message.success("删除成功");
        this.getList();
      }).catch(() => {
      });

    },
    insertText(url) {
      // 插入文本到 
      const quill = this.$refs.myEditor.quill
      const range = quill.getSelection()
      if (!range) {
        // 如果编辑器还没 focus，默认插到文末
        quill.focus()
        quill.insertEmbed(quill.getLength(), "image", url)
      } else {
        // 3. 在光标处插入
        quill.insertEmbed(range.index, "image", url)
        // 4. （可选）把光标挪到新内容的后面
        quill.setSelection(range.index + url.length)
      }
    },
    async handleImageAdded(file, Editor, cursorLocation, resetUploader) {
      const formData = new FormData()
      formData.append('file', file)   // 字段名 file，后端用同名接收
      // 2. axios 发 POST，Content-Type 由浏览器自动设为 multipart/form-data
      const res = await uploadFile(formData)
      if (!res) return
      // this.insertText(`<img src="${res.data.url}" >`)
      this.insertText(res.data.url)

    },
    async getList() {
      this.loading = true
      try {
        const res = await listNoticeByPage({
          page: this.pagination.page,
          limit: this.pagination.pageSize
        })
        this.noticeList = res.data?.records || []
        this.pagination.total = parseInt(res.data?.total) || 0
      } catch (e) {
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.pagination.page = val
      this.getList()
    },
    openAddDialog() {
      this.dialogTitle = '新增通知'
      this.form = { content: '', active: false }
      this.dialogVisible = true
    },
    openEditDialog(row) {
      this.dialogTitle = '编辑通知'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (valid) {
          if (this.form.id) {
            const res = await updateNotice(this.form)
            if (!res) return
            this.$message.success('修改成功')
          } else {
            console.log("form", this.form)
            const res = await addNotice(this.form)
            if (!res) return
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
.notice-container {
  padding: 20px;
}

.notice-content-preview {
  max-width: 300px;
  max-height: 60px;
  overflow: auto;
}
</style>