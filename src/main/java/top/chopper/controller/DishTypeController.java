package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chopper.pojo.DishType;
import top.chopper.pojo.R;
import top.chopper.service.DishTypeService;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:38
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/dishType")
@Tag(name = "菜品类型接口")
public class DishTypeController {
   @Autowired
   private DishTypeService service;

   @GetMapping("/list")
   @Operation(summary = "取全部菜品类获型",description = "取全部菜品类获型")
    public R listAllDishType(){
       return R.SUCCESS(service.list());
   }
   @GetMapping("/like/{name}")
   @Operation(description = "根据菜品名称模糊查询菜品类获型",summary = "根据菜品名称模糊查询菜品类获型")
    public R queryByName(@PathVariable ("name") String name){
       QueryWrapper<DishType> queryWrapper = new QueryWrapper<>();
       queryWrapper.like("name",name);
       return R.SUCCESS(service.list(queryWrapper));
   }
   @GetMapping("/query/{id}")
   @Operation(description = "根据菜品id获取菜品类获型",summary = "根据菜品id获取菜品类获型")
   public R queryById(@PathVariable("id") Integer id){
      return R.SUCCESS(service.getById(id));
   }

   @PostMapping("/add")
   @Operation(description = "添加菜品类获型",summary = "添加菜品类获型")
   public R addDishType(@RequestBody DishType dishType){
      dishType.setCreateTime(LocalDateTime.now());
      QueryWrapper<DishType> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("name",dishType.getName());
      if (service.exists(queryWrapper)) {
         return R.FAIL("类型名称==>" + dishType.getName()+"<==已经存在");
      }
      return R.SUCCESS(service.save(dishType));
   }

   @PutMapping("/update")
   @Operation(description = "更新菜品类获型",summary = "更新菜品类获型")
   public R updateDishType(@RequestBody DishType dishType){
      dishType.setUpdateTime(LocalDateTime.now());
      service.updateById(dishType);
      return R.SUCCESS();
   }

   @DeleteMapping("/delete/{id}")
   @Operation(description = "根据id(逻辑删除)菜品类获型",summary = "根据id(逻辑删除)菜品类获型")
   public R deleteDishType(@PathVariable("id") Integer id){
      service.removeById(id);
      return R.SUCCESS();
   }
}
