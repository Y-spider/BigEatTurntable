package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.SysDish;
import java.util.List;
/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:33
   @Version:1.0.0
   @Description:
   */
public interface SysDishService extends IService<SysDish> {
    List<SysDish> listRandomDish(Integer count,Integer type);
}
