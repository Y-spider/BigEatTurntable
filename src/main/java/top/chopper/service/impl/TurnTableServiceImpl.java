package top.chopper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.constant.TurnTableType;
import top.chopper.mapper.TurnTableMapper;
import top.chopper.pojo.TurnTable;
import top.chopper.service.TurnTableService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:36
   @Version:1.0.0
   @Description:
   */
@Service
public class TurnTableServiceImpl extends ServiceImpl<TurnTableMapper, TurnTable> implements TurnTableService {
    @Autowired
    private TurnTableMapper mapper;


    /**
     * @param turnTable 这里最主要的是接受的是id 和 内容 有以下两种可能：
     *                  1. 用户自定义系统转盘 那就是新建转盘
     *                  2. 修改用户自定义转盘(非系统转盘)
     */
    @Override
    @Transactional
    public void updateTurnTable(TurnTable turnTable) {
        TurnTable oldTurntable = mapper.selectById(turnTable.getId());
        if (oldTurntable.getType().equals(TurnTableType.TURN_TABLE_TYPE_SYS) || oldTurntable.getType().equals(TurnTableType.TURN_TABLE_TYPE_HOT)) {
            // 情况1 新建用户自定义转盘
            oldTurntable.setCreateTime(LocalDateTime.now());
            oldTurntable.setUpdateTime(LocalDateTime.now());
            oldTurntable.setContent(turnTable.getContent());
            oldTurntable.setId(null);
            oldTurntable.setType(TurnTableType.TURN_TABLE_TYPE_OPT);
            oldTurntable.setOpenid(SecurityUtil.getUserName());
            if(turnTable.getTitle()!=null){
                oldTurntable.setTitle(turnTable.getTitle());
            }
            mapper.insert(oldTurntable);
        } else {
            // 情况2 修改用户自定义转盘
            oldTurntable.setContent(turnTable.getContent());
            oldTurntable.setUpdateTime(LocalDateTime.now());
            mapper.updateById(oldTurntable);
        }

    }
}
