package top.chopper.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.constant.SysConstant;
import top.chopper.mapper.DishMakeMapper;
import top.chopper.mapper.SysDishMapper;
import top.chopper.pojo.DishMake;
import top.chopper.pojo.SysDish;
import top.chopper.service.DishMakeService;
import top.chopper.utils.xcfspider.FoodPreparation;
import top.chopper.utils.xcfspider.SpiderUtil;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/20 14:07
   @Version:1.0.0
   @Description:
   */
@Service
public class DishMakeServiceImpl extends ServiceImpl<DishMakeMapper, DishMake> implements DishMakeService {
    @Autowired
    private SpiderUtil spiderUtil;
    @Autowired
    private DishMakeMapper mapper;
    @Autowired
    private SysDishMapper sysDishMapper;

    @Override
    @Transactional
    public void addDishMakeByUrl(String url,Integer dishId) {
        DishMake dishMake = new DishMake();
        dishMake.setDishId(dishId);
        dishMake.setCreateTime(LocalDateTime.now());
        dishMake.setIsDelete(SysConstant.ALIVE);
        FoodPreparation foodPreparation = spiderUtil.spiderPreparation(url);
        dishMake.setContent(JSONUtil.toJsonStr(foodPreparation));
        mapper.insert(dishMake);
        SysDish sysDish = new SysDish();
        sysDish.setId(dishId);
        sysDish.setUpdateTime(LocalDateTime.now());
        sysDish.setIsMake(true);
        sysDish.setMakeId(dishMake.getId());
        sysDishMapper.updateById(sysDish);

    }
}
