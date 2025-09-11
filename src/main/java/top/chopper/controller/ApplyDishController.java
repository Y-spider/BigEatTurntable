package top.chopper.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chopper.pojo.ApplyDish;
import top.chopper.pojo.R;
import top.chopper.service.ApplyDishService;

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

    @PostMapping("/add")
    @Operation(description = "处理新增请求",summary = "处理新增请求")
    public R handleAdd(@RequestBody ApplyDish applyDish){
        service.myAddApplyDish(applyDish);
        return R.SUCCESS();
    }

}
