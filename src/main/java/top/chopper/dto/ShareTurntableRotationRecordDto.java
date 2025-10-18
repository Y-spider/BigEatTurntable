package top.chopper.dto;

import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/10/18 16:22
   @Version:1.0.0
   @Description: 转盘旋转记录，包含好友
   */
@Data
public class ShareTurntableRotationRecordDto {
    private Long id; // 主键
    private String userName; // 用户名称
    private LocalDateTime createTime; // 创建时间
    private Boolean isMy; // 标识是否是自己的旋转记录，与好友进行区分
    private String result; // 抽取结果
}
