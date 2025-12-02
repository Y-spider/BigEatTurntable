package top.chopper.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.Exception.BusinessException;
import top.chopper.constant.BillConstant;
import top.chopper.dto.QueryPageDto;
import top.chopper.mapper.BillRecordMapper;
import top.chopper.mapper.BillShareMapper;
import top.chopper.pojo.BillBook;
import top.chopper.pojo.BillRecord;
import top.chopper.pojo.BillShare;
import top.chopper.pojo.User;
import top.chopper.service.BillBookService;
import top.chopper.service.BillRecordService;
import top.chopper.service.UserService;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

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

    @Autowired
    @Lazy
    private BillBookService billBookService;
    @Autowired
    private UserService userService;

    /**
     * 分页+查询条件获取账单
     *
     * @param queryPageDto
     * @return
     */
    @Override
    public Page<BillRecord> listWithPageAndCondition(QueryPageDto queryPageDto) {
        try {
            Page<BillRecord> pageInfo = new Page<>(queryPageDto.getPage(), queryPageDto.getLimit());
            LambdaQueryWrapper<BillRecord> queryWrapper = new LambdaQueryWrapper<>();
            HashMap<String, Object> conditionMap = queryPageDto.getQueryMap();
            Integer billBookId = (Integer) conditionMap.get("billId"); // 账单ID
            Object startTime = conditionMap.get("startTime"); // 查询范围的起始时间
            Object endTime = conditionMap.get("endTime"); // 查询范围的终结时间
            Object aimOpenid = conditionMap.get("selectUser"); // 筛选用户的openid
            Object type = conditionMap.get("type");
            if (ObjectUtil.isNull(billBookId)) {
                log.error("获取账单记录参数[billBookId]==>{}", billBookId);
                throw new BusinessException("系统繁忙，请稍后再试！");
            }
            queryWrapper.eq(BillRecord::getBillBookId, billBookId);
            if (ObjectUtil.isNotEmpty(aimOpenid)) {
                // 检查当前用户是否为合法
                LambdaQueryWrapper<BillShare> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(BillShare::getId, billBookId)
                        .eq(BillShare::getOpenid, aimOpenid);
                BillShare billShare = billShareMapper.selectOne(queryWrapper1);
                if (ObjectUtil.isEmpty(billShare)) {
                    log.error("获取账单[{}]记录查询参数[aimOpenid]非法{}", billBookId, aimOpenid);
                    throw new BusinessException("系统繁忙，请稍后再试！");
                }
                queryWrapper.eq(BillRecord::getOpenid, aimOpenid);
            }
            if (ObjectUtil.isNotEmpty(type)) {
                queryWrapper.eq(BillRecord::getType, type);
            }
            if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty("endTime")) {
                queryWrapper.between(BillRecord::getCreateTime, startTime, endTime);
            }
            queryWrapper.orderByDesc(BillRecord::getCreateTime);

            return billRecordMapper.selectPage(pageInfo, queryWrapper);
        } catch ( Exception e ) {
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * @param userId 为-1表示获取全部
     * @param time 时间格式为yyyy-dd
     * @return
     */
    @Override
    public Map<String, Object> handleSummaryMonth(LocalDateTime time,Integer userId) {
        BillBook currentChooseBookBill = billBookService.getCurrentChooseBookBill();
        Integer id = currentChooseBookBill.getId();
        if(ObjectUtil.isNotEmpty(currentChooseBookBill.getParentId())){
            id = currentChooseBookBill.getParentId();
        }
        Map<String, String> timeMap = handleGetMonthStartAndEnd(time);
        String openid = "all";
       if(userId!=-1){
           User user = userService.getById(userId);
           if(ObjectUtil.isEmpty(user)){
               throw new BusinessException("系统错误，请稍后再试!");
           }
           openid = user.getOpenid();
       }
        return billRecordMapper.summaryRecordByIdAndTime(id, timeMap.get("startTime"), timeMap.get("endTime"), openid);
    }

    /**
     * @param time
     * @param opneid 筛选出的目标用户  查询全部表示时openid == all
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public Map<String, Object> listWithMonth(LocalDateTime time,Integer userId) {
        LocalDateTime startOfMonth = time.with(TemporalAdjusters.firstDayOfMonth())
                .with(LocalTime.MIN);
        BillBook currentChooseBookBill = billBookService.getCurrentChooseBookBill();
        // 当月最后一天 23:59:59.999999999
        LocalDateTime endOfMonth = time.with(TemporalAdjusters.lastDayOfMonth())
                .with(LocalTime.MAX);
        HashMap<String, String> result = new HashMap<>();
        result.put("startTime", startOfMonth.toString());
        result.put("endTime", endOfMonth.toString());
        LambdaQueryWrapper<BillRecord> queryWrapper = new LambdaQueryWrapper<>();
       String openid = "all";
        if(userId!=-1){
           User user = userService.getById(userId);
           if(ObjectUtil.isEmpty(user)){
               throw new BusinessException("系统错误，请稍后再试!");
           }
           openid = user.getOpenid();
       }
        Integer queryBillBookId = currentChooseBookBill.getId();
        if(ObjectUtil.isNotEmpty(currentChooseBookBill.getParentId())){
            queryBillBookId = currentChooseBookBill.getParentId();
        }
        queryWrapper
                .eq("all"!=openid,BillRecord::getOpenid, openid)
                .eq(BillRecord::getBillBookId,queryBillBookId)
                .between(BillRecord::getCreateTime, startOfMonth, endOfMonth)
                .orderByDesc(BillRecord::getRecordTime)
                .orderByDesc(BillRecord::getUpdateTime)
                .orderByDesc(BillRecord::getCreateTime);
        List<BillRecord> billRecords = billRecordMapper.selectList(queryWrapper);
        Map<String, Object> map = new LinkedHashMap<>();
        for (BillRecord billRecord : billRecords) {
            String key = billRecord.getRecordTime();
            if (map.containsKey(key)) {
                HashMap<String,Object> valueMap = (HashMap<String,Object>)map.get(billRecord.getRecordTime());
                ArrayList<BillRecord> recordList = (ArrayList<BillRecord>)valueMap.get("recordList");
                if("in".equals(billRecord.getType())){
                    BigDecimal inAmount = (BigDecimal)valueMap.get("inAmount");
                    BigDecimal add = inAmount.add(billRecord.getAmount());
                    valueMap.put("inAmount",add);
                }else if("out".equals(billRecord.getType())){
                    BigDecimal outAmount = (BigDecimal)valueMap.get("outAmount");
                    BigDecimal add = outAmount.add(billRecord.getAmount());
                    valueMap.put("outAmount",add);
                }
                recordList.add(billRecord);
                map.put(key,valueMap);
            } else {
                HashMap<String, Object> valueMap = new HashMap<>();
                ArrayList<BillRecord> recordArrayList = new ArrayList<>();
                recordArrayList.add(billRecord);
                valueMap.put("date", translateDate(billRecord.getRecordTime()));
                valueMap.put("week",getChineseWeek(billRecord.getRecordTime()));
                valueMap.put("recordList",recordArrayList);
                if("in".equals(billRecord.getType())){
                    valueMap.put("inAmount",billRecord.getAmount());
                    valueMap.put("outAmount",BigDecimal.ZERO);
                }else if("out".equals(billRecord.getType())){
                    valueMap.put("inAmount",BigDecimal.ZERO);
                    valueMap.put("outAmount",billRecord.getAmount());
                }
                map.put(key, valueMap);
            }
        }
        return map;
    }

    /**
     * 导入规则 默认可以导入共享  共享不能导入默认 共享可以导入共享
     * @param formBookId
     * @param toBookId
     */
    @Override
    @Transactional
    public void handleMergeBook(Integer formBookId, Integer toBookId) {
        BillBook formBillBook = billBookService.getById(formBookId);
        BillBook toBillBook = billBookService.getById(toBookId);
        if(ObjectUtil.isEmpty(formBillBook)){
            throw new BusinessException("选择账本已经被删除！");
        }
        if(ObjectUtil.isEmpty(toBillBook)){
            throw new BusinessException("目标账本已经被删除!");
        }
        if(toBillBook.getType().equals(BillConstant.BILL_BOOK_TYPE_DEFAULT)){
            throw new BusinessException("默认账本不能导入其他账本记录");
        }
        // 1. 获取form记录
        LambdaQueryWrapper<BillRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillRecord::getBillBookId,formBillBook.getId());
        List<BillRecord> billRecords = billRecordMapper.selectList(queryWrapper);
        for (BillRecord billRecord : billRecords) {
            billRecord.setId(null);
            billRecord.setBillBookId(toBookId);
        }
        billRecordMapper.insert(billRecords);
    }

    /**
     * 处理筛选请求
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> searchList(Map<String, Object> map) {
        BillBook currentChooseBookBill = billBookService.getCurrentChooseBookBill();
        LambdaQueryWrapper<BillRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillRecord::getBillBookId,BillBook.getIdByCurrentBillBook(currentChooseBookBill));
        if(ObjectUtil.isNotEmpty(map.get("userId")) && !"-1".equals(map.get("userId").toString())){
            User user = userService.getById((Integer) map.get("userId"));
            queryWrapper.eq(BillRecord::getOpenid,user.getOpenid());
        }
        if(ObjectUtil.isNotEmpty(map.get("type")) && !"全部分类".equals(map.get("type"))){
            queryWrapper.eq(BillRecord::getType,map.get("type"));
        }
        if(ObjectUtil.isNotEmpty(map.get("startTime")) && ObjectUtil.isNotEmpty(map.get("endTime"))){
            LocalDate startTime1 = LocalDate.parse(map.get("startTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime startTime = startTime1.atStartOfDay();
            LocalDate endTime1 = LocalDate.parse(map.get("endTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime endTime = endTime1.atStartOfDay();
            if(endTime.isBefore(startTime)){
                // 不处理，查询全部
            }else{
                queryWrapper.between(BillRecord::getRecordTime,startTime1,endTime1);
            }
        }
        if(ObjectUtil.isNotEmpty(map.get("methodType")) && !"全部账户".equals(map.get("methodType"))){
            queryWrapper.eq(BillRecord::getMethodLabel,map.get("methodType"));
        }
        if(ObjectUtil.isNotEmpty(map.get("remark"))){
            queryWrapper.like(BillRecord::getRemark,map.get("remark"));
        }
        queryWrapper.
                 orderByDesc(BillRecord::getRecordTime)
                .orderByDesc(BillRecord::getUpdateTime)
                .orderByDesc(BillRecord::getCreateTime);
        List<BillRecord> billRecords = billRecordMapper.selectList(queryWrapper);
        BigDecimal outTotal = BigDecimal.ZERO;
        BigDecimal inTotal = BigDecimal.ZERO;
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        for (BillRecord billRecord : billRecords) {
            String key = billRecord.getRecordTime();
            if (resultMap.containsKey(key)) {
                HashMap<String,Object> valueMap = (HashMap<String,Object>)resultMap.get(billRecord.getRecordTime());
                ArrayList<BillRecord> recordList = (ArrayList<BillRecord>)valueMap.get("recordList");
                if("in".equals(billRecord.getType())){
                    BigDecimal inAmount = (BigDecimal)valueMap.get("inAmount");
                    BigDecimal add = inAmount.add(billRecord.getAmount());
                    valueMap.put("inAmount",add);
                    inTotal = inTotal.add(billRecord.getAmount());
                }else if("out".equals(billRecord.getType())){
                    BigDecimal outAmount = (BigDecimal)valueMap.get("outAmount");
                    BigDecimal add = outAmount.add(billRecord.getAmount());
                    valueMap.put("outAmount",add);
                    outTotal = outTotal.add(billRecord.getAmount());
                }
                recordList.add(billRecord);
                resultMap.put(key,valueMap);
            } else {
                HashMap<String, Object> valueMap = new HashMap<>();
                ArrayList<BillRecord> recordArrayList = new ArrayList<>();
                recordArrayList.add(billRecord);
                valueMap.put("date", translateDate(billRecord.getRecordTime()));
                valueMap.put("week",getChineseWeek(billRecord.getRecordTime()));
                valueMap.put("recordList",recordArrayList);
                if("in".equals(billRecord.getType())){
                    valueMap.put("inAmount",billRecord.getAmount());
                    valueMap.put("outAmount",BigDecimal.ZERO);
                    inTotal = inTotal.add(billRecord.getAmount());
                }else if("out".equals(billRecord.getType())){
                    valueMap.put("inAmount",BigDecimal.ZERO);
                    valueMap.put("outAmount",billRecord.getAmount());
                    outTotal = outTotal.add(billRecord.getAmount());
                }
                resultMap.put(key, valueMap);
            }
        }
        resultMap.put("inTotal",inTotal);
        resultMap.put("outTotal",outTotal);
        resultMap.put("surPlus",inTotal.subtract(outTotal));
        resultMap.put("makeCount",billRecords.size()); // 记录数
        return resultMap;
    }

    /**
     * 处理获取当前所属月份起始时间与结束时间
     *
     * @param time
     * @return
     */
    private Map<String, String> handleGetMonthStartAndEnd(LocalDateTime time) {
        LocalDateTime startOfMonth = time.with(TemporalAdjusters.firstDayOfMonth())
                .with(LocalTime.MIN);

        // 当月最后一天 23:59:59.999999999
        LocalDateTime endOfMonth = time.with(TemporalAdjusters.lastDayOfMonth())
                .with(LocalTime.MAX);
        HashMap<String, String> result = new HashMap<>();
        result.put("startTime", startOfMonth.toString());
        result.put("endTime", endOfMonth.toString());
        return result;
    }

    public static void main(String[] args) {
        LocalDateTime date = LocalDateTime.of(2025, 11, 5, 10, 30); // 示例日期

        // 当月第一天 00:00:00
        LocalDateTime startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth())
                .with(LocalTime.MIN);

        // 当月最后一天 23:59:59.999999999
        LocalDateTime endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth())
                .with(LocalTime.MAX);

        System.out.println("当月起始日期: " + startOfMonth);
        System.out.println("当月结束日期: " + endOfMonth);
    }

    private String translateDate(String recordTime) {
        String[] split = recordTime.split("-");
        return split[1] + "月" + split[2] + "日";
    }

    // 工具方法：英文星期转中文星期
    private static String getChineseWeek(String recordTime) {
        LocalDate date = LocalDate.parse(recordTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        // 3. 转为中文星期
        switch (dayOfWeek) {
            case MONDAY:
                return "星期一";
            case TUESDAY:
                return "星期二";
            case WEDNESDAY:
                return "星期三";
            case THURSDAY:
                return "星期四";
            case FRIDAY:
                return "星期五";
            case SATURDAY:
                return "星期六";
            case SUNDAY:
                return "星期日";
            default:
                return "";
        }
    }
}
