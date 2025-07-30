<template>
    <div class="dish-container">
        <el-card>
            <div slot="header" class="clearfix">
                <span>菜品管理</span>
                <div style="display: flex; gap: 15px;">
                    <el-button type="primary" size="small" style="float:right;" icon="el-icon-plus"
                        @click="handleAdd(1)">新增菜品</el-button>
                    <el-button type="primary" size="small" style="float:right;" icon="el-icon-plus"
                        @click="handleAdd(2)">搜索菜品添加</el-button>
                    <el-button type="primary" size="small" style="float:right;" icon="el-icon-plus"
                        @click="handleAdd(3)">按菜品种类添加</el-button>
                </div>
            </div>
            <!-- 搜索栏 -->
            <el-form :inline="true" :model="searchForm" class="search-form" @submit.native.prevent>
                <el-form-item label="菜品名称">
                    <el-input v-model="searchForm.name" placeholder="请输入菜品名称" clearable />
                </el-form-item>
                <el-form-item label="菜品类型">
                    <el-select v-model="searchForm.typeId" placeholder="请选择类型" clearable>
                        <el-option v-for="item in typeOptions" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="制作教程">
                    <el-select v-model="searchForm.isMake" placeholder="请选择" clearable>
                        <el-option v-for="item in makeDishPreperation" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch" icon="el-icon-search">搜索</el-button>
                    <el-button @click="handleReset" icon="el-icon-refresh">重置</el-button>
                </el-form-item>
            </el-form>
            <!-- 表格 -->
            <el-table :data="dishList" style="width: 100%" v-loading="loading">
                <el-table-column label="ID" width="80">
                    <template v-slot="scope">
                        {{ ++scope.$index }}</template>
                </el-table-column>
                <el-table-column prop="name" label="菜品名称" />
                <el-table-column prop="typeId" label="类型">
                    <template slot-scope="scope">
                        {{ getTypeLabel(scope.row.typeId) }}
                    </template>
                </el-table-column>
                <el-table-column prop="isMake" label="有无菜谱">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.isMake ? 'success' : 'danger'">
                            {{ scope.row.isMake ? '有' : '无' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <!-- <el-table-column prop="hot" label="热度" width="80" /> -->
                <el-table-column prop="createTime" label="创建时间" />
                <el-table-column prop="updateTime" label="更新时间" />
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <div class="op-btn-group">
                            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
                            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
                            <el-button v-if="!scope.row.isMake" type="primary" size="mini"
                                @click="goAddTutorial(scope.row)">添加教程</el-button>
                            <el-button v-else size="mini" type="warning" @click="goEditTutorial(scope.row)">修改教程</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页 -->
            <el-pagination style="margin-top: 16px; text-align: right;" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" :current-page="pagination.page" :page-sizes="[5, 10, 20]"
                :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="pagination.total" />
        </el-card>
        <!-- 新增/编辑弹窗 -->
        <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :before-close="handleBeforeClose">
            <el-form v-if="dialogType == 1" v-loading="addLoading" element-loading-text="上传菜品中，请稍等..." ref="dishForm"
                :model="dishForm" :rules="dishRules" label-width="100px">
                <el-form-item label="菜品名称" prop="name">
                    <el-input v-model="dishForm.name" placeholder="请输入菜品名称" />
                </el-form-item>
                <el-form-item label="类型" prop="typeId">
                    <el-select v-model="dishForm.typeId" placeholder="请选择类型">
                        <el-option v-for="item in typeOptions" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="有无菜谱" prop="isMake">
                    <el-switch v-model="dishForm.isMake" active-text="有" inactive-text="无" />
                </el-form-item>
                <el-form-item label="热度" prop="hot">
                    <el-input-number v-model="dishForm.hot" :min="0" />
                </el-form-item>
                <el-form-item v-if="dishForm.isMake" label="菜品制作教程URL" prop="makeUrl">
                    <el-input v-model="dishForm.makeUrl" placeholder="支持https://www.xiachufang.com 网站菜品制作页面详情url" />
                </el-form-item>
            </el-form>
            <el-form v-if="dialogType == 2" v-loading="loading" element-loading-text="搜索菜品上传中，请稍等...">
                <el-form-item label="搜索菜品名称" prop="searchName">
                    <el-input v-model="dishForm.searchName" placeholder="请输入搜索菜品名称" />
                </el-form-item>
                <el-form-item label="搜索数量" prop="count">
                    <el-input-number v-model="dishForm.count" :min="1" :max="999"></el-input-number>
                </el-form-item>
                <el-form-item label="搜索起始页" prop="startPage">
                    <el-input-number v-model="dishForm.startPage" :min="1" :max="999"></el-input-number>
                </el-form-item>
                <el-form-item label="搜索结束页" prop="endPage">
                    <el-input-number v-model="dishForm.endPage" :min="1" :max="999"></el-input-number>
                </el-form-item>
            </el-form>
            <el-form v-if="dialogType == 3" v-loading="loading" element-loading-text="菜品上传中，请稍等...">
                <el-form-item label="请输入分类名称" prop="typeName">
                    <el-select allow-create @change="handleChange" v-model="dishForm.typeName" filterable remote reserve-keyword placeholder="请输入菜品类别"
                        :remote-method="remoteMethod" :loading="loading">
                        <el-option v-for="item in options" :key="item.id" :label="item.name" :value="item.name">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="菜品分类URL" prop="typeUrl">
                    <el-input v-model="dishForm.typeUrl" placeholder="请输入菜品类别URL(参考下厨房官网)" />
                </el-form-item>
                <el-form-item label="搜索数量" prop="count">
                    <el-input-number v-model="dishForm.count" :min="1" :max="999"></el-input-number>
                </el-form-item>
                <el-form-item label="搜索起始页" prop="startPage">
                    <el-input-number v-model="dishForm.startPage" :min="1" :max="999"></el-input-number>
                </el-form-item>
                <el-form-item label="搜索结束页" prop="endPage">
                    <el-input-number v-model="dishForm.endPage" :min="1" :max="999"></el-input-number>
                </el-form-item>
            </el-form>
            <div v-if="uploadResultShow" style="display:flex;flex-direction:column;gap:10px;">
                <div v-for="dishName, index in searchComplateList" :key="index" class="text item">
                    {{ (index + 1) + '.' + dishName }}===>上传成功
                </div>
            </div>

            <div slot="footer" class="dialog-footer">
                <el-button @click="handleBeforeClose">取消</el-button>
                <el-button type="primary" @click="handleSubmit">保存</el-button>
            </div>

        </el-dialog>
    </div>
</template>

<script>
/* eslint-disable */
import { listDishByPage, getDishType, deleteDishByIds, addDishWithMakeUrl, addDishBySearchName, addDishBySearchType } from '@/api/dish'
import { searchDishTypeByName } from '@/api/dishType'

export default {
    name: "DishPage",
    data() {
        return {
            addLoading: false,
            dialogType: 1, // 弹框类型
            loading: false,
            searchForm: {
                name: '',
                typeId: '',
                isMake: ''
            },
            typeOptions: [
                { id: 1, name: '热菜' },
            ],
            makeDishPreperation: [
                { id: 1, name: "有" },
                { id: 0, name: "无" }
            ],
            dishList: [],
            pagination: {
                page: 1,
                pageSize: 5,
                total: 0
            },
            dialogVisible: false,
            dialogTitle: '新增菜品',
            dishForm: {
                id: null,
                name: '',
                typeId: '',
                isMake: false,
                hot: 0,
                makeId: '',
                searchType: "",
                typeUrl: "",
                count: 1,
                startPage: 2,
                endPage: 5
            },
            dishRules: {
                name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
                typeId: [{ required: true, message: '请选择类型', trigger: 'change' }]
            },
            searchComplateList: [], //上传成功菜品回调显示 
            uploadResultShow: false,
            webSocket: null,
            options: []
        }
    },
    mounted() {
        this.getDishList()
        this.getDishTypeList()
    },
    methods: {
        handleChange(name){
            console.log("value",this.options.filter(item => item.name===name))
           this.dishForm.typeUrl =  this.options.filter(item => item.name===name)[0]?.typeUrl
        },
        async remoteMethod(inputVal) {
            // 远程搜索
            if (inputVal == "" || inputVal == undefined) return
            let res = await searchDishTypeByName(inputVal)
            if (!res) return;
            this.options = res.data
        },
        getTypeLabel(typeId) {
            const t = this.typeOptions.find(t => t.id === typeId)
            return t ? t.name : ''
        },
        async getDishTypeList() {
            // 获取菜品类别
            let res = await getDishType()
            if (!res) return
            this.typeOptions = res.data
        },
        async getDishList() {
            this.loading = true
            try {
                const params = {
                    page: this.pagination.page,
                    limit: this.pagination.pageSize,
                    queryMap: {
                        name: this.searchForm.name,
                        type: this.searchForm.typeId,
                        isMake: this.searchForm.isMake
                    }
                }
                const res = await listDishByPage(params)
                // 假设后端返回 { data: { records: [], total: 0 } }
                this.dishList = res.data?.records || []
                this.pagination.total = parseInt(res.data?.total) || 0
            } catch (e) {
                this.$message.error('获取数据失败')
            } finally {
                this.loading = false
            }
        },
        handleSearch() {
            this.pagination.page = 1
            this.getDishList()
        },
        handleReset() {
            this.searchForm = { name: '', typeId: '', isMake: '' }
            this.handleSearch()
        },
        handleSizeChange(val) {
            this.pagination.pageSize = val
            this.getDishList()
        },
        handleCurrentChange(val) {
            this.pagination.page = val
            this.getDishList()
        },
        handleAdd(type) {
            this.dialogType = type
            if (this.dialogType == 1) {
                this.dialogTitle = '新增菜品'
                this.dishForm = { id: null, name: '', typeId: '', isMake: false, hot: 0, makeId: '', makeUrl: '' }
                this.dialogVisible = true
            }
            else if (this.dialogType == 2) {
                this.initWebSocket()
                this.dialogTitle = "搜索菜品添加"
                this.dishForm = { searchName: "", count: 1, startPage: 1, endPage: 5 }
                this.uploadResultShow = false
                this.searchComplateList = []
                this.dialogVisible = true
            }
            else if (this.dialogType == 3) {
                this.initWebSocket()
                this.dialogTitle = '菜品类型URL添加'
                this.dialogVisible = true
            }
        },
        async handleEdit(row) {
            this.dialogVisible = true
            this.dialogType = 3
            this.dishForm = { ...row }
        },
        handleDelete(row) {
            this.$confirm('确认删除该菜品吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(async () => {
                await deleteDishByIds([row.id])
                this.$message.success('删除成功')
                this.getDishList()
            }).catch(() => {

            })
        },
        async handleSubmit() {
            this.addLoading = true
            if (this.dialogType == 1) {
                this.$refs.dishForm.validate(async valid => {
                    if (valid) {
                        // 新增和编辑都用 updateDish，实际可区分接口
                        let res = await addDishWithMakeUrl(this.dishForm)
                        if (!res) {
                            this.dialogVisible = false
                            return
                        }
                        this.$message.success('保存成功')
                        this.getDishList()
                        this.addLoading = false
                        this.dialogVisible = false
                    }
                })
            }
            else if (this.dialogType == 2) {
                this.loading = true
                // 名称搜索
                this.uploadResultShow = true
                const res = await addDishBySearchName(this.dishForm)
                if (!res) {
                    this.loading = false
                    return
                }
                this.getDishList()
                this.$message.success('上传成功')
                this.loading = false
            }
            else if (this.dialogType == 3) {
                this.uploadResultShow = true
                this.loading = true
                const res = await addDishBySearchType(this.dishForm)
                if (!res) {
                    this.loading = false
                    return
                }
                this.getDishList()
                this.$message.success("上传成功")
                this.loading = false
            }
        },
        goAddTutorial(row) {
            this.$router.push({ name: 'AddDishTutorial', params: { id: row.id, dishName: row.name } })
        },
        goEditTutorial(row) {
            this.$router.push({ name: 'EditDishTutorial', params: { id: row.id, isEdit: true } })
        },
        initWebSocket() {
            const ws = new WebSocket(`ws://127.0.0.1:16378/websocket?sid=17760580731`)
            if (ws) {
                this.webSocket = ws
                this.webSocket.onopen = this.onWebSocketOpen; // WebSocket连接打开时的处理函数
                this.webSocket.onmessage = this.onWebSocketMessage; // 收到WebSocket消息时的处理函数
                this.webSocket.onclose = this.onWebSocketClose; // WebSocket连接关闭时的处理函数
                this.webSocket.onerror = this.onWebsocketError; // websocket出错时调用
            } else {
                this.$message.warning("websocket连接失败,但不影响上传")
            }

        },
        onWebsocketError(err) {
            console.log("链接出错", err)
        },
        closeWebsocket() {
            // 主动关闭websocket
            if (this.webSocket && this.webSocket.readyState === WebSocket.OPEN) {
                this.webSocket.close(1000, "client close") // ✅ 加括号,通知服务器关闭连接
            }
        },
        onWebSocketOpen() {
            console.log("链接成功");
        },
        onWebSocketClose() {
            console.log("连接断开...")
        },
        onWebSocketMessage(event) {
            if (event.data == "conn_success") return
            this.searchComplateList.push(event.data)
        },
        handleBeforeClose() {
            this.dialogVisible = false
            this.loading = false
            this.closeWebsocket()

        }
    }
}
</script>

<style scoped>
.dish-container {
    padding: 20px;
}

.search-form {
    margin-bottom: 16px;
}

.op-btn-group {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    align-items: center;
}
</style>