package top.chopper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.constant.TurnTableType;
import top.chopper.mapper.RotationRecordMapper;
import top.chopper.mapper.TurnTableMapper;
import top.chopper.pojo.RotationRecord;
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
@Slf4j
public class TurnTableServiceImpl extends ServiceImpl<TurnTableMapper, TurnTable> implements TurnTableService {
    @Autowired
    private TurnTableMapper mapper;
    @Autowired
    private RotationRecordMapper recordMapper;


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
            if(!oldTurntable.getTitle().equals(turnTable.getTitle())){
                // 修改记录
                LambdaUpdateWrapper<RotationRecord> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(RotationRecord::getTurntableName,turnTable.getTitle());
                updateWrapper.eq(RotationRecord::getTurntableId,turnTable.getId());
                int updated = recordMapper.update(updateWrapper);
                log.info("用户:{}修改了转盘oldName={}为newName={},旋转记录受影响条数为={}",SecurityUtil.getUserName(),oldTurntable.getTitle(),turnTable.getTitle(),updated);
                oldTurntable.setTitle(turnTable.getTitle());
            }
            oldTurntable.setContent(turnTable.getContent());
            oldTurntable.setUpdateTime(LocalDateTime.now());
            mapper.updateById(oldTurntable);
        }

    }

    /**
     * @param id
     */
    @Override
    @Transactional
    public void myDeleteTurntableById(Integer id) {
        LambdaQueryWrapper<RotationRecord> recordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        recordLambdaQueryWrapper.eq(RotationRecord::getTurntableId,id);
        TurnTable turnTable = mapper.selectById(id);
        int deleted = recordMapper.delete(recordLambdaQueryWrapper);
        mapper.deleteById(id);
        log.info("成功删除用户==>{}自定义转盘==》{}旋转记录受影响条数为:{}",SecurityUtil.getUserName(),turnTable.getTitle(),deleted);
    }
}
