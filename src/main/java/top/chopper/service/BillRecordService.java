package top.chopper.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.dto.QueryPageDto;
import top.chopper.pojo.BillRecord;

import java.time.LocalDateTime;
import java.util.Map;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:25
   @Version:1.0.0
   @Description:
   */
public interface BillRecordService extends IService<BillRecord> {
    Page<BillRecord> listWithPageAndCondition(QueryPageDto queryPageDto);

    Map<String,Object> handleSummaryMonth(LocalDateTime date,Integer userId);

    Map<String,Object> listWithMonth(LocalDateTime time,Integer userId);

    void handleMergeBook(Integer formBookId,Integer toBookId);

    Map<String,Object> searchList(Map<String,Object> map);
}
