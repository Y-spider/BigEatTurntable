package top.chopper.service.impl;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.chopper.Exception.BusinessException;
import top.chopper.dto.AdminUserLoginDto;
import top.chopper.mapper.TokenMapper;
import top.chopper.mapper.UserMapper;
import top.chopper.pojo.R;
import top.chopper.pojo.Token;
import top.chopper.pojo.User;
import top.chopper.service.UserService;
import top.chopper.utils.JWTUtil;
import top.chopper.utils.MinioUtil;
import top.chopper.utils.SecurityUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:29
   @Version:1.0.0
   @Description:
   */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TokenMapper tokenMapper;
    @Value("${system.jwt.secretKey}")
    private String secretKey;
    @Value("${wexi.appid}")
    private String appid;
    @Value("${wexi.secret}")
    private String secret;
    @Value("${wexi.grant_type}")
    private String grant_type;
    @Autowired
    private MinioUtil minioUtil;

    @Override
    @Transactional
    public R adminLogin(AdminUserLoginDto adminUserLoginDto) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount,adminUserLoginDto.getAccount());
        User user = userMapper.selectOne(queryWrapper);
        if (user!=null && adminUserLoginDto.matchPasswordByBCrypt(user.getPasswd())){
            // 生成token信息 并更新数据库中用户的token  key:adminUser value 用户账号
            HashMap<String, String> claimMap = new HashMap<>();
            claimMap.put("identity",user.getAccount());
            claimMap.put("role","admin");
            claimMap.put("createTime",LocalDateTime.now().toString());
            String jwtToken = JWTUtil.createJWTWitExpire(claimMap, secretKey,2);
            Token token = tokenMapper.selectById(user.getId());
            if(token==null){
                token = new Token();
                token.setToken(jwtToken);
                token.setId(user.getId());
                tokenMapper.insert(token);
            }else{
                token.setToken(jwtToken);
                tokenMapper.updateById(token);
            }

            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
            return R.SUCCESS(jwtToken);
        }
        else{
            return R.FAIL("账号密码错误");
        }
    }

    /**
     * 微信小程序用户登录处理
     * @param params 参数map
     * @return
     */
    @Override
    @Transactional
    public R wxClientLogin(Map params) {
        String openid = getOpenid(params.get("code").toString());
        Object inviter = params.get("shareOpenid"); // 邀请人
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid,openid);
        User user = userMapper.selectOne(queryWrapper);
        if(user==null){
            user = new User();
            user.setCreateTime(LocalDateTime.now());
            user.setOpenid(openid);
            user.setRemark("client");
            user.setName(openid.substring(openid.length()-6));
            if(inviter!=null && !StrUtil.isEmpty(inviter.toString())){
                user.setInviter(inviter.toString());
            }
            userMapper.insert(user);
            HashMap<String, String> claimMap = new HashMap<>();
            claimMap.put("identity",openid); // 记录openid和account的
            claimMap.put("role","client"); // 标记当前用户所属角色
            claimMap.put("createTime",LocalDateTime.now().toString());// 记录jwt保存的时间
            String jwtToken = JWTUtil.createJWT(claimMap, secretKey);
            Token token = new Token();
            token.setCreateTime(LocalDateTime.now());
            token.setId(user.getId());
            token.setToken(jwtToken);
            tokenMapper.insert(token);
            HashMap<String, String> resMap = new HashMap<>();
            resMap.put("token",jwtToken);
            resMap.put("userName",user.getName());
            resMap.put("avtar",user.getAvatar());
            resMap.put("email",user.getEmail());
            return R.SUCCESS(resMap);
        }else{
            // 已经存在该用户
            HashMap<String, String> claimMap = new HashMap<>();
            claimMap.put("identity",openid);
            claimMap.put("role","client");
            claimMap.put("createTime",LocalDateTime.now().toString());
            String jwtToken = JWTUtil.createJWT(claimMap, secretKey);
            Token token = tokenMapper.selectById(user.getId());
            if(token==null){ // 数据库删除用户token ，前端强制重新登录
                token = new Token();
                token.setId(user.getId());
                token.setToken(jwtToken);
                token.setCreateTime(LocalDateTime.now());
                tokenMapper.insert(token);
            }
            else{
                token.setToken(jwtToken);
                token.setUpdateTime(LocalDateTime.now());
                tokenMapper.updateById(token);
            }
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
            HashMap<String, String> resMap = new HashMap<>();
            resMap.put("token",jwtToken);
            resMap.put("userName",user.getName());
            resMap.put("avatar",user.getAvatar());
            resMap.put("email",user.getEmail());
            return R.SUCCESS(resMap);
        }

    }

    /**
     * @param file
     * @return
     */
    @Override
    @Transactional
    public R uploadAvatar(MultipartFile file) {
        HashMap<String, Object> map = minioUtil.uploadAvatar(file);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid, SecurityUtil.getUserName());
        User user = userMapper.selectOne(queryWrapper);
        if(ObjectUtil.isEmpty(user)){
            log.info("用户:{}不存在",SecurityUtil.getUserName());
            throw new BusinessException("认证失败，请稍后再试!");
        }
        user.setAvatar(map.get("url").toString());
        return R.SUCCESS(map);
    }

    /**
     * @param user
     */
    @Override
    @Transactional
    public void handleClientUpdate(User user) {
        user.setId(null);
        user.setOpenid(null);
        user.setUpdateTime(LocalDateTime.now());
        LambdaUpdateWrapper<User> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(User::getOpenid,SecurityUtil.getUserName());
        userMapper.update(user,queryWrapper);
    }


    private String getOpenid(String code){
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        // 通过访问微信小程序提供的 GET https://api.weixin.qq.com/sns/jscode2session 接口，可以获取用户唯一标识openid
        // 下面就使用java程序发起HTTPS请求
        try {
            // 1. 创建连接
            HttpsURLConnection connection =  (HttpsURLConnection)new URL(url).openConnection();
            // 2. 设置请求方式为GET
            connection.setRequestMethod("GET");
            // 3. 设置允许向服务器输出内容
            connection.setDoOutput(true);
            // 4. 开始向服务器写入参数数据(get请求)
            String parames = "js_code="+code+"&appid="+appid+"&secret="+secret+"&grant_type="+grant_type;
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(parames);
            dataOutputStream.close();
            // 5.读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            // 6. 设置一个StringBuilder将结果保存起来
            StringBuilder stringBuilder = new StringBuilder();
            while((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
            // 7. 关闭流
            reader.close();
            // 8. 输出响应内容
            log.info("获取的请求数据:{}",stringBuilder.toString());  // {"session_key":"eVWJieCvQTM49amR7MRxZw==","openid":"oOoZ369VTavOxqGt88ZpaG3hzL3A"}
            // 9. 将json对象使用转换为json对象,JSONUtils对字符串解析后是LinkedHashMap
            LinkedHashMap<String,String> map = (LinkedHashMap<String, String>) JSONUtils.parse(stringBuilder.toString());
            return map.get("openid");
        } catch ( IOException e ) {
            throw new BusinessException(e.toString());
        }
    }


}
