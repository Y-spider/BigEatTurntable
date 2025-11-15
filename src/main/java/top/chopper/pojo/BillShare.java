package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/*
   @Author:ROBOT
   @DateTime:2025/11/15 22:09
   @Version:1.0.0
   @Description: 用户共享账单
   */
@TableName("bill_share")
@Data
public class BillShare {
    @TableId(type = IdType.AUTO)
    @TableField("`id`")
    @Schema(name = "id",description = "主键")
    private Integer id;

    @TableField("`openid`")
    @Schema(name = "`openid`",description = "用户标识")
    private String openid;
    @TableField("`billId`")
    @Schema(name = "billId",description = "参与的共享账单id")
    private Integer billId;
}
