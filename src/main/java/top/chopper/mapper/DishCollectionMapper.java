package top.chopper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.chopper.pojo.DishCollection;

/*
   @Author:ROBOT
   @DateTime:2025/10/3 14:39
   @Version:1.0.0
   @Description:
   */
public interface DishCollectionMapper extends BaseMapper<DishCollection> {
    int deleteMyCollectionBuId(Integer id,String openid);
}
