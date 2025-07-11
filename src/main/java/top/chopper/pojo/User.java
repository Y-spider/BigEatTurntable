package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 16:24
   @Version:1.0.0
   @Description:
   */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    @Schema(name="id",description="主键标识")
    private Integer id;
    @Schema(name="openid",description="微信小程序用户标识")
    private String openid;
    @Schema(name="account",description="管理端用户登陆账号")
    private String account;
    @Schema(name="passwd",description="管理端用户登陆密码")
    private String passwd;
    @Schema(name="remark",description="账号备注")
    private String remark;
    @Schema(name = "createTime",description = "账号创建时间")
    private LocalDateTime createTime;
    @Schema(name = "updateTime",description = "账号修改时间")
    private LocalDateTime updateTime;
    @Schema(name = "name",description = "微信登录用户名称")
    private String name;
}
