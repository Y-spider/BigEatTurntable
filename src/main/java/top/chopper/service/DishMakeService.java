package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.DishMake;

/*
   @Author:ROBOT
   @DateTime:2025/7/20 14:07
   @Version:1.0.0
   @Description:
   */
public interface DishMakeService extends IService<DishMake> {
    void addDishMakeByUrl(String url,Integer dishId);
}
