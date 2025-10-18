package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.ApplyDish;

import java.util.HashMap;

/*
   @Author:ROBOT
   @DateTime:2025/9/11 23:12
   @Version:1.0.0
   @Description:
   */
public interface ApplyDishService  extends IService<ApplyDish> {
    void myAddApplyDish(ApplyDish applyDish);


    int reviewApplyDish(HashMap<String,Object> params);
}
