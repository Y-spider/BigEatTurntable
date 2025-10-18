package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chopper.dto.QueryPageDto;
import top.chopper.pojo.R;
import top.chopper.pojo.SysNotice;
import top.chopper.service.SysNoticeService;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/26 15:59
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/notice")
@Tag(description = "系统通知接口",name = "SysNoticeController")
public class SysNoticeController {
    @Autowired
    private SysNoticeService service;

    @Operation(description = "新增通知信息",summary = "新增通知信息")
    @PostMapping("/add")
    public R handleAdd(@RequestBody SysNotice sysNotice){
        sysNotice.setCreateTime(LocalDateTime.now());
        sysNotice.setUpdateTime(LocalDateTime.now());
        if(sysNotice.getActive()){
            LambdaUpdateWrapper<SysNotice> updateChainWrapper = new LambdaUpdateWrapper<>();
            updateChainWrapper.set(SysNotice::getActive,false);
            service.update(updateChainWrapper);
        }
        service.save(sysNotice);
        return R.SUCCESS();
    }

    @Operation(description = "修改通知信息",summary = "修改通知信息")
    @PutMapping("/update")
    public R handleUpdate(@RequestBody SysNotice sysNotice){
        sysNotice.setCreateTime(LocalDateTime.now());
        sysNotice.setUpdateTime(LocalDateTime.now());
        if(sysNotice.getActive()){
             SysNotice oldActive = (SysNotice) handleGetActive().getData();
             if(oldActive!=null){
                 oldActive.setActive(false);
                 service.updateById(oldActive);
             }
        }
        service.updateById(sysNotice);
        return R.SUCCESS();
    }

    @Operation(description = "分页获取通知信息",summary = "分页获取通知信息")
    @PostMapping("/list/page")
    public R handleListPage(@RequestBody QueryPageDto queryPageDto){
        LambdaQueryWrapper<SysNotice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(SysNotice::getCreateTime);
        Page<SysNotice> page = new Page<>(queryPageDto.getPage(),queryPageDto.getLimit());
        return R.SUCCESS(service.page(page,queryWrapper));
    }

    @Operation(description = "获取当前激活通知",summary = "获取当前激活通知")
    @GetMapping("/active")
    public R handleGetActive(){
        LambdaQueryWrapper<SysNotice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNotice::getActive,true);
        return R.SUCCESS(service.getOne(queryWrapper));
    }


    @Operation(description = "删除通知",summary = "删除通知")
    @DeleteMapping("/delete/{id}")
    public R handleDelete(@PathVariable("id") Integer id){
        service.myDeleteNoticeById(id);
        return R.SUCCESS();
    }

}
