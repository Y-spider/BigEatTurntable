package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:19
   @Version:1.0.0
   @Description: 消费方式
   */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("pay_method")
public class PayMethod extends BasePojo {
    @TableId
    @TableField("`id`") // 表示数据库的字段名为 `id`
    private String id;  // 品种唯一标识符

    @TableField("`method`")
    @Schema(name = "method",description = "支付方式")
    private String method;
    @TableField("`icon_url`")
    @Schema(name = "iconUrl",description = "iconURL")
    private String iconUrl;
    @TableField("`openid`")
    @Schema(name = "openid",description = "所属用户，为空则为系统")
    private String openid;

}
