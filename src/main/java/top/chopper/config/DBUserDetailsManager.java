package top.chopper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/*
   @Author:ROBOT
   @DateTime:2024/4/10 16:51
   @Version:1.0.0
   @Description: 自定义用户信息类
   */
@Slf4j
public class DBUserDetailsManager implements UserDetailsService {
//    @Resource
//    private ClientUserMapper clientUserMapper;
//    @Resource
//    private PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String openid) throws UsernameNotFoundException {
//        // 该方法从数据库获取用户信息,封装为UserDetails进行返回
//        ClientUserDto clientUserDto = clientUserMapper.selectByOpenid(openid);
//        List<Permission> permissions = permissionMapper.selectPermissionByOpenid(openid);
//        permissions.forEach(permission -> {
//            clientUserDto.getPermissions().add(permission.getName());
//        });
        return null;
    }
//                user.isEnabled(),  // 没有对应的方法时，自己给true
//                user.isAccountNonExpired(), // 用户账号是否过期
//                user.isCredentialsNonExpired(), // 用户凭证是否过期
//                user.isAccountNonLocked(), // 用户是否被锁定
//                user.getAuthorities()); // 用户权限列表
}
