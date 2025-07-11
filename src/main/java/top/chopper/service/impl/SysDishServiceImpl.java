package top.chopper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chopper.mapper.SysDishMapper;
import top.chopper.pojo.SysDish;
import top.chopper.service.SysDishService;

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
    @Override
    public List<SysDish> listRandomDish(Integer count,Integer type) {
        return mapper.listRandom(count,type);
    }
}
