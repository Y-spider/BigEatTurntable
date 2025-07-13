package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chopper.pojo.R;
import top.chopper.pojo.RotationRecord;
import top.chopper.service.RotationRecordService;
import top.chopper.utils.SecurityUtil;
import java.time.LocalDateTime;
/*
   @Author:ROBOT
   @DateTime:2025/7/13 18:29
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/record")
@Tag(name = "转动记录相关API")
public class RotationRecordController {
    @Autowired
    private RotationRecordService service;

    @Operation(description = "分页获取用户转动记录",summary = "分页获取用户转动记录")
    @GetMapping("/list/page/{pageIndex}/{pageSize}")
    public R listPage(@PathVariable("pageIndex") Integer pageIndex,@PathVariable("pageSize") Integer pageSize){
        String openid = SecurityUtil.getUserName();
        LambdaQueryWrapper<RotationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RotationRecord::getOpenid,openid)
                .orderByDesc(RotationRecord::getCreateTime);
        Page<RotationRecord> page = new Page<>(pageIndex,pageSize);
        return R.SUCCESS(service.page(page,queryWrapper));
    }

    @Operation(description = "获取用户转动记录总数",summary = "获取用户转动记录总数")
    @GetMapping("/list/count")
    public R getRecordCount(){
        String openid = SecurityUtil.getUserName();
        LambdaQueryWrapper<RotationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RotationRecord::getOpenid,openid);
        return R.SUCCESS(service.count(queryWrapper));
    }

    @Operation(description = "新增转动记录",summary = "新增转动记录")
    @PostMapping("/add")
    public R addRecord(@RequestBody RotationRecord record){
        String openid = SecurityUtil.getUserName();
        record.setOpenid(openid);
        record.setCreateTime(LocalDateTime.now());
        return R.SUCCESS(service.save(record));
    }

}
