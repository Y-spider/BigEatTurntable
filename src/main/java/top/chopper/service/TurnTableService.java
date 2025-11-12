package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.TurnTable;

import java.util.HashMap;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:36
   @Version:1.0.0
   @Description:
   */
public interface TurnTableService extends IService<TurnTable> {
    /**
     * @param turnTable
     * @return 返回操作转盘id,或者新增转盘ID
     */
    Integer updateTurnTable(TurnTable turnTable);

    void myDeleteTurntableById(Integer id);

    HashMap<String,Object> getErCodeUrl(Integer id);

}
