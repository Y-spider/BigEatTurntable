package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chopper.Exception.BusinessException;
import top.chopper.dto.QueryPageDto;
import top.chopper.pojo.ApplyDish;
import top.chopper.pojo.R;
import top.chopper.pojo.SysDish;
import top.chopper.service.ApplyDishService;
import top.chopper.service.SysDishService;

import java.util.HashMap;
import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/9/11 23:20
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/apply/dish")
@Tag(description = "菜品申请处理器",name = "ApplyDishController")
public class ApplyDishController {
    @Autowired
    private ApplyDishService service;
    @Autowired
    private SysDishService sysDishService;

    @PostMapping("/add")
    @Operation(description = "处理新增请求",summary = "处理新增请求")
    public R handleAdd(@RequestBody ApplyDish applyDish){
        LambdaQueryWrapper<SysDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDish::getName,applyDish.getDishName());
        List<SysDish> list = sysDishService.list(queryWrapper);
        if(!list.isEmpty()){
            throw new BusinessException("菜品=>" + applyDish.getDishName()+" 已经存在");
        }
        service.myAddApplyDish(applyDish);
        return R.SUCCESS();
    }

    @PostMapping("/list/page")
    @Operation(summary = "分页获取",description = "分页获取")
    public R handleListPage(@RequestBody QueryPageDto queryPageDto){
        LambdaQueryWrapper<ApplyDish> queryWrapper = new LambdaQueryWrapper<>();
        Page<ApplyDish> page = new Page<>(queryPageDto.getPage(),queryPageDto.getLimit());
        queryWrapper.orderByDesc(ApplyDish::getCreateTime);
        queryWrapper.eq(queryPageDto.queryConditionIsExists("searchStatus"),ApplyDish::getStatus,queryPageDto.getQueryConditionValue("searchStatus"));
        return R.SUCCESS( service.page(page,queryWrapper));
    }

    @PostMapping("/review")
    @Operation(summary = "审核菜品",description = "审核菜品")
    public R handleRelation(@RequestBody HashMap<String,Object> params){
         service.reviewApplyDish(params);
        return R.SUCCESS();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(description = "删除菜单申请",summary = "删除菜单申请")
    public R handleDeleteApplyDish(@PathVariable(name = "id",required = true) Integer id){
        ApplyDish applyDish = service.getById(id);
        if(applyDish==null){
            throw new BusinessException("目标申请不存在");
        }
        if(applyDish.getStatus().equals("通过")){
            throw new BusinessException("审核通过无法删除");
        }
        service.removeById(id);
        return R.SUCCESS();
    }
}
