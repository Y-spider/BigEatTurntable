package top.chopper.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import top.chopper.constant.TurnTableType;
import top.chopper.mapper.RotationRecordMapper;
import top.chopper.mapper.TurnTableMapper;
import top.chopper.mapper.UserMapper;
import top.chopper.pojo.RotationRecord;
import top.chopper.pojo.TurnTable;
import top.chopper.pojo.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/*
   @Author:ROBOT
   @DateTime:2025/10/22 16:50
   @Version:1.0.0
   @Description: 定时邮件发送
   */
@Component
@Slf4j
public class EmailSendTask {
    // 邮件发送对象(自动装配)
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;
    // 定义邮件发送的基本属性
    private String from = "3267585160@qq.com";


    // 使用异步方式发送邮件
    @Async
    public void sendTextEmail(String to,String title,String content) {
        // 解决本地DNS未配置 ip->域名场景下，邮件发送太慢的问题
        System.getProperties().setProperty("mail.mime.address.usecanonicalhostname", "false");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setText(content);
        message.setSubject(title);
        try{
            mailSender.send(message);
            log.info("发送邮件->{}成功 =====》 内容:{}",to,content);
        }catch ( Exception e ){
            log.info("发送邮件->{}失败 =====》 Exception:{}",to,e.toString());
            throw new RuntimeException("发送邮件->"+to+"失败 =====》 Exception:" + e);
        }
    }

    public void sendHtmlEmail(String to,String title,String htmlContent){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setSubject(title);
            helper.setFrom(from);
            helper.setText(htmlContent, true);
            helper.setTo(to);
            mailSender.send(mimeMessage);
        } catch ( MessagingException e ) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 每天0点发送当日新注册用户数量
     * 转动转盘数量
     * 新建转盘数(用户自定义)
     */
    @Autowired
    private RotationRecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TurnTableMapper turnTableMapper;
    @Value("${spring.mail.custom-to}")
    private String customTo;
    @Scheduled(cron = "0 1 0 * * ?") // 秒 分 时 日 月 星期几
    public void sendSummaryEmail(){
        // ================== 获取昨天的时间范围 ==================
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime start = LocalDateTime.of(yesterday, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(yesterday, LocalTime.MAX);

        LambdaQueryWrapper<RotationRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<TurnTable> turnTableQueryWrapper = new LambdaQueryWrapper<>();
        recordQueryWrapper.between(RotationRecord::getCreateTime,start,end);
        userQueryWrapper.between(User::getCreateTime,start,end);
        turnTableQueryWrapper.between(TurnTable::getCreateTime,start,end);
        turnTableQueryWrapper.eq(TurnTable::getType, TurnTableType.TURN_TABLE_TYPE_OPT);
        Context templateContext = new Context(); // 模版上下文
        templateContext.setVariable("spinCount",recordMapper.selectCount(recordQueryWrapper));
        templateContext.setVariable("totalSpinCount",recordMapper.selectCount(null));
        templateContext.setVariable("newUserCount",userMapper.selectCount(userQueryWrapper));
        templateContext.setVariable("todayShareUserCount",userMapper.selectCount(userQueryWrapper.isNotNull(User::getInviter))); // 受邀请新用户，今日
        templateContext.setVariable("totalUser",userMapper.selectCount(null)-2);
        templateContext.setVariable("newTurntable",turnTableMapper.selectCount(turnTableQueryWrapper));
        String mailTemplateContent = templateEngine.process("MailTemplate", templateContext);
        sendHtmlEmail(customTo,"不纠结星球昨日汇总",mailTemplateContent);
        log.info("成功发送昨日汇总邮件到目标邮箱:{}",customTo);
    }


}
