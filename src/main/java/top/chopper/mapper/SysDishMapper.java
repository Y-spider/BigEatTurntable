package top.chopper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
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

    // 设置dishtypeId为默认id=0
    int updateSetDishType(Integer dishTypeId);
    // 获取当前用户收藏菜品信息
    Page<SysDish> selectCollectedDishPage(Page<SysDish> page, @Param("openid") String openid);

    int updateCollectionCount(Integer id,Integer increment);
}
