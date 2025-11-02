package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/*
   @Author:ROBOT
   @DateTime:2025/10/27 21:41
   @Version:1.0.0
   @Description: 猫咪
   */
@TableName("animal_fact")
@Data
public class AnimalFact {
    @TableId(type = IdType.AUTO)
    @Schema(name = "id",description = "主键")
    private Integer id;
    @Schema(name = "enText",description = "英语原文")
    private String enText;
    @Schema(name = "zhText",description = "中文")
    private String zhText;
    @Schema(name = "type",description = "种类")
    @TableField("`type`")
    private String type;

}
