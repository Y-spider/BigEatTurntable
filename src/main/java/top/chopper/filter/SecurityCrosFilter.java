package top.chopper.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
   @Author:ROBOT
   @DateTime:2024/4/15 19:46
   @Version:1.0.0
   @Description: security 处理跨域过滤器
   */
@Component
public class SecurityCrosFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 响应类型
        response.setHeader("Access-Control-Allow-Methods", "*");
        // 请求头添加了 token 字段，所以需要让其能通过
        response.setHeader("Access-Control-Allow-Headers",
                "*");

        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        // 探针请求，直接返回，无需执行后续过滤器。
        if (request.getMethod().equals("OPTIONS"))
            return;
        filterChain.doFilter(request, response);
    }
}
