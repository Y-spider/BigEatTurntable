package top.chopper.utils.xcfspider;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/20 0:53
   @Version:1.0.0
   @Description: 保存菜品信息
   */
@Data
@TableName("food_preparation")
@Builder
public class FoodPreparation {
    // 主键id
    @TableId(type = IdType.AUTO)
    private Integer id;
    // 菜品id
    private Integer dishId;
    // 菜品名称
    private String name;
    // 菜品描述
    private String desc;
    // 封面url
    private String coverUrl;
    // 用料表
    private Ingredient[] ingredients;
    // 步骤
    private Step[] steps;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
    // 删除标注
    private Integer isDelete;
    // 查看人数
    private Integer lookCount;
}
