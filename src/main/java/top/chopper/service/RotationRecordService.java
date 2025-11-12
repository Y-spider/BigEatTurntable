package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.dto.ShareTurntableRotationRecordDto;
import top.chopper.pojo.RotationRecord;

import java.util.HashMap;
import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/7/13 18:25
   @Version:1.0.0
   @Description:
   */
public interface RotationRecordService extends IService<RotationRecord> {
    List<ShareTurntableRotationRecordDto> listShareTurntableRecord(Long id);
    void addRecord(RotationRecord record);

    // 计算当前用户剩余转动次数
    HashMap<String,Object> calcSpinCount(Long id);
}
