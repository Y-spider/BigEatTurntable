package top.chopper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.chopper.pojo.Token;

/*
   @Author:ROBOT
   @DateTime:2025/7/7 23:22
   @Version:1.0.0
   @Description:
   */
public interface TokenMapper extends BaseMapper<Token> {
    // 根据管理端账号或者openid查询Token信息
    Token ISelectById(String identity);
}
