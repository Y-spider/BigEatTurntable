<template>
  <div>
    <el-card>
      <div slot="header">
        <div style="display: flex;">
          <div style="width: 100%;">
            <el-form :inline="true" :model="queryparams" ref="searchForm" class="demo-form-inline">
              <el-form-item label="转盘名称" prop="title">
                <el-input @keyup.enter.native="handleSearch" v-model="queryparams.title" placeholder="请输入转盘名称"></el-input>
              </el-form-item>
              <el-form-item label="转盘类型" prop="type">
                <el-select v-model="queryparams.type" placeholder="选择转盘类型">
                  <el-option label="系统转盘" :value="1"></el-option>
                  <el-option label="自定义转盘" :value="0"></el-option>
                  <el-option label="热门转盘" :value="2"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
                <el-button @click="resetForm('searchForm')" icon="el-icon-refresh">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
          <div>
            <!-- 操作按钮 -->
            <el-button @click="hadleAdd" type="primary" icon="el-icon-plus">新增</el-button>
          </div>
        </div>
      </div>
      <el-table align="center" type="expand" :data="turntableList" style="width: 100%" v-loading="loading">
        <el-table-column type="expand">
          <template slot-scope="props">
            <Turntable :prizeList="props.row.content"></Turntable>
          </template></el-table-column>
        <el-table-column label="ID" width="80">
          <template v-slot="scope">
            {{ ++scope.$index }}</template>
        </el-table-column>
        <el-table-column prop="title" label="转盘名称" />
        <el-table-column prop="type" label="类型">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 0 ? 'primary' : (scope.row.type == 1 ? 'warning' : 'danger')">{{
              scope.row.type == 0 ?
              '用户' : (scope.row.type == 1 ? '系统' : '热门') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNumber" label="排序" align="center" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="updateTime" label="更新时间" />
        <el-table-column label="操作" width="300">
          <template slot-scope="scope">
            <div class="op-btn-group">
              <el-button size="mini" icon="el-icon-view" @click="hadleView(scope.row)">查看</el-button>
              <el-button size="mini" icon="el-icon-edit" :disabled="scope.row.type == 0"
                @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="mini" icon="el-icon-delete" :disabled="scope.row.type == 0" type="danger"
                @click="handleDelete(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination style="margin-top: 16px; text-align: right;" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" :current-page="pageInfo.pageIndex" :page-sizes="[5, 10, 20]"
        :page-size="pageInfo.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="pageInfo.total" />
    </el-card>
    <!-- 查询 修改 新增弹框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%">
      <el-form :model="dialogTurntableInfo">
        <el-form-item label="转盘类型" prop="type">
          <el-select :disabled="dialogType != 'add'" v-model="dialogTurntableInfo.type" placeholder="请选择">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="转盘名称">
          <el-input v-model="dialogTurntableInfo.title" :disabled="dialogType == 'view'"></el-input>
        </el-form-item>
        <el-form-item label="转盘排序">
          <el-input type="number" v-model="dialogTurntableInfo.orderNumber" :disabled="dialogType == 'view'"></el-input>
        </el-form-item>
        <el-form-item v-for="prize, index in prizeList" :key="index" :label="'内容' + (index + 1)">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-input size="mini" :disabled="dialogType == 'view'" v-model="prize.fonts[0].text" placeholder="请输入内容"
                label="内容"></el-input>
            </el-col>
            <el-col :span="6">
              <el-input-number :disabled="dialogType == 'view'" size="mini" v-model="prize.range" :min="0"
                placeholder="请选择概率" label="中奖概率"></el-input-number>
            </el-col>
            <el-col :span="6">
              <el-color-picker size="mini" :disabled="dialogType == 'view'" v-model="prize.background"></el-color-picker>
            </el-col>
            <el-button :disabled="dialogType == 'view'" type="danger" size="mini" icon="el-icon-delete" round
              @click="deletePrize(index)">删除</el-button>
          </el-row>
        </el-form-item>
        <div>
          <el-button :disabled="dialogType == 'view'" type="primary" size="mini" @click="handleAddPrize">添加奖品</el-button>
          <el-button :disabled="dialogType == 'view'" type="primary" size="mini"
            @click="handleAddPrizeWithDishTransfer">菜单添加</el-button>
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible=false">返回</el-button>
        <el-button type="primary" :disabled="dialogType == 'view'" @click="handleConfirm">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 菜品选择穿梭框 -->
    <el-dialog title="选择菜品" :visible.sync="dialogVisibleOfDishTransfer" width="50%">
      <el-transfer style="text-align: left; display: inline-block" @change="handleChange" filterable
        :filter-method="filterMethod" filter-placeholder="请输入菜品名称" v-model="selectedDishList" :data="dishDataList"
        :titles="['待选菜单', '已选菜单']">
        <!-- 类别选择框 -->
        <el-select @change="handleDishTypeSelect" class="transfer-footer" slot="left-footer"
          :filter-method="searchDishType" v-model="selectedDishTypeId" filterable placeholder="请选择菜品类别" :format="{
            noChecked: '${total}',
            hasChecked: '${checked}/${total}'
          }">
          <el-option v-for="dishType in dishTypeOptions" :key="dishType.id" :label="dishType.name" :value="dishType.id">
          </el-option>
        </el-select>
      </el-transfer>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleCloseDialogTranfer">返回</el-button>
        <el-button type="primary" :disabled="dialogType == 'view'" @click="handleConfirmAddDishList">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listPageWithPageQuery, selectById, updateTurntable, deleteTurntableById, addTurntable } from "@/api/turntable"
import { searchDishTypeByName, listAllDishType } from "@/api/dishType"
import { listWithTypeIdAndSearchName } from "@/api/dish"
import Turntable from "@/components/Turntable.vue"
export default {
  name: "TurntablePage",
  components: {
    Turntable
  },
  data() {
    return {
      dishTypeOptions: [], // 获取的菜品种类
      selectedDishTypeId: null,
      dialogVisibleOfDishTransfer: false,
      searchDishName: "",
      selectedDishList: [], // 选中添加的
      dishDataList: [], // 根据菜品分类获取菜品
      tempAddPrizeList: [], // 暂时存储需要保存的菜单转盘信息
      options: [
        { label: "系统", value: 1 },
        { label: "热门", value: 2 },
      ],
      loading: true,
      queryparams: {
        type: "",
        title: ""
      },
      turntableList: [],
      pageInfo: {
        pageIndex: 1,
        pageSize: 10,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: "新增",
      dialogType: "add", // 可能取值为 add edit view
      dialogTurntableInfo: { title: "", type: 2, content: "" },
      prizeList: []
    }
  },
  created() {
    this.init()
  },
  methods: {
    handleCloseDialogTranfer(){
      this.tempAddPrizeList = []
      this.dialogVisibleOfDishTransfer = false
    },
    handleDishTypeSelect(selectId) {
      this.selectedDishTypeId = selectId
      this.initTranferDishList()
    },
    async searchDishType(val) {
      let res = null;
      if (!val) res = await listAllDishType()
      else res = await searchDishTypeByName(val)
      if (!res) return;
      this.dishTypeOptions = res.data
      this.selectedDishTypeId = this.dishTypeOptions[0]?.id
    },
    async handleAddPrizeWithDishTransfer() {
      this.dialogVisibleOfDishTransfer = true
      let res = await listAllDishType()
      if (!res) return
      this.dishTypeOptions = res.data
      this.selectedDishTypeId = this.dishTypeOptions[0]?.id
      this.selectById = this.selectedDishType
      this.initTranferDishList()
    },
    async initTranferDishList() {
      let res = await listWithTypeIdAndSearchName(this.selectedDishTypeId, this.searchDishName)
      if (!res) return;
      // 在拿到数据后做一次映射
      this.dishDataList = res.data.map(item => ({
        key: item.id,      // 把 id → key
        label: item.name,   // 把 name → label
        isMake: item.isMake,
        typeId: item.typeId,
        hot: item.hot
      }))
    },
    handleConfirmAddDishList() {
      console.log("this.prizeList",this.prizeList)
      console.log("this.tempAddPrizeList",this.tempAddPrizeList)

        this.prizeList = [...this.prizeList,...this.tempAddPrizeList]
        this.dialogVisibleOfDishTransfer = false
    },
    filterMethod(val, item) {
      this.searchDishName = val
      return item.label.indexOf(val) > -1;
    },
    handleChange(value, direction, movedKeys) {
      const filtereds = this.dishDataList.filter(item =>
        value.includes(item.key)
      )
      console.log("filtereds",filtereds)
      filtereds.forEach(filtered => {
        let addPrizeObject = {
          fonts: [{
            text: filtered.label,
            top: "10%"
          }],
          background: "#" + Math.floor(Math.random() * 0xFFFFFF).toString(16).padStart(6, '0'),
          lineClamp: 2,
          range: 1,
          isMake: filtered.isMake,
          typeId: filtered.typeId,
          hot: filtered.hot,
          id: filtered.key
        }
        if(direction=="right"){
          // 表现新增
          this.tempAddPrizeList.push(addPrizeObject)
        }
        else{
          // 表示移除
          this.tempAddPrizeList = this.tempAddPrizeList.filter(item => item.id !== filtered.key)
        }
      })
    },
    async handleConfirm() {
      this.dialogTurntableInfo.content = JSON.stringify(this.prizeList)
      if (this.dialogType == 'add') {
        const res = await addTurntable(this.dialogTurntableInfo)
        if (!res) return
        this.$message.success("添加成功")
        this.dialogVisible = false
        this.init()
      } else if (this.dialogType == 'edit') {
        const res = await updateTurntable(this.dialogTurntableInfo)
        if (!res) return
        this.$message.success("修改成功")
        this.dialogVisible = false
        this.init()
      }

    },
    handleAddPrize() {
      const prize = {
        fonts: [{ text: "", top: "10%"}],
        lineClamp: 2,
        background: "#fff",
        range: 1
      }
      prize.background = "#" + Math.floor(Math.random() * 0xFFFFFF).toString(16).padStart(6, '0');
      this.prizeList.push(prize)
    },
    deletePrize(index) {
      this.prizeList.splice(index, 1)
    },
    hadleAdd() {
      this.dialogTitle = "新增转盘"
      this.dialogType = "add"
      this.dialogTurntableInfo.title = ""
      this.dialogTurntableInfo.id = null
      this.dialogTurntableInfo.orderNumber = 0
      this.prizeList = [{ fonts: [{ text: "", top: "10%" }], lineClamp: 2, background: "#409eff", range: 1 }]
      this.dialogVisible = true
    },
    async handleEdit(turntable) {
      this.dialogTitle = "编辑转盘"
      this.dialogType = "edit"
      const res = await selectById(turntable.id)
      if (!res) return
      this.dialogTurntableInfo = res.data
      this.prizeList = JSON.parse(this.dialogTurntableInfo.content)
      this.dialogVisible = true
    },
    async hadleView(turntable) {
      this.dialogTitle = "查看转盘"
      this.dialogType = "view"
      const res = await selectById(turntable.id)
      if (!res) return
      this.dialogTurntableInfo = res.data
      this.prizeList = JSON.parse(this.dialogTurntableInfo.content)
      this.dialogVisible = true
    },
    handleClose() {

    },
    handleCurrentChange(newIndex) {
      this.pageInfo.pageIndex = newIndex
      this.init()
    },
    handleSizeChange(size) {
      this.pageInfo.pageSize = size
      this.init()
    },
    handleDelete(turntbale) {
      this.$confirm(`此操作将永久删除转盘'${turntbale.title}', 是否继续?`, "提示", {
        type: "warning",
      }).then(async () => {
        await deleteTurntableById(turntbale.id)
        this.init()
        this.$message({
          type: 'success',
          message: '删除成功!'
        });
      }).catch((err) => {
        // 取消
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.pageInfo.pageIndex = 1
      this.init()
    },
    async init() {
      this.loading = true
      const data = {
        queryMap: this.queryparams,
        page: this.pageInfo.pageIndex,
        limit: this.pageInfo.pageSize
      }
      let res = await listPageWithPageQuery(data)
      if (!res) return
      this.turntableList = res.data.records
      this.turntableList.forEach((item) => {
        item.content = JSON.parse(item.content)
      })
      this.pageInfo.total = parseInt(res.data.total)
      this.loading = false
    },
    handleSearch() {
      this.pageInfo.pageIndex = 1
      this.init()
    }
  }
}
</script>

<style></style>