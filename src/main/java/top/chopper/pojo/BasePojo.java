package top.chopper.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:07
   @Version:1.0.0
   @Description:
   */
@Data
public class BasePojo {
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
