package top.chopper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.Exception.BusinessException;
import top.chopper.dto.ShareTurntableRotationRecordDto;
import top.chopper.mapper.RotationRecordMapper;
import top.chopper.mapper.TurnTableMapper;
import top.chopper.pojo.RotationRecord;
import top.chopper.pojo.TurnTable;
import top.chopper.service.RotationRecordService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
   @Author:ROBOT
   @DateTime:2025/7/13 18:25
   @Version:1.0.0
   @Description:
   */
@Service
@Slf4j
public class RotationRecordImpl extends ServiceImpl<RotationRecordMapper, RotationRecord> implements RotationRecordService {
    @Autowired
    private RotationRecordMapper recordMapper;
    @Autowired
    private TurnTableMapper turnTableMapper;
    /**
     * @param id
     * @return
     */
    @Override
    public List<ShareTurntableRotationRecordDto> listShareTurntableRecord(Long id) {
        String openid = SecurityUtil.getUserName();
        return recordMapper.listShareTurntableRecordByTurntableId(id, openid);
    }

    /**
     * 新增旋转记录，如果是不重复选，那么还需要减去剩余奖品数。
     * @param record
     */
    @Transactional
    @Override
    public void addRecord(RotationRecord record) {
        String openid = SecurityUtil.getUserName();
        record.setOpenid(openid);
        record.setCreateTime(LocalDateTime.now());
        recordMapper.insert(record);
        // 开始判断是否需要减去库存
        TurnTable turnTable = turnTableMapper.selectById(record.getTurntableId());
        if(turnTable!=null){
            if(!turnTable.getIsRepeat()){
                String s = this.reducePrizeCount(turnTable.getContent(), record.getResult());
                turnTable.setContent(s);
                turnTableMapper.updateById(turnTable);
            }
        }else{
            log.error("旋转记录添加转盘查询为NULL!!!参数不存在==》｛{}",record.getTurntableId());
            throw new BusinessException("系统错误，请联系管理员!");
        }
    }

    /**
     * 计算当前用户剩余抽取次数
     * @param id
     * @return
     */
    @Override
    public HashMap<String, Object> calcSpinCount(Long id) {
        HashMap<String, Object> result = new HashMap<>();
        TurnTable turnTable = turnTableMapper.selectById(id);
        if(turnTable.getLimitCount() == 0){
            result.put("spinCount",-1);
            return result;
        }
        LambdaQueryWrapper<RotationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RotationRecord::getOpenid,SecurityUtil.getUserName())
                .between(RotationRecord::getCreateTime,turnTable.getLimitStartTime(),LocalDateTime.now());
        Long count = recordMapper.selectCount(queryWrapper);
        result.put("spinCount",turnTable.getLimitCount() -  count);
        return result;
    }

    /**
     * ps : 如果是不重复抽的情况下，业务上杜绝了相同奖品名称的出现
     * @param prize 奖品名称
     * @param content 奖品列表JSON字符串
     * @return 处理后的奖品JSON字符串
     */
    private String reducePrizeCount(String content,String prize){
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String, Object>> prizeList = mapper.readValue(content, new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> map : prizeList) {
                List<Map<String, Object>> fonts = (List<Map<String, Object>>) map.get("fonts");
                if((fonts.get(0).get("text").equals(prize))){
                    Integer count = (Integer)map.get("count");
                    if(count- 1 >= 0){
                        map.put("count",--count);
//                        if(count==0){
//                            map.put("range",0);
//                        }
                        break;
                    }else{
                        log.error("奖品库存不足，系统错误!");
                        throw new BusinessException("已经被其他用户抽走啦!");
                    }
                }
            }
            return mapper.writeValueAsString(prizeList);
        } catch ( JsonProcessingException e ) {
            log.error("减少奖品库存解析奖品JSON字符串报错===>{}",e.getMessage());
            throw new BusinessException("系统异常，请联系管理员!");
        }

    }

}
