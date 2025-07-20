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
   @DateTime:2025/7/20 14:01
   @Version:1.0.0
   @Description: 菜品制作
   */
@Data
@TableName("dish_make")
public class DishMake {
    @TableId(type = IdType.AUTO)
    @Schema(name="id",description="主键id")
    private Integer id;
    @Schema(name="dishId",description="菜品id")
    private Integer dishId;
    @TableLogic(value = "0", delval = "2") // 逻辑删除字段
    private Integer isDelete;
    @Schema(name="content",description="菜品制作信息json字符串")
    private String content;
    @Schema(name="createTime",description="创建时间")
    private LocalDateTime createTime;
    @Schema(name="updateTime",description="更新时间")
    private LocalDateTime updateTime;
    @Schema(name="lookCount",description="浏览次数")
    private Integer lookCount;


}
