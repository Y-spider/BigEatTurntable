package top.chopper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.chopper.service.TestService;

import java.util.List;

@SpringBootTest(classes = ServerApplication.class)
class ServerApplicationTests {
    @Autowired
    private TestService testService;
    @Test
    void contextLoads() {
        System.out.println(testService.myGetById(1));
    }

}
