package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chopper.dto.QueryPageDto;
import top.chopper.pojo.R;
import top.chopper.pojo.SysDish;
import top.chopper.service.SysDishService;
import java.util.ArrayList;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:38
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/sysDish")
@Tag(name = "系统菜品接口")
public class SysDishController {
    @Autowired
    private SysDishService service;

    @Operation(description = "随机获取菜品", summary = "随机获取菜品", parameters = {
            @Parameter(name = "count", description = "随机获取的数量", required = true),
            @Parameter(name = "type", description = "菜单类型id，0表示全部类型", required = false)
    })
    @GetMapping("/list/random/{count}/{type}")
    public R listRandom(@PathVariable("count") Integer count, @PathVariable("type") Integer type) {
        return R.SUCCESS(service.listRandomDish(count, type));
    }

    @Operation(description = "分页条件获取菜品信息",summary = "分页条件获取菜品信息")
    @PostMapping("/list/page")
    public R handleListByPage(@RequestBody QueryPageDto queryPageDto){
        LambdaQueryWrapper<SysDish> queryWrapper = new LambdaQueryWrapper<>();
        Page<SysDish> page = new Page<>(queryPageDto.getPage(),queryPageDto.getLimit());
        queryWrapper.like(queryPageDto.queryConditionIsExists("name"),SysDish::getName,queryPageDto.getQueryConditionValue("name"))
                .eq(queryPageDto.queryConditionIsExists("type"),SysDish::getTypeId,queryPageDto.getQueryConditionValue("type")) ;
        return R.SUCCESS( service.page(page,queryWrapper));
    }

    @Operation(description = "根据id获取菜品信息",summary = "根据id获取菜品信息")
    @GetMapping("/select/{id}")
    public R selectSingelSysDish(@PathVariable("id") Integer id){
        return R.SUCCESS(service.getById(id));
    }
    @Operation(description = "根据id删除菜品",summary = "根据id删除菜品",parameters = {
            @Parameter(name = "id",description = "菜单id",required = true)
    })
    @PostMapping("/delete/batch")
    // 还需要调试
    public R delete(@RequestBody ArrayList<Integer> ids){
        return R.SUCCESS(service.removeBatchByIds(ids));
    }

    @Operation(description = "修改菜品信息",summary = "修改菜品信息")
    @PutMapping("/update")
    public R updateDish(@RequestBody SysDish sysDish){
        sysDish.autoSetUpdateTime();
        service.updateById(sysDish);
        return R.SUCCESS();
    }

    @Operation(description = "新增菜品信息",summary = "新增菜品信息")
    @PostMapping("/add")
    public R addSysDish(@RequestBody SysDish sysDish){
        service.save(sysDish);
        return R.SUCCESS();
    }
}