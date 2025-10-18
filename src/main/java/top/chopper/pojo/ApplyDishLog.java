package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/10/5 18:54
   @Version:1.0.0
   @Description:
   */
@Data
@TableName("apply_dish_log")
public class ApplyDishLog {
    @TableId(type = IdType.AUTO)
    @Schema(name="id",description="主键id")
    private Integer id;
    @Schema(name="applyDishId",description="菜品申请ID")
    private Integer applyDishId;
    @Schema(name="applyDishName",description="处理上传的菜品名称(去掉-)")
    private String applyDishName;
    @Schema(name="createTime",description="创建时间")
    private LocalDateTime createTime;
}
