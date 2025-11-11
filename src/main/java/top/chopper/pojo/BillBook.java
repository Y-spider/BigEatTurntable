package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:02
   @Version:1.0.0
   @Description: 账本
   */
@EqualsAndHashCode(callSuper = true)
@TableName("bill_book")
@Data
public class BillBook extends BasePojo{
    @TableId
    @TableField("`id`") // 表示数据库的字段名为 `id`
    private String id;  // 品种唯一标识符

    @TableField("`openid`")
    @Schema(name = "openid",description = "账本所属标识")
    private String openid;

    @TableField("`title`")
    @Schema(name = "title",description = "账本名称")
    private String title;
    @TableField("`type`")
    @Schema(name = "type",description = "账本类型，私有，共享")
    private String type;
    @TableField("`is_share`")
    @Schema(name = "isShare",description = "是否为好友账本")
    private Boolean isShare;
    @TableField("`icon_url`")
    @Schema(name = "iconUrl",description = "账本ICON访问地址")
    private String iconUrl;

}
