package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:22
   @Version:1.0.0
   @Description:
   */
@TableName("sys_dish")
@Data
public class SysDish {
    @TableId(type=IdType.AUTO)
    @Schema(name="id",description="菜品id")
    private Integer id;
    @Schema(name="name",description="菜品名称")
    private String name;
    @Schema(name="typeId",description="菜品分类id")
    private Integer typeId;
    @Schema(name="isMake",description="是否有菜谱(制作过程)")
    private Boolean isMake;
    @Schema(name="hot",description="热度值")
    private Integer hot;
    @Schema(name="makeId",description="菜品制作教程id")
    private Integer makeId;
    @Schema(name="createTime",description="创建时间")
    private LocalDateTime createTime;
    @Schema(name="updateTime",description="更新时间")
    private LocalDateTime updateTime;
    @TableLogic(value = "0", delval = "2") // 逻辑删除字段
    @Schema(name="isDelete",description="采用逻辑删除 0表示未删除 2表示删除")
    private Integer isDelete;
    public void autoSetUpdateTime(){
        this.updateTime = LocalDateTime.now();
    }
}
