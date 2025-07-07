package top.chopper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/7 23:15
   @Version:1.0.0
   @Description:
   */
@Data
@TableName("token")
public class Token {
    @Schema(name = "id",description = "用户id")
    private Integer id;
    @Schema(name="token",description="存放的token令牌信息")
    private String token;
    @Schema(name = "createTime",description = "账号创建时间")
    private LocalDateTime createTime;
    @Schema(name = "updateTime",description = "账号修改时间")
    private LocalDateTime updateTime;
    public Token(){
        this.updateTime = LocalDateTime.now();
    }
}
