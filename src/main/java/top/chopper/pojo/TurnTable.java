package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
}
