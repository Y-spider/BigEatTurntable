package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.AnimalFact;

/*
   @Author:ROBOT
   @DateTime:2025/11/1 21:45
   @Version:1.0.0
   @Description:
   */
public interface AnimalFactService  extends IService<AnimalFact> {
    AnimalFact randomCatFact();

    AnimalFact randomDogFact();
}
