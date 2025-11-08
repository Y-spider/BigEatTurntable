package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import top.chopper.dto.AdminUserLoginDto;
import top.chopper.pojo.R;
import top.chopper.pojo.User;

import java.util.Map;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:29
   @Version:1.0.0
   @Description:
   */
public interface UserService extends IService<User> {
    R adminLogin(AdminUserLoginDto adminUserLoginDto);

    R wxClientLogin(Map params);

    R uploadAvatar(MultipartFile file);

    void handleClientUpdate(User user);
}
