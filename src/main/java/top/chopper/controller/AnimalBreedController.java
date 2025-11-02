package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chopper.pojo.AnimalBreed;
import top.chopper.pojo.R;
import top.chopper.service.AnimalBreedService;

/*
   @Author:ROBOT
   @DateTime:2025/10/30 21:33
   @Version:1.0.0
   @Description: 种类处理器
   */
@RestController
@RequestMapping("/breed")
@Tag(description = "种类处理接口",name = "种类处理接口")
public class AnimalBreedController {
    @Autowired
    private AnimalBreedService animalBreedService;

    @GetMapping("/query/{type}/{id}")
    @Operation(description = "搜索动物种类",summary = "搜索动物种类")
    public R handleQuery(@PathVariable("type") String type,@PathVariable("id") String id){
        LambdaQueryWrapper<AnimalBreed> queryWrapper = new LambdaQueryWrapper<>();
        if("cat".equals(type)){
            queryWrapper.eq(AnimalBreed::getType,type)
                    .eq(AnimalBreed::getId,id);
        }else if("dog".equals(type)){
            queryWrapper.eq(AnimalBreed::getType,type)
                    .eq(AnimalBreed::getIdDog,id);
        }
        AnimalBreed one = animalBreedService.getOne(queryWrapper);
        return R.SUCCESS(one);
    }


}
