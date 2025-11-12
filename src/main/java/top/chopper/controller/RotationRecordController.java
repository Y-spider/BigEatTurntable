package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chopper.dto.ShareTurntableRotationRecordDto;
import top.chopper.pojo.R;
import top.chopper.pojo.RotationRecord;
import top.chopper.service.RotationRecordService;
import top.chopper.utils.SecurityUtil;

import java.util.List;

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
        service.addRecord(record);
        return R.SUCCESS();
    }

    @Operation(description = "获取单个转盘记录，包含好友")
    @GetMapping("/list/share/{id}")
    public R getRecordByTurntableId(@PathVariable("id")Long id){
        List<ShareTurntableRotationRecordDto> list = service.listShareTurntableRecord(id);
        return R.SUCCESS(list);
    }

    @Operation(description = "获取剩余次数，仅针对设置了抽取次数限制的转盘")
    @GetMapping("/spinCount/{id}")
    public R getRecordCanSpinCountTurntableId(@PathVariable("id")Long id){
        return R.SUCCESS(service.calcSpinCount(id));
    }

}
