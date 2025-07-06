package top.chopper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.chopper.utils.MyGetCommentUtil;

@SpringBootTest(classes = ServerApplication.class)
class ServerApplicationTests {
    @Autowired
    private MyGetCommentUtil myGetCommentUtil;

    @Test
    void generateTableFiled(){
//        myGetCommentUtil.generatePerfect("dish_type",true);
        myGetCommentUtil.generatePerfect("sys_dish",true);
//        myGetCommentUtil.generatePerfect("turntable",true);
    }


}
