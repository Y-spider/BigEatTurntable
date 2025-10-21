package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/10/3 14:37
   @Version:1.0.0
   @Description:
   */
@TableName("dish_collection")
@Data
public class DishCollection {
    @TableId(type = IdType.AUTO)
    @Schema(name="id",description="主键id")
    private Integer id;
    @Schema(name="dishId",description="菜品id")
    private Integer dishId;
    @Schema(name="openid",description="用户openid")
    private String openid;
    @Schema(name="createTime",description="创建时间")
    private LocalDateTime createTime;
}
