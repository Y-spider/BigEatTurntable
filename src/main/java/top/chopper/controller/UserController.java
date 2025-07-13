package top.chopper.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.chopper.dto.AdminUserLoginDto;
import top.chopper.pojo.R;
import top.chopper.service.UserService;

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
        return userService.wxClientLogin(params.get("code"));
    }

}
