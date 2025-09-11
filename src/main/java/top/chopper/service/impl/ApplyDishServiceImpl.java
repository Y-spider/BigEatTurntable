package top.chopper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.constant.ApplyDishConstant;
import top.chopper.mapper.ApplyDishMapper;
import top.chopper.mapper.UserMapper;
import top.chopper.pojo.ApplyDish;
import top.chopper.pojo.User;
import top.chopper.service.ApplyDishService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;


/*
   @Author:ROBOT
   @DateTime:2025/9/11 23:13
   @Version:1.0.0
   @Description:
   */
@Service
public class ApplyDishServiceImpl extends ServiceImpl<ApplyDishMapper, ApplyDish> implements ApplyDishService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ApplyDishMapper applyDishMapper;


    /**
     * @param applyDish
     */
    @Override
    @Transactional
    public void myAddApplyDish(ApplyDish applyDish) {
        String openid = SecurityUtil.getUserName();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid,openid);
        Integer userId = userMapper.selectOne(queryWrapper).getId();
        applyDish.setCreateTime(LocalDateTime.now());
        applyDish.setUserId(userId);
        applyDish.setStatus(ApplyDishConstant.NEED_HANDLE);
        applyDishMapper.insert(applyDish);
    }
}
