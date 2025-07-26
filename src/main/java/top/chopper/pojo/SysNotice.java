package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/26 15:50
   @Version:1.0.0
   @Description:
   */
@TableName("sys_notice")
@Schema(description = "系统通知表")
@Data
public class SysNotice {
    @TableId(type=IdType.AUTO)
    @Schema(name="id",description="主键id")
    private Integer id;
    @Schema(name="content",description="通知内容")
    private String content;
    @Schema(name="createTime",description="创建时间")
    private LocalDateTime createTime;
    @Schema(name="updateTime",description="更新时间")
    private LocalDateTime updateTime;
    @Schema(name="active",description="是否生效")
    private Boolean active;
}
