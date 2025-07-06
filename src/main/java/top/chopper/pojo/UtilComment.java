package top.chopper.pojo;

import lombok.Data;

/*
   @Author:ROBOT
   @DateTime:2024/4/29 16:30
   @Version:1.0.0
   @Description: 用于封装获取到的字段备注信息
   Columns: COLUMN_NAME, COLUMN_COMMENT,DATA_TYPE
<==        Row: avatar, 用户头像 Varchar
<==        Row: comment, 评论数 BigInt
<==        Row: contactor, 联系人 VarChar
        .... ..... .....
   */
@Data
public class UtilComment {
    private String columnName;
    private String columnComment;
    private String dataType;
}
