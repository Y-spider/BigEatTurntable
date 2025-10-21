package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.DishCollection;

/*
   @Author:ROBOT
   @DateTime:2025/10/3 14:41
   @Version:1.0.0
   @Description:
   */
public interface DishCollectionService extends IService<DishCollection> {
    int myDeleteById(Integer id);
}
