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
   @DateTime:2025/7/6 16:38
   @Version:1.0.0
   @Description:
   */
@Data
@TableName("dish_type")
public class DishType {
    @TableId(type = IdType.AUTO)
    @Schema(name="id",description="主键id")
    private Integer id;
    @Schema(name="name",description="菜品分类名称(早餐 午餐 奶茶......)")
    private String name;
    @Schema(name="createTime",description="创建时间")
    private LocalDateTime createTime;
    @Schema(name="updateTime",description="更新时间")
    private LocalDateTime updateTime;
    @TableLogic(value = "0", delval = "2") // 逻辑删除字段
    @Schema(name="isDelete",description="采用逻辑删除 0表示未删除 2表示删除")
    private Integer isDelete;
}
