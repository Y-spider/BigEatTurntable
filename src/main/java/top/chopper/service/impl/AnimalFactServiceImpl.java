package top.chopper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chopper.mapper.AnimalFactMapper;
import top.chopper.pojo.AnimalFact;
import top.chopper.service.AnimalFactService;

/*
   @Author:ROBOT
   @DateTime:2025/11/1 21:46
   @Version:1.0.0
   @Description:
   */
@Service
public class AnimalFactServiceImpl extends ServiceImpl<AnimalFactMapper, AnimalFact> implements AnimalFactService {
    @Autowired
    private AnimalFactMapper mapper;
    /**
     * @return
     */
    @Override
    public AnimalFact randomCatFact() {
        return mapper.getRandomByType("cat");
    }

    /**
     * @return
     */
    @Override
    public AnimalFact randomDogFact() {
        return mapper.getRandomByType("dog");
    }
}
