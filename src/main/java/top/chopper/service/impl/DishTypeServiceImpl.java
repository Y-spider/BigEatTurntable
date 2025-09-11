package top.chopper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.mapper.DishTypeMapper;
import top.chopper.mapper.SysDishMapper;
import top.chopper.pojo.DishType;
import top.chopper.pojo.SysDish;
import top.chopper.service.DishTypeService;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:35
   @Version:1.0.0
   @Description:
   */
@Service
@Slf4j
public class DishTypeServiceImpl extends ServiceImpl<DishTypeMapper, DishType> implements DishTypeService {
    @Autowired
    private DishTypeMapper dishTypeMapper;
    @Autowired
    private SysDishMapper sysDishMapper;
    @Override
    @Transactional
    public void deleteDishById(Integer id) {
        LambdaQueryWrapper<SysDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDish::getTypeId,id);
        int deleted = sysDishMapper.delete(queryWrapper);
        DishType dishType = dishTypeMapper.selectById(id);
        dishTypeMapper.deleteById(id);
        dishTypeMapper.deleteById(id);
        log.info("删除菜品类型id={},并且删除该菜品下的{}个菜品信息",dishType.getName(),deleted);
    }
}
