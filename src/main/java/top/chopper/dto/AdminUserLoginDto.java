package top.chopper.dto;

import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*
   @Author:ROBOT
   @DateTime:2025/7/7 22:28
   @Version:1.0.0
   @Description: 封装用户登陆参数接收对象
   */
@Data
public class AdminUserLoginDto {
    private String account;
    private String password;

    /**
     * 对比数据库密码和当前密码是否一致
     */
    public boolean matchPasswordByBCrypt(String encodePassword){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.matches(password,encodePassword);
    }
}
