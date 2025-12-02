package top.chopper.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.chopper.Exception.BusinessException;
import top.chopper.dto.QueryPageDto;
import top.chopper.pojo.BillBook;
import top.chopper.pojo.BillRecord;
import top.chopper.pojo.R;
import top.chopper.service.BillBookService;
import top.chopper.service.BillRecordService;
import top.chopper.service.UserService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:50
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/book/record")
@Tag(description = "账单明细处理器",name = "BillRecordController")
@Slf4j
public class BillRecordController {
    @Autowired
    private BillRecordService billRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private BillBookService billBookService;


    @PostMapping("/add")
    @Operation(description = "新增消费记录",summary = "新增消费记录")
    public R handleSave(@RequestBody BillRecord billRecord){
        BillBook currentChooseBookBill = billBookService.getCurrentChooseBookBill();
        billRecord.setBillBookId(BillBook.getIdByCurrentBillBook(currentChooseBookBill));
        billRecord.setName(userService.getCurrentUser().getName());
        billRecord.setOpenid(SecurityUtil.getUserName());
        LocalDateTime now = LocalDateTime.now();
        billRecord.setCreateTime(now);
        billRecord.setUpdateTime(now);
        billRecordService.save(billRecord);
        return R.SUCCESS();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(description = "删除消费记录",summary = "删除消费记录")
    @Transactional
    public R handleDelete(@PathVariable("id") Integer id){
        List<BillRecord> billRecords = billRecordService.listByIds(List.of(id));
        if(billRecords.isEmpty()){
            throw new BusinessException("系统错误，请稍后重试!!");
        }
        billRecordService.removeById(id);
        return R.SUCCESS();
    }

    @PutMapping("/update")
    @Transactional
    @Operation(description = "修改消费记录（好友之间不可以相互修改）",summary = "新增消费记录(好友之间可以相互修改)")
    public R modifyBillRecord(@RequestBody BillRecord billRecord){
        if(!Objects.equals(billRecord.getOpenid(), SecurityUtil.getUserName())){
            throw new BusinessException("无权限修改!");
        }
        billRecord.setUpdateTime(LocalDateTime.now());
        billRecord.setUpdateOpenid(SecurityUtil.getUserName());
        billRecord.setOpenid(null);
        billRecordService.updateById(billRecord);
        return R.SUCCESS();
    }

    // 参数比较多
    @PostMapping("/list")
    @Operation(description = "分页获取账单记录",summary = "分页获取账单记录")
    public R listWithPage(@RequestBody QueryPageDto queryPageDto){
      return R.SUCCESS(billRecordService.listWithPageAndCondition(queryPageDto));
    }


    @GetMapping("/query/{id}")
    @Operation(description = "根据ID获取账单信息",summary = "根据ID获取账单信息")
    public R handleQueryBillRecord(@PathVariable("id") Integer id){
        BillRecord billRecord = billRecordService.getById(id);
        if(StrUtil.equals(billRecord.getOpenid(),SecurityUtil.getUserName())){
            billRecord.setIsEdit(true);
        }else{
            billRecord.setIsEdit(false);
        }
        return R.SUCCESS(billRecord);
    }


    @GetMapping("/summary/{time}/{userId}")
    @Operation(description = "月份数据统计，按照账本+月份维度",summary = "月份数据统计，按照账本+月份维度")
    public R handleSummaryMonth(@PathVariable("time") LocalDateTime time,@PathVariable(value = "userId") Integer userId){
        return R.SUCCESS(billRecordService.handleSummaryMonth(time,userId));
    }

    @GetMapping("/list/month/{time}/{userId}")
    @Operation(description = "按照月份来获取当月的消费记录，其中按照天来划分",summary = "按照月份来获取当月的消费记录，其中按照天来划分")
    public R handleListWithMonth(@PathVariable("time") LocalDateTime time,@PathVariable(value = "userId") Integer userId){
        return R.SUCCESS(billRecordService.listWithMonth(time,userId));
    }

    @PostMapping("/merge/{fromId}/{toId}")
    @Operation(description = "将from账本记录导入到to目标账本中",summary = "将from账本记录导入到to目标账本中")
    public R handleMerge(@PathVariable("fromId") Integer fromId,@PathVariable("toId") Integer toId){
        billRecordService.handleMergeBook(fromId,toId);
        return R.SUCCESS();
    }

    @PostMapping("/search")
    @Operation(description = "处理检索请求",summary = "处理检索请求")
    public R handleSearch(@RequestBody Map<String,Object> map){
        return R.SUCCESS(billRecordService.searchList(map));
    }










}
