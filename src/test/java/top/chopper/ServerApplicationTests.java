package top.chopper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.chopper.task.EmailSendTask;

@SpringBootTest(classes = ServerApplication.class)
class ServerApplicationTests {
    @Autowired
    private EmailSendTask emailSendTask;
    @Test
    void testSendEmail(){
        emailSendTask.sendTextEmail("3267585160@qq.com","测试","hello 成功");
    }

}
