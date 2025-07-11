package top.chopper.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.dto.AdminUserLoginDto;
import top.chopper.mapper.TokenMapper;
import top.chopper.mapper.UserMapper;
import top.chopper.pojo.R;
import top.chopper.pojo.Token;
import top.chopper.pojo.User;
import top.chopper.service.UserService;
import top.chopper.utils.JWTUtil;

import java.time.LocalDateTime;
import java.util.HashMap;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:29
   @Version:1.0.0
   @Description:
   */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TokenMapper tokenMapper;
    @Value("${system.jwt.secretKey}")
    private String secretKey;

    @Override
    @Transactional
    public R adminLogin(AdminUserLoginDto adminUserLoginDto) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount,adminUserLoginDto.getAccount());
        User user = userMapper.selectOne(queryWrapper);
        if (user!=null && adminUserLoginDto.matchPasswordByBCrypt(user.getPasswd())){
            // 生成token信息 并更新数据库中用户的token  key:adminUser value 用户账号
            HashMap<String, String> claimMap = new HashMap<>();
            claimMap.put("adminUser",user.getAccount());
            claimMap.put("role","admin");
            String jwtToken = JWTUtil.createJWT(claimMap, secretKey);
            Token token = tokenMapper.selectById(user.getId());
            token.setToken(jwtToken);
            tokenMapper.updateById(token);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
            return R.SUCCESS(jwtToken);
        }
        else{
            return R.FAIL("账号密码错误");
        }
    }

//    public static void main(String[] args) {
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        System.out.println(bCryptPasswordEncoder.encode("Yanwenguang123_"));
//    }
}
