package top.chopper.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chopper.Exception.BusinessException;
import top.chopper.dto.QueryPageDto;
import top.chopper.mapper.BillRecordMapper;
import top.chopper.mapper.BillShareMapper;
import top.chopper.pojo.BillRecord;
import top.chopper.pojo.BillShare;
import top.chopper.service.BillRecordService;

import java.util.HashMap;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:27
   @Version:1.0.0
   @Description:
   */
@Service
@Slf4j
public class BillRecordServiceImpl extends ServiceImpl<BillRecordMapper, BillRecord> implements BillRecordService {
    @Autowired
    private BillRecordMapper billRecordMapper;

    @Autowired
    private BillShareMapper billShareMapper;
    /**
     * 分页+查询条件获取账单
     * @param queryPageDto
     * @return
     */
    @Override
    public Page<BillRecord> listWithPageAndCondition(QueryPageDto queryPageDto) {
        try {
            Page<BillRecord> pageInfo = new Page<>(queryPageDto.getPage(),queryPageDto.getLimit());
            LambdaQueryWrapper<BillRecord> queryWrapper = new LambdaQueryWrapper<>();
            HashMap<String, Object> conditionMap = queryPageDto.getQueryMap();
            Integer billBookId = (Integer)conditionMap.get("billId"); // 账单ID
            Object startTime = conditionMap.get("startTime"); // 查询范围的起始时间
            Object endTime = conditionMap.get("endTime"); // 查询范围的终结时间
            Object aimOpenid = conditionMap.get("selectUser"); // 筛选用户的openid
            Object type = conditionMap.get("type");
            if(ObjectUtil.isNull(billBookId)){
                log.error("获取账单记录参数[billBookId]==>{}",billBookId);
                throw new BusinessException("系统繁忙，请稍后再试！");
            }
            queryWrapper.eq(BillRecord::getBillBookId,billBookId);
            if(ObjectUtil.isNotEmpty(aimOpenid)){
                // 检查当前用户是否为合法
                LambdaQueryWrapper<BillShare> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(BillShare::getId,billBookId)
                        .eq(BillShare::getOpenid,aimOpenid);
                BillShare billShare = billShareMapper.selectOne(queryWrapper1);
                if(ObjectUtil.isEmpty(billShare)){
                    log.error("获取账单[{}]记录查询参数[aimOpenid]非法{}",billBookId,aimOpenid);
                    throw new BusinessException("系统繁忙，请稍后再试！");
                }
                queryWrapper.eq(BillRecord::getOpenid,aimOpenid);
            }
            if(ObjectUtil.isNotEmpty(type)){
                queryWrapper.eq(BillRecord::getType,type);
            }
            if(ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty("endTime")){
                queryWrapper.between(BillRecord::getCreateTime,startTime,endTime);
            }
            queryWrapper.orderByDesc(BillRecord::getCreateTime);

            return billRecordMapper.selectPage(pageInfo, queryWrapper);
        }catch ( Exception e ){
            throw new BusinessException(e.getMessage());
        }
    }
}
