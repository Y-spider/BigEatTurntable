<template>
    <div class="notice-container">
        <el-card>
            <div slot="header" class="clearfix">
                <span>申请管理</span>
            </div>
            <el-form :inline="true" class="search-form" @submit.native.prevent>
                <el-form-item label="申请状态">
                    <el-select @change="handleOptionChange" v-model="searchStatus" placeholder="请选择状态" clearable>
                        <el-option v-for="item in statusOptions" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-form>
            <el-table :data="applyList" style="width: 100%; margin-top: 10px;" v-loading="loading" border>
                <el-table-column prop="id" label="ID" width="80">
                    <template v-slot="scope">
                        {{ ++scope.$index }}
                    </template>
                </el-table-column>
                <el-table-column prop="content" label="菜品名称">
                    <template slot-scope="scope">
                        <div v-html="scope.row.dishName" class="notice-content-preview"></div>
                    </template>
                </el-table-column>
                <el-table-column prop="content" label="上传菜名">
                    <template slot-scope="scope">
                        <div v-html="scope.row.applyDishName" class="notice-content-preview"></div>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" />
                <el-table-column prop="updateTime" label="修改时间" width="180" />
                <el-table-column prop="remark" label="备注" width="180" />

                <el-table-column prop="active" label="状态" width="100">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.status == '待处理'" type="warning">{{ scope.row.status }}</el-tag>
                        <el-tag v-if="scope.row.status == '未通过'" type="danger">{{ scope.row.status }}</el-tag>
                        <el-tag v-if="scope.row.status == '通过'" type="success">{{ scope.row.status }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180">
                    <template slot-scope="scope">
                        <el-button :disabled="scope.row.status == '通过'" size="mini"
                            @click="openEditDialog(scope.row)">审核</el-button>
                        <el-button :disabled="!(scope.row.status == '待处理')" size="mini" type="danger"
                            @click="handleDeleteNotice(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination style="margin-top: 16px; text-align: right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="pagination.page" :page-sizes="[5, 10, 20]"
                :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="pagination.total" />
        </el-card>
        <!-- 新增/编辑弹窗 -->
        <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px">
            <el-form :model="form" ref="formRef" :rules="rules" v-loading="loading" label-width="160px">
                <el-form-item label="菜品名称" prop="dsihName">
                    <el-input v-model="form.dishName" disabled></el-input>
                </el-form-item>

                <el-form-item label="菜品类型" prop="dishTypeId">
                    <el-select @change="handleOptionChange" v-model="form.dishTypeId" placeholder="请选择菜品类型" clearable>
                        <el-option v-for="item in dishTypeOptions" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>

                <el-form-item label="审核状态" prop="status">
                    <el-select @change="handleOptionChange" v-model="form.status" placeholder="请选择状态" clearable>
                        <el-option v-for="item in statusOptions" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="是否生成审核日志" prop="generateLog">
                    <el-switch v-model="form.generateLog" active-text="是" inactive-text="否" />
                </el-form-item>
                <el-form-item label="备注信息" prop="remark">
                    <el-input type="textarea" v-model="form.remark"></el-input>
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
import { listDishApplyList, reviewApplyDish, deleteApplyDish } from "@/api/apply";
import { listAllDishType } from "@/api/dishType";
export default {
    name: 'NoticePage',
    data() {
        return {
            statusOptions: [{ id: "待处理", name: "待处理" }, { id: "通过", name: "通过" }, { id: "未通过", name: "未通过" }],
            searchStatus: '', // 过滤状态
            loading: false,
            applyList: [],
            pagination: {
                page: 1,
                pageSize: 10,
                total: 0
            },
            dialogVisible: false,
            dialogTitle: '',
            form: {
                dishTypeId: '',
                remark: '',
                generateLog: true
            },
            rules: {},
            dishTypeOptions: []
        }
    },
    async mounted() {
        this.getList();
        const res = await listAllDishType();
        if (!res) return;
        this.dishTypeOptions = res.data;
    },
    methods: {
        handleDeleteNotice(record) {
            this.$confirm('确定删除所选申请？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(async () => {
                const res = await deleteApplyDish(record.id);
                if (!res) return;
                this.$message.success("删除成功");
                this.getList();
            }).catch(() => {
            });

        },
        handleOptionChange() {
            this.getList();
        },
        async getList() {
            this.loading = true
            try {
                const res = await listDishApplyList({
                    page: this.pagination.page,
                    limit: this.pagination.pageSize,
                    queryMap: {
                        searchStatus: this.searchStatus
                    }
                })
                this.applyList = res.data?.records || []
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
        openEditDialog(row) {
            this.dialogTitle = '审核'
            this.form = {
                dishTypeId: '',
                remark: '',
                generateLog: true, // 默认值
                ...row
            }
            this.dialogVisible = true
        },
        handleSubmit() {
            this.$refs.formRef.validate(async valid => {
                if (valid) {
                    this.loading = true;
                    const res = await reviewApplyDish(this.form)
                    if (!res) return
                    this.$message.success('操作成功')
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