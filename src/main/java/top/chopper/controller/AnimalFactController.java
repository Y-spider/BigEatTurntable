package top.chopper.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chopper.pojo.R;
import top.chopper.service.AnimalFactService;

/*
   @Author:ROBOT
   @DateTime:2025/11/1 21:47
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/fact")
public class AnimalFactController {
    @Autowired
    private AnimalFactService service;

    @GetMapping("/cat")
    @Operation(description = "随机获取一个猫咪冷知识",summary = "随机获取一个猫咪冷知识")
    public R handleQueryCat(){
        return R.SUCCESS(service.randomCatFact());
    }

    @GetMapping("/dog")
    @Operation(description = "随机获取一个狗狗冷知识",summary = "随机获取一个狗狗冷知识")
    public R handleQueryDog(){
        return R.SUCCESS(service.randomDogFact());
    }
}
