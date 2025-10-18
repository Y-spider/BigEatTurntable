package top.chopper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.chopper.dto.ShareTurntableRotationRecordDto;
import top.chopper.pojo.RotationRecord;

import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/7/13 18:17
   @Version:1.0.0
   @Description:
   */
public interface RotationRecordMapper extends BaseMapper<RotationRecord> {
    List<ShareTurntableRotationRecordDto> listShareTurntableRecordByTurntableId(Long id,String openid);
}
