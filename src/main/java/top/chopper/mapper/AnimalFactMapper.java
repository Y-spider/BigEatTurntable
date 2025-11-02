package top.chopper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.chopper.pojo.AnimalFact;


/*
   @Author:ROBOT
   @DateTime:2025/10/27 22:01
   @Version:1.0.0
   @Description:
   */
public interface AnimalFactMapper extends BaseMapper<AnimalFact> {
    AnimalFact getRandomByType(String type);
}
