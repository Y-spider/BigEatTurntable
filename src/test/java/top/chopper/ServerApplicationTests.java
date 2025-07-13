package top.chopper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.chopper.pojo.TurnTable;
import top.chopper.service.TurnTableService;
import top.chopper.utils.MyGetCommentUtil;

@SpringBootTest(classes = ServerApplication.class)
class ServerApplicationTests {
    @Autowired
    private MyGetCommentUtil myGetCommentUtil;

    @Autowired
    private TurnTableService turnTableService;
    @Test
    void generateTableFiled(){
//        myGetCommentUtil.generatePerfect("dish_type",true);
        myGetCommentUtil.generatePerfect("rotation_record",true);
//        myGetCommentUtil.generatePerfect("turntable",true);
    }

    // 测试mybtais-plus的分页插件功能
    @Test
    void testMybatisPlusPage(){
        LambdaQueryWrapper<TurnTable> queryWrapper = new LambdaQueryWrapper<>();
        Page<TurnTable> page = new Page<>(2,10);
        turnTableService.page(page,queryWrapper);
        System.out.println(page);
    }

}
