package top.chopper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.chopper.pojo.Test;

/*
   @Author:ROBOT
   @DateTime:2025/7/4 17:07
   @Version:1.0.0
   @Description:
   */
public interface TestMapper extends BaseMapper<Test> {
    Test myGetTestById(Integer id);
}
