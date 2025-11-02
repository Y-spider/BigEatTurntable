package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/11/2 13:00
   @Version:1.0.0
   @Description:
   */
@TableName("festival")
@Data
public class Festival {
    @TableId(type = IdType.AUTO)
    public Integer id;

    @TableField("`name`")
    private String name;
    @TableField("`des`")
    private String des;
    @TableField("`calender`")
    private String calender;
    @TableField("`time`")
    private LocalDate time;
    @TableField("`status`")
    private Integer status;
    @TableField("`remaining`")
    private Long remaining;
    @TableField("`type`")
    private String type;
    @TableField("`openid`")
    private String openid;

    @TableField("`create_time`")
    private LocalDateTime createTime;

}
