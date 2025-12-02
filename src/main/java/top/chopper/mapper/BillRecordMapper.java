package top.chopper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.chopper.pojo.BillRecord;

import java.util.Map;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:23
   @Version:1.0.0
   @Description:
   */
public interface BillRecordMapper extends BaseMapper<BillRecord> {
    Map<String,Object> summaryRecordByIdAndTime(Integer billId,String startTime,String endTime,String openid);
}
