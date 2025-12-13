package top.chopper.pojo;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.IdType;
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
    @TableId(type = IdType.AUTO)
    private Integer id;  // 品种唯一标识符

    @TableField("`openid`")
    @Schema(name = "openid",description = "账本所属标识")
    private String openid;

    @TableField("`name`")
    @Schema(name = "name",description = "账本所属人")
    private String name;

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
    @TableField("`is_choose`")
    @Schema(name = "isChoose",description = "选中标记")
    private Boolean isChoose;
    @TableField("`parent_id`")
    @Schema(name = "parentId",description = "父账本ID")
    private Integer parentId;

    @TableField("`share_count`")
    @Schema(name = "shareCount",description = "账本参与人数")
    private Integer shareCount; // 共享账单人数

    @TableField(exist = false)
    private Boolean isParentDelete; // 表示参与的共享账单原作者已经删除，已经不能再选了

    @TableField(exist = false)
    private Boolean isEdit; // 是否可以进行编辑

    @TableField(exist = false)
    private String billCreateUserName; // 账本创建用户名称


    public static Integer getIdByCurrentBillBook(BillBook currentChooseBillBook){
        if(ObjectUtil.isNotEmpty(currentChooseBillBook.getParentId())){
            return currentChooseBillBook.getParentId();
        }else{
            return currentChooseBillBook.getId();
        }
    }
}
