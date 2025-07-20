package top.chopper.utils.xcfspider;

import lombok.Builder;
import lombok.Data;

/*
   @Author:ROBOT
   @DateTime:2025/7/20 0:53
   @Version:1.0.0
   @Description: 保存菜品信息
   */
@Data
@Builder
public class FoodPreparation {
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
    // 提示
    private String tips;
}
