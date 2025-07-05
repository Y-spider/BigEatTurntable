package top.chopper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chopper.mapper.TestMapper;
import top.chopper.pojo.Test;
import top.chopper.service.TestService;

/*
   @Author:ROBOT
   @DateTime:2025/7/4 17:15
   @Version:1.0.0
   @Description:
   */
@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService{
    @Autowired
    private TestMapper testMapper;
    @Override
    public Test myGetById(Integer id) {
        return testMapper.myGetTestById(id);
    }
}
