package top.chopper.config;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import top.chopper.pojo.R;

import java.io.IOException;

/*
   @Author:ROBOT
   @DateTime:2024/4/12 21:40
   @Version:1.0.0
   @Description: 认证失败处理类
   */
@Component
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        R<String> result = new R<>();
        result.setCode(HttpStatus.UNAUTHORIZED.value()); // 认证失败
        result.setErrMsg("用户认证失败,请重新认证");
//        String jsonString = JSON.toJSONString(result);
        response.setContentType("application/json;charset=utf-8");
        log.warn("处理用户认证失败==>{}",authException.toString());
        response.getWriter().write(JSONUtil.toJsonStr(result));
        try {
            response.getWriter().close();
        }catch ( Exception ignored ){
//            忽略掉这里的认证失败并不打印失败信息
//            log.info(ignored.getMessage());
        }
    }

}
