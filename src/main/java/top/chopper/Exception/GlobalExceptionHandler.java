package top.chopper.Exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.chopper.pojo.R;
import top.chopper.pojo.RCode;
import top.chopper.utils.SecurityUtil;

import java.sql.SQLException;

/*
   @Author:ROBOT
   @DateTime:2024/4/17 22:23
   @Version:1.0.0
   @Description:全局异常处理类
   */
@ResponseBody
@Slf4j
@ControllerAdvice(annotations = {RestController.class, RestControllerAdvice.class})  // 监听controller中抛出的异常
@Hidden  // 如果没有加上这注解 会造成打开不来knief4j接口文档
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLException.class)
    private R doException(Exception e){
        log.error(e.getMessage());
        if(e.getMessage().contains("Duplicate entry")){
            // 表示是一个重复错误
            String[] s = e.getMessage().split(" ");
            String msg = s[9] + "已存在";
            return R.FAIL(msg);
        }
        return R.FAIL("未知错误....");

    }
    @ExceptionHandler(BusinessException.class)
    private R doBusinessException(Exception e){
        log.error("发生业务异常==>{}",e.getMessage());
//        if(e.getMessage().equals("当前账号无任何后台权限!")){
//            return R.FAIL(e.getMessage(),RCode.NOMENUS);
//        }
        return R.FAIL(e.getMessage());
    }
    @ExceptionHandler(TokenExpireException.class)
    private R doTokenExpireException(Exception e){
        String userName = SecurityUtil.getUserName();
        log.error("用户:"+userName+"token已失效");
        R<String> result =  new R<String >();
        result.setData(e.toString());
        result.setCode(RCode.EXPIRETOKEN);
        return result;
    }

}
