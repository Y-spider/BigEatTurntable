package top.chopper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.validation.constraints.Max;
import top.chopper.pojo.SysDish;
import java.util.List;
/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:27
   @Version:1.0.0
   @Description:
   */
public interface SysDishMapper extends BaseMapper<SysDish> {
    List<SysDish> listRandom(Integer count,Integer type);
}
