package top.chopper.filter;

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
import top.chopper.utils.JWTUtil;

import java.io.IOException;
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
    @Qualifier(value = "handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //  获取token
        String token = request.getHeader("token");
        response.setCharacterEncoding("utf-8");
        if(token!=null && !token.isEmpty()){
            // 解析token，查看是移动端用户还是后台用户
            String openid = JWTUtil.getDecodeJWTData(token, "tony chopper", "openid");
            if(openid!=null){
                // 表示是移动端用户登陆
                // 建议从数据库中获取数据，在该系统中暂时不使用redis
//                ClientUserDto clientUserDto = (ClientUserDto)redisTemplate.opsForValue().get("client_" + openid);
                // 封装为UsernamePasswordAuthenticationToken 对象添加到SecurityContextHolder中
//                if(clientUserDto!=null){
//                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(clientUserDto.getOpenid(), "123456", clientUserDto.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//                    // 放行
//                    filterChain.doFilter(request,response);
//                }
//                else{
//                    // 表示在redis缓存中的用户存储的信息已经过期了
////                    R<Object> expireR = new R<>();
////                    expireR.setCode(RCode.EXPIRETOKEN);
////                    expireR.setErrMsg("token已失效");
////                    response.getWriter().write(JSON.toJSONString(expireR));
//                }
            }
            else{
                // 说明是后台用户登录
                String key = JWTUtil.getDecodeJWTData(token, "tony chopper", "backUserToken");
                // 从redis中获取存储的后台用户信息
//                BackUserDto backUserDto = (BackUserDto)redisTemplate.opsForValue().get("back_" + key);
//                if(backUserDto!=null){
//                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(backUserDto.getAccount(), backUserDto.getPassword(), backUserDto.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//                    filterChain.doFilter(request,response);
//                }
//                else{
//                    // 表示在redis缓存中的后台用户存储的信息已经过期了
//                    R<Object> expireR = new R<>();
//                    expireR.setCode(RCode.EXPIRETOKEN);
//                    expireR.setErrMsg("token已失效");
//                    response.getWriter().write(JSON.toJSONString(expireR));
//                }
            }
        }else{
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken("chopper", "123456", null);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request,response);
        }
    }

}