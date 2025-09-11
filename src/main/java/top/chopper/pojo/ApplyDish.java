package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/9/11 23:05
   @Version:1.0.0
   @Description:
   */
@Data
public class ApplyDish  {
    @TableId(type = IdType.AUTO)
    @Schema(name="id",description="主键id")
    private Integer id;
    @Schema(name="dishName",description="菜品名称")
    private String dishName;
    @Schema(name="status",description="处理状态")
    private String status;
    @Schema(name="createTime",description="创建时间")
    private LocalDateTime createTime;
    @Schema(name="updateTime",description="更新时间")
    private LocalDateTime updateTime;
    @Schema(name="remark",description="备注信息")
    private String remark;
    @Schema(name="userId",description="用户ID")
    private Integer userId;
}
