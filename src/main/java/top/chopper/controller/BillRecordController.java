package top.chopper.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.chopper.Exception.BusinessException;
import top.chopper.pojo.BillRecord;
import top.chopper.pojo.R;
import top.chopper.service.BillRecordService;
import top.chopper.service.UserService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:50
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/book/record")
@Tag(description = "账单明细处理器",name = "BillRecordController")
public class BillRecordController {
    @Autowired
    private BillRecordService billRecordService;
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @Operation(description = "新增消费记录",summary = "新增消费记录")
    public R handleSave(@RequestBody BillRecord billRecord){
        billRecord.setOpenid(SecurityUtil.getUserName());
        billRecord.setCreateTime(LocalDateTime.now());
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
    @Operation(description = "修改消费记录（好友之间可以相互修改）",summary = "新增消费记录(好友之间可以相互修改)")
    public R modifyBillRecord(@RequestBody BillRecord billRecord){
        billRecord.setUpdateTime(LocalDateTime.now());
        billRecord.setUpdateOpneid(SecurityUtil.getUserName());
        billRecord.setOpenid(null);
        billRecordService.updateById(billRecord);
        return R.SUCCESS();
    }



}
