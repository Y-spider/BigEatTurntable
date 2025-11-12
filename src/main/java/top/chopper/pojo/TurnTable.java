package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:15
   @Version:1.0.0
   @Description:
   */
@Data
@TableName("turntable")
public class TurnTable {
    @TableId(type = IdType.AUTO)
    @Schema(name="id",description="转盘id")
    private Integer id;
    @Schema(name="content",description="转盘内容格式为 xxx,xxx,xxx,xxx 使用逗号分隔")
    private String content;
    @Schema(name="type",description="标识转盘类型 1为系统转盘 0为用户自定义转盘")
    private Integer type;
    @Schema(name="openid",description="当前转盘所属用户,为空则表示是系统转盘")
    private String openid;
    @Schema(name="title",description="转盘标题")
    private String title;
    @TableLogic(value = "0", delval = "2") // 逻辑删除字段
    @Schema(name="isDelete",description="采用逻辑删除 0表示未删除 2表示删除")
    private Integer isDelete;
    @Schema(name="createTime",description="创建时间")
    private LocalDateTime createTime;
    @Schema(name="updateTime",description="更新时间")
    private LocalDateTime updateTime;
    @Schema(name = "orderNumber",description = "排序字段")
    private Integer orderNumber;
    @Schema(name = "repeat",description = "是否可重复抽")
    private Boolean isRepeat;

    @TableField(exist = false)
    private Boolean canEdit = true; // 转盘修改权限,默认为true

    @Schema(name = "limitCount",description = "限制抽奖次数")
    private Integer limitCount;

    @Schema(name = "limitStartTime",description = "标识时间")
    private LocalDateTime limitStartTime;

    @Schema(name = "erCodeUrl",description = "分享二维码URL")
    private String erCodeUrl;
}
