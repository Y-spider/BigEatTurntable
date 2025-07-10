package top.chopper.utils;

import org.springframework.security.core.context.SecurityContextHolder;

/*
   @Author:ROBOT
   @DateTime:2024/8/19 22:04
   @Version:1.0.0
   @Description:
   */
public class SecurityUtil {
    /**
     * Principal 如果是微信用户端，那么表示openid 如果是后台用户，那么表示的是用户账号
     * @return
     */
    public static String getUserName(){
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    public static String getUserRole(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
    }
}
