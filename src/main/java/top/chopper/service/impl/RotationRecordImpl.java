package top.chopper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chopper.dto.ShareTurntableRotationRecordDto;
import top.chopper.mapper.RotationRecordMapper;
import top.chopper.pojo.RotationRecord;
import top.chopper.service.RotationRecordService;
import top.chopper.utils.SecurityUtil;

import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/7/13 18:25
   @Version:1.0.0
   @Description:
   */
@Service
public class RotationRecordImpl extends ServiceImpl<RotationRecordMapper, RotationRecord> implements RotationRecordService {
    @Autowired
    private RotationRecordMapper recordMapper;
    /**
     * @param id
     * @return
     */
    @Override
    public List<ShareTurntableRotationRecordDto> listShareTurntableRecord(Long id) {
        String openid = SecurityUtil.getUserName();
        return recordMapper.listShareTurntableRecordByTurntableId(id, openid);
    }
}
