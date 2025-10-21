package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.chopper.dto.QueryPageDto;
import top.chopper.mapper.SysDishMapper;
import top.chopper.pojo.DishCollection;
import top.chopper.pojo.R;
import top.chopper.pojo.SysDish;
import top.chopper.service.DishCollectionService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/10/3 14:47
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/collection")
@Tag(description = "菜品收藏接口",name = "菜品收藏接口")
public class DishCollectionController {
    @Autowired
    private DishCollectionService dishCollectionService;
    @Autowired
    private SysDishMapper sysDishMapper;



    @Operation(summary = "新增收藏",description = "新增收藏")
    @GetMapping("/add")
    public R add(Integer dishId){
        DishCollection dishCollection = new DishCollection();
        dishCollection.setCreateTime(LocalDateTime.now());
        dishCollection.setDishId(dishId);
        dishCollection.setOpenid(SecurityUtil.getUserName());
        dishCollectionService.save(dishCollection);
        sysDishMapper.updateCollectionCount(dishId,1);
        return R.SUCCESS();
    }


    @Operation(summary = "取消收藏",description = "取消收藏")
    @GetMapping("/cancel")
    @Transactional
    public R cancel(Integer dishId){
        dishCollectionService.myDeleteById(dishId);
        sysDishMapper.updateCollectionCount(dishId,-1);
        return R.SUCCESS();
    }

    @Operation(summary = "查询收藏",description = "查询收藏")
    @GetMapping("/query")
    public R query(Integer dishId){
        LambdaQueryWrapper<DishCollection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishCollection::getDishId,dishId)
                .eq(DishCollection::getOpenid,SecurityUtil.getUserName());
        return R.SUCCESS(dishCollectionService.exists(queryWrapper));
    }

    @Operation(summary = "分页查询收藏记录",description = "分页查询收藏记录")
    @PostMapping("/list")
    public R query(@RequestBody QueryPageDto queryPageDto){
        Page<SysDish> page = new Page<>(queryPageDto.getPage(), queryPageDto.getLimit());
        Page<SysDish> dishPage = sysDishMapper.selectCollectedDishPage(page, SecurityUtil.getUserName());
        return R.SUCCESS(dishPage);
    }

}
