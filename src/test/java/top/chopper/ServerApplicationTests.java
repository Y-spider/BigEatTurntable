package top.chopper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.chopper.task.EmailSendTask;
import top.chopper.utils.BaiduTranslateUtil;
import top.chopper.utils.catspider.CatJoyInitHandler;
import top.chopper.utils.catspider.RequestWikiHandler;

@SpringBootTest(classes = ServerApplication.class)
class ServerApplicationTests {
    @Autowired
    private EmailSendTask emailSendTask;
    @Autowired
    private BaiduTranslateUtil baiduTranslateUtil;
    @Autowired
    private CatJoyInitHandler catJoyInitHandler;
//    @Test
//    void testSendEmail(){
//        emailSendTask.sendTextEmail("3267585160@qq.com","测试","hello 成功");
//    }

//    @Test
//    void testTranslate(){
//        System.out.println(baiduTranslateUtil.aiTextTranslate("Almost 10% of a cat's bones are in its tail, and the tail is used to maintain balance."));
//    }

//    @Test
//    void testCatJoyInit(){
//        catJoyInitHandler.initCatJoy(100); // 5 *2 = 10
//    }
    @Autowired
    private RequestWikiHandler wikiHandler;
    @Test
    void testCatBreedInit(){
        wikiHandler.initCatBreed();
    }

}
