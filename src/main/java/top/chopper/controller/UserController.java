package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.chopper.dto.AdminUserLoginDto;
import top.chopper.pojo.R;
import top.chopper.pojo.User;
import top.chopper.service.UserService;
import top.chopper.utils.SecurityUtil;

import java.util.HashMap;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:38
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/user")
@Tag(name = "用户相关接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(description = "后台管理端用户登陆",summary = "后台管理端用户登陆")
    @PostMapping("/admin/login")
    public R adminLogin(@RequestBody AdminUserLoginDto adminUserLoginDto){
        return userService.adminLogin(adminUserLoginDto);
    }

    @Operation(description = "微信小程序用户登录",summary = "微信小程序用户登录")
    @PostMapping("/client/login")
    public R clientLogin(@RequestBody HashMap<String,String> params){
        return userService.wxClientLogin(params);
    }

    @GetMapping("/get/userId")
    public R handleGetCurrentUserOpenid(){
        return R.SUCCESS(SecurityUtil.getUserName());
    }

    @Operation(description = "微信小程序用户修改头像",summary = "微信小程序用户修改头像")
    @PostMapping("/client/avatar")
    public R uploadClientAvatar(MultipartFile file){
        return userService.uploadAvatar(file);
    }

    @Operation(description = "微信小程序用户修改信息",summary = "微信小程序用户修改信息")
    @PostMapping("/client/update")
    @Transactional
    public R updateClientInfo(@RequestBody User user){
        userService.handleClientUpdate(user);
        return R.SUCCESS();
    }

    @GetMapping
    @Operation(description = "获取微信用户的信息",summary = "获取微信用户的信息")
    public R getClientInfo(){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getName,User::getEmail,User::getAvatar);
        queryWrapper.eq(User::getOpenid,SecurityUtil.getUserName());
        User one = userService.getOne(queryWrapper);
        return R.SUCCESS(one);
    }


}
