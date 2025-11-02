package top.chopper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chopper.pojo.Festival;
import top.chopper.pojo.R;
import top.chopper.service.impl.FestivalService;

/*
   @Author:ROBOT
   @DateTime:2025/11/2 13:11
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/festival")
public class FestivalController {
    @Autowired
    private FestivalService service;

    @GetMapping("list/{type}")
    public R handleListById(@PathVariable(value = "type") String type){
        return R.SUCCESS(service.listByType(type));
    }

    @GetMapping("query/{id}")
    public R handleQueryById(@PathVariable("id") Integer id){
        return R.SUCCESS(service.queryById(id));
    }


    @PostMapping("/custom")
    public R handleSave(@RequestBody Festival festival){
        service.addCustomFestival(festival);
        return R.SUCCESS();
    }

    @DeleteMapping("/delete/{id}")
    public R handleDeleteCustomFestival(@PathVariable("id") Integer id){
        service.deleteCustomById(id);
        return R.SUCCESS();
    }


}
