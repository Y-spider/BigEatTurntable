package top.chopper.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.chopper.mapper.ApplyDishLogMapper;
import top.chopper.pojo.ApplyDishLog;
import top.chopper.pojo.SysNotice;
import top.chopper.service.SysNoticeService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MyScheduledJob {
    @Autowired
    private ApplyDishLogMapper applyDishLogMapper;
    @Autowired
    private SysNoticeService sysNoticeService;
    /**
     * 每周一凌晨1点执行
     * cron 表达式: 秒 分 时 日 月 周
     * 0 0 1 ? * MON  -> 表示每周一 01:00:00 执行
     */

//    @Scheduled(cron = "0 0 1 ? * MON")
    public void weeklyJob() {
        log.info("周一凌晨一点执行为文档更新~~~~");

        // 当前时间（周一 01:00）
        LocalDate now = LocalDate.now();

        // 上周一 00:00
        LocalDate lastMonday = now.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDateTime startTime = lastMonday.atStartOfDay();

        // 上周日 23:59:59
        LocalDate lastSunday = now.minusWeeks(1).with(DayOfWeek.SUNDAY);
        LocalDateTime endTime = lastSunday.atTime(LocalTime.MAX);

        log.info("查询时间范围: {} ~ {}", startTime, endTime);

        // 查询日志
        LambdaQueryWrapper<ApplyDishLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(ApplyDishLog::getCreateTime, startTime, endTime);

        List<ApplyDishLog> logs = applyDishLogMapper.selectList(queryWrapper);
        log.info("本次查询到 {} 条记录", logs.size());

        if (logs.isEmpty()) {
            log.info("上周无菜品操作日志，不生成通知");
            return;
        }

        // 提取菜品名称（去重）
        List<String> dishNames = logs.stream()
                .map(ApplyDishLog::getApplyDishName)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 生成带序号的多行内容
        StringBuilder sb = new StringBuilder("上周新增/修改的菜品有：\n");
        for (int i = 0; i < dishNames.size(); i++) {
            sb.append(i + 1).append(". ").append(dishNames.get(i).split("-")[0]).append("\n");
        }
        String content = sb.toString().trim(); // 去掉最后一个换行

        // 旧通知设为 inactive
        LambdaUpdateWrapper<SysNotice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysNotice::getActive, true)
                .set(SysNotice::getActive, false);
        sysNoticeService.update(updateWrapper);

        // 插入新通知
        SysNotice sysNotice = new SysNotice();
        sysNotice.setActive(true);
        sysNotice.setCreateTime(LocalDateTime.now());
        sysNotice.setContent(content);
        sysNoticeService.save(sysNotice);

        log.info("生成新通知:\n{}", content);
    }


}
