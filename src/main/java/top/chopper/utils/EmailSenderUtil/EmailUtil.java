package top.chopper.utils.EmailSenderUtil;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/*
   @Author:ROBOT
   @DateTime:2025/12/3 13:04
   @Version:1.0.0
   @Description:
   */
@Component
public class EmailUtil {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;
    public void sendExcelMail(String toEmail, byte[] excelBytes,String content,String subjectName,String excelName) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(from);
        helper.setTo(toEmail);
        helper.setSubject(subjectName);
        helper.setText(content);
        // 使用 InputStream 作为附件
        ByteArrayResource resource = new ByteArrayResource(excelBytes);
        helper.addAttachment(excelName + ".xlsx", resource);

        mailSender.send(message);
    }


}
