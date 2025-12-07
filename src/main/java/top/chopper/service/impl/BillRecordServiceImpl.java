package top.chopper.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.chopper.Exception.BusinessException;
import top.chopper.constant.BillConstant;
import top.chopper.dto.BillRecordExportExcelDto;
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
import top.chopper.utils.EmailSenderUtil.EmailUtil;
import top.chopper.utils.FasterWhisperUtil;
import top.chopper.utils.SecurityUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private EmailUtil emailUtil;

    @Value("${LLM.deepseek.api-key}")
    private String apiKey;

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
        BillBook currentChooseBookBill = billBookService.getCurrentChooseBookBill();
        Map<String, String> timeMap = handleGetMonthStartAndEnd(time);
        HashMap<String, String> result = new HashMap<>();
        result.put("startTime", timeMap.get("startTime").toString());
        result.put("endTime",timeMap.get("endTime").toString());
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
                .between(BillRecord::getRecordTime,  timeMap.get("startTime"),  timeMap.get("endTime"))
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
        List<BillRecord> billRecords = listByQueryWrappy(queryWrapper, map);
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
     * @param map
     */
    @Override
    public void sendRecordSummaryExcelToCurrentUser(Map<String, Object> map) {
        User currentUser = userService.getCurrentUser();
        if(ObjectUtil.isEmpty(currentUser.getEmail())){
            throw new BusinessException("请先绑定接收邮箱!");
        }
        LambdaQueryWrapper<BillRecord> queryWrapper = new LambdaQueryWrapper<>();
        List<BillRecord> billRecords = listByQueryWrappy(queryWrapper, map);
        if(billRecords.isEmpty()){
            throw new BusinessException("当前时间范围内无消费记录!");
        }
        BillBook exportBook = billBookService.getById((Integer) map.get("bookId"));
        ArrayList<BillRecordExportExcelDto> billRecordExportExcelList = new ArrayList<>();
        BillRecordExportExcelDto lastNextLine = new BillRecordExportExcelDto(); // 记录总支出，总收入，结余
        lastNextLine.setInTotal(BigDecimal.ZERO);
        lastNextLine.setOutTotal(BigDecimal.ZERO);
        billRecordExportExcelList.add(lastNextLine);
        for (BillRecord record : billRecords) {
            BillRecordExportExcelDto target = new BillRecordExportExcelDto();
            BeanUtils.copyProperties(record,target);
            if("in".equals(record.getType())){
                lastNextLine.setInTotal(lastNextLine.getInTotal().add(record.getAmount()));
            }else if("out".equals(record.getType())){
                lastNextLine.setOutTotal(lastNextLine.getOutTotal().add(record.getAmount()));
            }
            target.setBookName(exportBook.getTitle());
            billRecordExportExcelList.add(target);
        }
        lastNextLine.setSurplus(lastNextLine.getInTotal().subtract(lastNextLine.getOutTotal()));
        try {
            WriteCellStyle contentStyle = new WriteCellStyle();
            contentStyle.setHorizontalAlignment(HorizontalAlignment.CENTER); // 水平居中
            contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);     // 垂直居中

// 内容策略
            WriteCellStyle headStyle = new WriteCellStyle(); // 表头样式
            headStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            headStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            HorizontalCellStyleStrategy styleStrategy =
                    new HorizontalCellStyleStrategy(headStyle, contentStyle);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            EasyExcel.write(out, BillRecordExportExcelDto.class)
                    .registerWriteHandler(styleStrategy)
                    .sheet("账单")
                    .doWrite(billRecordExportExcelList);
            byte[] excelBytes = out.toByteArray();
            String contentText = String.format("亲爱的用户：您好！\n%s至%s账本[%s]账单%d条明细已发送到您的邮箱，请下载附件查阅。",map.get("startTime"),map.get("endTime"),exportBook.getTitle(),billRecords.size());
            emailUtil.sendExcelMail(currentUser.getEmail(),excelBytes,contentText,"转盘记账工具_账单明细",exportBook.getTitle()+"账单明细");
            log.info("用户:{}导出账本:{}记录{}条成功，导出时间范围为:[{}]-[{}]", SecurityUtil.getUserName(),exportBook.getTitle(),billRecords.size(),map.get("startTime"),map.get("endTime"));
        } catch (Exception e ) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ai解析上传的语音，生成billRecord记录返回给前端
     * @param file
     * @return
     */
    @Override
    public BillRecord voiceAddRecordByAi(MultipartFile file) {
        BillRecord billRecord = new BillRecord();
        try {
            // 翻译后的文本
            String transcribeText = FasterWhisperUtil.transcribe(file);
            if(ObjectUtil.isEmpty(transcribeText)){
                return billRecord;
            }
            String textJson = deepSeekMakeTranslate(transcribeText);
            billRecord = genBillRecordByJsonStr(textJson);
            return billRecord;
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        } catch ( InterruptedException e ) {
            log.error(e.toString());
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 通过解析用户输入的文本来解析生成billrecord
     * @param des
     * @return
     */
    @Override
    public BillRecord desAddRecordByAi(String des) {
        String jsonStr = deepSeekMakeTranslate(des);
        return genBillRecordByJsonStr(jsonStr);
    }

    /**
     * @param params 
     * @return
     */
    /**
     * 获取统计信息
     *
     * @param params 前端参数: {date="2025-12", type="expense", billBookId=1}
     * @return 统计结果 Map
     */
    @Override
    public Map<String, Object> getStatisticsInfo(Map<String, Object> params) {
        // 1. 基础参数校验
        if (ObjectUtil.isEmpty(params) || params.get("date") == null) {
            log.warn("统计参数缺失, params=>[{}]", params);
            throw new BusinessException("请选择日期");
        }
        String ym = params.get("date").toString(); // 例如 "2025-12"
        YearMonth yearMonth = YearMonth.parse(ym);
        // 当月第一天
        LocalDate startDate = yearMonth.atDay(1);
        // 当月最后一天
        LocalDate endDate = yearMonth.atEndOfMonth();
        params.put("startTime",startDate);
        params.put("endTime",endDate);
        BillBook currentChooseBookBill = billBookService.getCurrentChooseBookBill();
        params.put("bookId",currentChooseBookBill.getId());
        LambdaQueryWrapper<BillRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(BillRecord::getAmount);
        // 3. 获取当月所有记录
        List<BillRecord> billRecords = listByQueryWrappy(queryWrapper, params); // 假设这是你封装的查询方法，或者直接用 baseMapper.selectList(queryWrapper)

        // 4. 初始化返回结构
        Map<String, Object> resultMap = new HashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 如果没有记录，直接返回空结构，防止空指针
        if (billRecords == null || billRecords.isEmpty()) {
            resultMap.put("totalAmount", 0);
            resultMap.put("categoryList", new ArrayList<>());
            resultMap.put("assetList", new ArrayList<>());
            resultMap.put("dailyData", new HashMap<>());
            resultMap.put("rankList", new ArrayList<>());
            return resultMap;
        }

        // 5. 计算总金额
        for (BillRecord record : billRecords) {
            if (record.getAmount() != null) {
                totalAmount = totalAmount.add(record.getAmount());
            }
        }
        resultMap.put("totalAmount", totalAmount);

        // 防止总金额为0导致除以0异常
        boolean isTotalZero = totalAmount.compareTo(BigDecimal.ZERO) == 0;

        // ---------------------------------------------------------
        // 6. 处理分类统计 (Category List - 饼图 & 列表)
        // ---------------------------------------------------------
        Map<String, List<BillRecord>> groupedByCategory = billRecords.stream()
                .collect(Collectors.groupingBy(record ->
                        StrUtil.isBlank(record.getLabelName()) ? "其他" : record.getLabelName()
                ));

        List<Map<String, Object>> categoryList = new ArrayList<>();
        for (Map.Entry<String, List<BillRecord>> entry : groupedByCategory.entrySet()) {
            String labelName = entry.getKey();
            List<BillRecord> records = entry.getValue();

            BigDecimal catTotal = records.stream()
                    .map(BillRecord::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> item = new HashMap<>();
            item.put("name", labelName);
            item.put("amount", catTotal);
            item.put("count", records.size());
            // 取第一条记录的图标作为分类图标
            item.put("icon", records.isEmpty() ? "goods" : records.get(0).getLabelUrl());
            // 计算百分比 (保留2位小数)
            item.put("percent", isTotalZero ? 0 :
                    catTotal.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));

            categoryList.add(item);
        }
        // 按金额排序分类列表
        categoryList.sort((o1, o2) -> ((BigDecimal) o2.get("amount")).compareTo((BigDecimal) o1.get("amount")));
        resultMap.put("categoryList", categoryList);

        // ---------------------------------------------------------
        // 7. 处理资产账户统计 (Asset List)
        // ---------------------------------------------------------
        Map<String, List<BillRecord>> groupedByMethod = billRecords.stream()
                .collect(Collectors.groupingBy(record ->
                        StrUtil.isBlank(record.getMethodLabel()) ? "未知账户" : record.getMethodLabel()
                ));

        List<Map<String, Object>> assetList = new ArrayList<>();
        for (Map.Entry<String, List<BillRecord>> entry : groupedByMethod.entrySet()) {
            String methodName = entry.getKey();
            List<BillRecord> records = entry.getValue();

            BigDecimal methodTotal = records.stream()
                    .map(BillRecord::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> item = new HashMap<>();
            item.put("name", methodName);
            item.put("amount", methodTotal);
            item.put("icon", records.isEmpty() ? "moneybag" : records.get(0).getMethodUrl());
            item.put("percent", isTotalZero ? 0 :
                    methodTotal.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));

            assetList.add(item);
        }
        // 按金额排序
        assetList.sort((o1, o2) -> ((BigDecimal) o2.get("amount")).compareTo((BigDecimal) o1.get("amount")));
        resultMap.put("assetList", assetList);

        // ---------------------------------------------------------
        // 8. 处理每日数据 (Daily Data - 柱状图)
        // ---------------------------------------------------------
        Map<String, BigDecimal> dailyDataMap = new HashMap<>();
        // 假设 recordTime 格式为 "yyyy-MM-dd HH:mm:ss" 或 "yyyy-MM-dd"
        // 我们只需要提取 "dd" 部分，例如 "2025-12-02" -> Key="2" (为了匹配前端循环的索引)
        // 或者保留 "02"，取决于前端处理。之前前端代码处理 key 兼容了 "1" 和 "01"，这里我们存 int 字符串

        for (BillRecord record : billRecords) {
            String time = record.getRecordTime();
            if (StrUtil.length(time) >= 10) {
                // 提取日期的日部分. 假设格式固定.
                // 最好使用 DateTimeFormatter, 但这里简单处理字符串: "2025-12-02" -> substring(8, 10) -> "02"
                String dayStr = time.substring(8, 10);
                // 去除前导0，变成 "2", "15" 等，因为前端循环 i 从 1 到 31
                String dayKey = String.valueOf(Integer.parseInt(dayStr));

                BigDecimal currentVal = dailyDataMap.getOrDefault(dayKey, BigDecimal.ZERO);
                dailyDataMap.put(dayKey, currentVal.add(record.getAmount()));
            }
        }
        resultMap.put("dailyData", dailyDataMap);


        resultMap.put("rankList", billRecords);

        return resultMap;
    }

    /**
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> getDurationSummary(Map<String, Object> params) {
        // 1. 校验参数
        if (ObjectUtil.isEmpty(params.get("startTime"))) {
            throw new BusinessException("起始时间不能为空!");
        }
        if (ObjectUtil.isEmpty(params.get("endTime"))) {
            throw new BusinessException("结束时间不能为空!");
        }

        // 2. 解析时间范围
        // 假设前端传入的是 "2024-12" 格式
        YearMonth yearMonthOfStart = YearMonth.parse(params.get("startTime").toString());
        YearMonth yearMonthOfEnd = YearMonth.parse(params.get("endTime").toString());

        // 转换为字符串用于数据库查询 (匹配 recordTime String类型)
        // 开始日期: "2024-12-01"
        String startDateStr = yearMonthOfStart.atDay(1).toString();
        // 结束日期: "2025-12-31" (自动计算当月最后一天)
        String endDateStr = yearMonthOfEnd.atEndOfMonth().toString();

        // 3. 获取当前账本并查询数据
        BillBook currentChooseBookBill = billBookService.getCurrentChooseBookBill();

        LambdaQueryWrapper<BillRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillRecord::getBillBookId, currentChooseBookBill.getId());
        // 针对 String 类型的 recordTime 进行范围查询
        // 注意：这里假设 recordTime 格式为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss，字符串比较在 ISO 格式下是有效的
        queryWrapper.between(BillRecord::getRecordTime, startDateStr, endDateStr);
        queryWrapper.orderByDesc(BillRecord::getRecordTime);

        List<BillRecord> billRecords = billRecordMapper.selectList(queryWrapper);

        // 4. 初始化总统计数据
        BigDecimal allTotalExpense = BigDecimal.ZERO;
        BigDecimal allTotalIncome = BigDecimal.ZERO;

        // 5. 按月份分组处理数据
        // key: "2025-12", value: List<BillRecord>
        Map<String, List<BillRecord>> groupedByMonth = billRecords.stream()
                .filter(r -> StrUtil.isNotBlank(r.getRecordTime()) && r.getRecordTime().length() >= 7)
                .collect(Collectors.groupingBy(r -> r.getRecordTime().substring(0, 7)));

        List<Map<String, Object>> monthList = new ArrayList<>();

        // 遍历分组数据
        for (Map.Entry<String, List<BillRecord>> entry : groupedByMonth.entrySet()) {
            String monthStr = entry.getKey();
            List<BillRecord> records = entry.getValue();

            BigDecimal monthExpense = BigDecimal.ZERO;
            BigDecimal monthIncome = BigDecimal.ZERO;

            for (BillRecord r : records) {
                BigDecimal amount = r.getAmount() == null ? BigDecimal.ZERO : r.getAmount();

                // 根据类型累加 (DB存的是 "out" 和 "in")
                if ("out".equals(r.getType())) {
                    monthExpense = monthExpense.add(amount);
                    allTotalExpense = allTotalExpense.add(amount); // 累加到总支出
                } else if ("in".equals(r.getType())) {
                    monthIncome = monthIncome.add(amount);
                    allTotalIncome = allTotalIncome.add(amount); // 累加到总收入
                }
            }

            // 构建单月数据对象
            Map<String, Object> monthItem = new HashMap<>();
            monthItem.put("month", monthStr);
            monthItem.put("expense", monthExpense);
            monthItem.put("income", monthIncome);
            monthItem.put("surplus", monthIncome.subtract(monthExpense)); // 结余 = 收入 - 支出

            monthList.add(monthItem);
        }

        // 6. 对月份列表进行降序排序 (最新的月份在前)
        monthList.sort((o1, o2) -> {
            String m1 = (String) o1.get("month");
            String m2 = (String) o2.get("month");
            return m2.compareTo(m1);
        });

        // 7. 构建返回结果
        Map<String, Object> resultMap = new HashMap<>();

        // 顶部总览
        Map<String, Object> totalStats = new HashMap<>();
        totalStats.put("totalExpense", allTotalExpense);
        totalStats.put("totalIncome", allTotalIncome);
        totalStats.put("totalSurplus", allTotalIncome.subtract(allTotalExpense));

        resultMap.put("totalStats", totalStats);
        resultMap.put("monthList", monthList);

        return resultMap;
    }
    /**
     * 处理获取当前所属月份起始时间与结束时间
     *
     * @param time
     * @return
     */
    private Map<String, String> handleGetMonthStartAndEnd(LocalDateTime time) {
        LocalDate date = time.toLocalDate();

        // 当月第一天
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        // 当月最后一天
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        HashMap<String, String> result = new HashMap<>();
        result.put("startTime", startOfMonth.format(formatter));
        result.put("endTime", endOfMonth.format(formatter));
        return result;

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


    private List<BillRecord> listByQueryWrappy(LambdaQueryWrapper<BillRecord> queryWrapper,Map<String,Object> map){
        if(ObjectUtil.isNotEmpty(map.get("bookId"))){
            queryWrapper.eq(BillRecord::getBillBookId,map.get("bookId"));
        }
        if(ObjectUtil.isNotEmpty(map.get("userId")) && !"-1".equals(map.get("userId").toString())){
            User user = userService.getById((Integer) map.get("userId"));
            queryWrapper.eq(BillRecord::getOpenid,user.getOpenid());
        }
        if(ObjectUtil.isNotEmpty(map.get("labelName")) && !"全部分类".equals(map.get("labelName"))){
            queryWrapper.eq(BillRecord::getLabelName,map.get("labelName"));
        }
        if(ObjectUtil.isNotEmpty(map.get("type"))){
            queryWrapper.eq(BillRecord::getType,map.get("type"));
        }
        DateTimeFormatter flexibleFormatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-")
                .appendValue(ChronoField.MONTH_OF_YEAR)
                .appendLiteral('-')
                .appendValue(ChronoField.DAY_OF_MONTH)
                .toFormatter();

        if (ObjectUtil.isNotEmpty(map.get("startTime"))
                && ObjectUtil.isNotEmpty(map.get("endTime"))) {

            LocalDate startDate = LocalDate.parse(map.get("startTime").toString(), flexibleFormatter);
            LocalDateTime startTime = startDate.atStartOfDay();

            LocalDate endDate = LocalDate.parse(map.get("endTime").toString(), flexibleFormatter);
            LocalDateTime endTime = endDate.atStartOfDay();

            if (!endTime.isBefore(startTime)) {
                queryWrapper.between(BillRecord::getRecordTime, startDate, endDate);
            }
        }

        if(ObjectUtil.isNotEmpty(map.get("methodType")) && !"全部账户".equals(map.get("methodType"))){
            queryWrapper.eq(BillRecord::getMethodLabel,map.get("methodType"));
        }
        if(ObjectUtil.isNotEmpty(map.get("remark"))){
            queryWrapper.like(BillRecord::getRemark,map.get("remark"));
        }
        if(ObjectUtil.isNotEmpty(map.get("inOutType")) && !"全部".equals(map.get("inOutType"))){
            queryWrapper.eq(BillRecord::getType,"支出".equals(map.get("inOutType"))?"out":"in");
        }
        queryWrapper.
                orderByDesc(BillRecord::getRecordTime)
                .orderByDesc(BillRecord::getUpdateTime)
                .orderByDesc(BillRecord::getCreateTime);
        return billRecordMapper.selectList(queryWrapper);
    }


    /**
     *
     * @param desText 描述文本 "我今天中午吃了一个螺蛳粉，花费了15块！"
     * @return
     *  结果JSON字符串
     *      * {
     *      *   "type": "out",
     *      *   "amount": 15,
     *      *   "methodLabel": "微信支付",
     *      *   "remark": "中午吃螺蛳粉"
     *      * }
     */
    private String deepSeekMakeTranslate(String desText){
        // 构建请求 JSON
        JSONObject requestJson = new JSONObject();
        requestJson.set("model", "deepseek-chat");

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content",
                "你是一个记账小助手，请解析用户提供的文本，严格提取关键信息，并按 JSON 返回。" +
                        "输出格式示例:{\"type\":\"out\",\"amount\":0,\"methodLabel\":\"微信支付\",\"remark\":\"总结描述\",\"labelName\":\"餐饮\"}。" +
                        "字段说明:" +
                        "type: 'in' 表示收入(默认)，'out' 表示支出;" +
                        "amount: 金额，必须为数字;" +
                        "methodLabel: 支付方式，取值: 微信支付(默认), 支付宝, 花呗, 现金, 京东白条, 储蓄卡, 信用卡, 其他;" +
                        "remark: 总结描述文本;" +
                        "labelName: 当 type='out' 时，从以下标签中选一个: 餐饮(默认), 交通, 蔬菜, 服饰, 购物, 娱乐, 运动, 宠物, 快递, 烟酒, 数码, 保险, 其他, 发红包, 旅行, 住房, 家电, 水果, 学习, 医疗, 缴费, 转账, 礼物;" +
                        "当 type='in' 时，从以下标签中选一个: 工资(默认), 理财, 退款, 奖金, 礼金, 兼职, 生活费, 生意;" +
                        "只返回 JSON，不要添加任何额外说明，JSON 必须合法可解析。"
        );

        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", desText);

        messages.add(systemMessage);
        messages.add(userMessage);

        requestJson.set("messages", messages);
        requestJson.set("stream", false);

        // 发送 POST 请求
        HttpResponse response = HttpRequest.post("https://api.deepseek.com/chat/completions")
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Authorization", "Bearer " + "sk-07de5615f2b04afaa9c2b3c1246c4757")
                .body(requestJson.toString(), "application/json;charset=utf-8")
                .execute();

        if (response.getStatus() == 200) {
            JSONObject jsonResponse = JSONUtil.parseObj(response.body());
            // 获取返回的文本内容
            return jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");
        } else {
            throw new RuntimeException("DeepSeek API 调用失败，状态码：" + response.getStatus() +
                    "，响应内容：" + response.body());
        }
    }
    private BillRecord genBillRecordByJsonStr(String jsonStr){
        BillRecord billRecord = new BillRecord();
        if(ObjectUtil.isEmpty(jsonStr)){
            return billRecord;
        }
        JSONObject entries = JSONUtil.parseObj(jsonStr);
        if (ObjectUtil.isNotEmpty(entries.get("type"))) {
            billRecord.setType(entries.get("type").toString());
        } else {
            billRecord.setType("out");
        }
        if(ObjectUtil.isNotEmpty(entries.get("amount"))){
            billRecord.setAmount(BigDecimal.valueOf((Long)entries.get("amount")));
        }else{
            billRecord.setAmount(BigDecimal.ZERO);
        }
        if(ObjectUtil.isNotEmpty(entries.get("methodLabel"))){
            billRecord.setMethodLabel(entries.get("methodLabel").toString());
        }else{
            billRecord.setMethodLabel("微信支付");
        }
        if(ObjectUtil.isNotEmpty(entries.get("remark"))){
            billRecord.setRemark(entries.get("remark").toString());
        }
        if(ObjectUtil.isNotEmpty(entries.get("labelName"))){
            billRecord.setLabelName(entries.get("labelName").toString());
        }
        return billRecord;
    }



}

