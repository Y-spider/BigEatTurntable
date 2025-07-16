package top.chopper.filter;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import top.chopper.mapper.TokenMapper;
import top.chopper.pojo.R;
import top.chopper.pojo.RCode;
import top.chopper.pojo.Token;
import top.chopper.utils.JWTUtil;

import java.io.IOException;
import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2024/4/12 13:53
   @Version:1.0.0
   @Description: 自定义的JWT认证过滤器
   这里为什么要继承spring提供的OncePerRequestFilter过滤器而不是在学习javaweb中那样直接实现Filter接口呢?
   在Spring框架中，通常推荐使用 OncePerRequestFilter 类来实现自定义过滤器，而不是直接实现 Filter 接口。
   这是因为 OncePerRequestFilter 类提供了一些方便的功能，比如确保每个请求只被过滤一次，避免重复执行过滤逻辑。
   此外，它还提供了一些钩子方法，可以让开发者更容易地扩展和定制过滤器的行为。因此，继承 OncePerRequestFilter 是更加方便和推荐的做法。
   */
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    @Qualifier(value = "handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    // 这里会在Security执行链之前执行(执行顺序优先级比较高)
    private static final List<String> WHITE_LIST = List.of(
            "/user/admin/login",
            "/user/client/login",
            "/doc.html",
            "/swagger-ui.html",
            "/swagger-ui/",
            "/webjars/",
            "/v2/",
            "/v3/",
            "/swagger-resources/"
    );
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 白名单直接放行
        String servletPath = request.getServletPath();
        if (WHITE_LIST.stream().anyMatch(servletPath::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        //  获取token
        String token = request.getHeader("token");
        response.setCharacterEncoding("utf-8");
        if(token!=null && !token.isEmpty()){
            // 解析token，查看是移动端用户还是后台用户
            String role = JWTUtil.getDecodeJWTData(token, "tony chopper", "role");
            String identity = JWTUtil.getDecodeJWTData(token, "tony chopper", "identity");
            Token tableToken = tokenMapper.ISelectById(identity);
            if(tableToken==null){
                R<Object> expireR = new R<>();
                expireR.setCode(RCode.EXPIRETOKEN);
                expireR.setErrMsg("令牌已失效，请重新登录");
                response.setHeader("Content-Type","application/json;charset=utf-8");
                response.getWriter().write(JSONUtil.toJsonStr(expireR));
                response.getWriter().flush();
                response.getWriter().close();
                return;
            }
            // 这里密码就存放的是角色信息
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(identity,role,null);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request,response);
        }
        else{
            R<Object> expireR = new R<>();
            expireR.setCode(RCode.EXPIRETOKEN);
            expireR.setErrMsg("无效令牌");
            response.setHeader("Content-Type","application/json;charset=utf-8");
            response.getWriter().write(JSONUtil.toJsonStr(expireR));
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

}