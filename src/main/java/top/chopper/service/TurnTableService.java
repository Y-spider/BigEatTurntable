package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.TurnTable;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:36
   @Version:1.0.0
   @Description:
   */
public interface TurnTableService extends IService<TurnTable> {
    void updateTurnTable(TurnTable turnTable);

}
