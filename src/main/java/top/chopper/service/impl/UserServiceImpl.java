package top.chopper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.chopper.mapper.UserMapper;
import top.chopper.pojo.User;
import top.chopper.service.UserService;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:29
   @Version:1.0.0
   @Description:
   */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
