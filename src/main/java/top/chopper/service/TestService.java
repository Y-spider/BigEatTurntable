package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.Test;

/*
   @Author:ROBOT
   @DateTime:2025/7/4 17:08
   @Version:1.0.0
   @Description:
   */
public interface TestService extends IService<Test> {
    Test myGetById(Integer id);
}
