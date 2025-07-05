package top.chopper.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.chopper.pojo.R;
import top.chopper.pojo.Test;
import top.chopper.service.TestService;

/*
   @Author:ROBOT
   @DateTime:2025/7/5 16:01
   @Version:1.0.0
   @Description:
   */
@RestController
@Tag(name = "测试类相关接口")
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService testService;
    @RequestMapping("/test")
    public R<Test> test(@RequestParam("id") int id){
        Test test = testService.myGetById(id);
//        System.out.println(test.getName());
       return R.SUCCESS(test);
    }
}
