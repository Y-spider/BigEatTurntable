package top.chopper.task;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.chopper.mapper.FestivalMapper;
import top.chopper.pojo.Festival;
import top.chopper.service.impl.FestivalService;
import top.chopper.utils.WxSendTemplateMessageUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/11/2 14:48
   @Version:1.0.0
   @Description: 用户自定义节日提醒发送任务
   */
@Component
@Slf4j
public class FestivalTemplateTask {

    @Autowired
    private WxSendTemplateMessageUtil wxSendTemplateMessageUtil;

    @Autowired
    private FestivalMapper festivalMapper;

    @Scheduled(cron = "0 0 7,13 * * ?") // 秒 分 时 日 月 星期几 每天7点进行
    public void sentFestivalTemplate(){
        // 获取今天的起止时间
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); // 00:00:00
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1); // 23:59:59
        LambdaQueryWrapper<Festival> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Festival::getTime, startOfDay, endOfDay);
        queryWrapper.eq(Festival::getStatus,1);
        List<Festival> festivals = festivalMapper.selectList(queryWrapper);
        festivals.forEach(festival -> sendTemplate(festival));

    }

    private boolean sendTemplate(Festival festival){
        List<WxMaSubscribeMessage.MsgData> subscribeDataList = new ArrayList<>();
        WxMaSubscribeMessage.MsgData subscribeData_1 = new WxMaSubscribeMessage.MsgData();
        WxMaSubscribeMessage.MsgData subscribeData_2 = new WxMaSubscribeMessage.MsgData();
        WxMaSubscribeMessage.MsgData subscribeData_3 = new WxMaSubscribeMessage.MsgData();
        subscribeData_1.setName("thing1");
        subscribeData_1.setValue(festival.getName());
        subscribeData_2.setName("character_string2");
        subscribeData_2.setValue(festival.getTime().toString());
        subscribeData_3.setName("thing4");
        subscribeData_3.setValue("不纠结星球小助手来提醒您啦～该准备啦！");

        subscribeDataList.add(subscribeData_1);
        subscribeDataList.add(subscribeData_2);
        subscribeDataList.add(subscribeData_3);
        wxSendTemplateMessageUtil.setToUser(festival.getOpenid());
        wxSendTemplateMessageUtil.setTemplateId("eV1m7anh8wsli5zjfehBzUmzBiFNkt2fOzviSkj727M");
        wxSendTemplateMessageUtil.setDataList(subscribeDataList);
        wxSendTemplateMessageUtil.setToPagePath("pages-tools/tools-components/festival-timer/festival-detail?id="+festival.id + "&backUrl=pages/index/index");
        try {
            wxSendTemplateMessageUtil.sendTemplateMessage();
        }catch ( Exception e ){
            log.error("发送自定义节日:{} 给用户:{} 提醒模版失败:cause by==>{}",festival.getName(),festival.getOpenid(),e.toString());
            return false;
        }
        return true;
    }

    @Autowired
    private FestivalService festivalService;
    @Scheduled(cron = "0 0 3 * * ?") // 秒 分 时 日 月 星期几 每天3点进行
    public void initFestival(){
        LambdaQueryWrapper<Festival> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Festival::getType,"custom");
        Festival festival = festivalMapper.selectOne(queryWrapper);
        if(festival.getTime().getYear() != LocalDateTime.now().getYear()){
            festivalService.init();
            log.info("初始化节日成功~~~在新的一年中");
        }

    }

}
