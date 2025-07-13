package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/13 18:09
   @Version:1.0.0
   @Description:
   */
@Data
@TableName("rotation_record")
public class RotationRecord {
    @Schema(name="id",description="主键")
    @TableId(type = IdType.AUTO)
    private Integer id;
    @Schema(name="turntableName",description="转盘名称")
    private String turntableName;
    @Schema(name="result",description="转动结果")
    private String result;
    @Schema(name="createTime",description="结果时间")
    private LocalDateTime createTime;
    @Schema(name="type",description="轮盘类型 自定义 热门 系统")
    private String type;
    @Schema(name="isDelete",description="0 正常 2删除")
    @TableLogic(value = "0", delval = "2") // 逻辑删除字段
    private Integer isDelete;
    @Schema(name="turntableId",description="所属轮盘id")
    private Integer turntableId;
    @Schema(name="openid",description="记录所属用户标识")
    private String openid;
}
