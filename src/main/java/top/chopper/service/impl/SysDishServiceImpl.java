package top.chopper.service.impl;

import cn.hutool.core.util.StrUtil;
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
import top.chopper.service.SysDishService;
import top.chopper.utils.xcfspider.SpiderUtil;

import java.time.LocalDateTime;
import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:34
   @Version:1.0.0
   @Description:
   */
@Service
public class SysDishServiceImpl extends ServiceImpl<SysDishMapper, SysDish> implements SysDishService {

    @Autowired
    private SysDishMapper mapper;
    @Autowired
    private SpiderUtil spiderUtil;
    @Autowired
    private DishMakeMapper dishMakeMapper;
    @Override
    public List<SysDish> listRandomDish(Integer count,Integer type) {
        List<SysDish> sysDishes = mapper.listRandom(count, type);
        for (SysDish sysDish : sysDishes) {
            sysDish.setName(sysDish.getName().split("-")[0]);
        }
        return sysDishes;
    }

    @Override
    @Transactional
    public void saveSysDishWithMakeUrl(SysDish sysDish) {
        sysDish.setCreateTime(LocalDateTime.now());
        sysDish.setUpdateTime(LocalDateTime.now());
        sysDish.setIsMake(true);
        mapper.insert(sysDish);
        if(StrUtil.isEmpty(sysDish.getMakeUrl())) return;
        DishMake dishMake = new DishMake();
        dishMake.setDishId(sysDish.getId());
        dishMake.setIsDelete(SysConstant.ALIVE);
        dishMake.setCreateTime(LocalDateTime.now());
        dishMake.setContent(JSONUtil.toJsonStr(spiderUtil.spiderPreparation(sysDish.getMakeUrl())));
        dishMakeMapper.insert(dishMake);
    }
}
