package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:09
   @Version:1.0.0
   @Description: 账本记录
   */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("bill_record")
public class
BillRecord extends BasePojo {
    @TableId(type = IdType.AUTO)
    private String id;  // 品种唯一标识符

    @TableField("`bill_book_id`")
    @Schema(name = "billBookId",description = "账本ID")
    private Integer billBookId;

    @TableField("`openid`")
    @Schema(name = "openid",description = "记录所属用户")
    private String openid;

    @TableField("`type`")
    @Schema(name = "type",description = "账单类型 out or in")
    private String type;

    @TableField("`amount`")
    @Schema(name = "amount",description = "消费记录")
    private BigDecimal amount;

    @TableField("`location`")
    @Schema(name = "location",description = "消费位置")
    private String location;

    @TableField("`label_name`")
    @Schema(name = "labelName",description = "标签名称")
    private String labelName;

    @TableField("`label_url`")
    @Schema(name = "labelUrl",description = "标签url")
    private String labelUrl;

    @TableField("`name`")
    @Schema(name = "name",description = "用户名称")
    private String name;

    @TableField("`remark`")
    @Schema(name = "remark",description = "备注信息")
    private String remark;

    @TableField("`attachment_url`")
    @Schema(name = "attachmentUrl",description = "附件URL，多个使用逗号分隔开")
    private String attachmentUrl;

    @TableField("`update_openid`")
    @Schema(name = "updateOpenid",description = "修改人标识")
    private String updateOpenid;

    @TableField("`record_time`")
    @Schema(name = "recordTime",description = "标记时间")
    private String recordTime;

    @TableField("`parent_id`")
    @Schema(name = "parentId",description = "从那个记录拷贝过来的，会在导入账本记录时生成")
    private Integer parentId;

    @TableField("`method_label`")
    @Schema(name = "methodLabel",description = "标记时间")
    private String methodLabel;

    @TableField("`method_url`")
    @Schema(name = "methodUrl",description = "标记时间")
    private String methodUrl;

    @TableField(exist = false)
    private Boolean isEdit; // 是否具有修改权限



}
