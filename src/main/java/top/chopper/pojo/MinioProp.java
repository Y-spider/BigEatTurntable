package top.chopper.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
   @Author:ROBOT
   @DateTime:2025/7/19 21:56
   @Version:1.0.0
   @Description:
   */
@Data
@ConfigurationProperties(prefix = "minio")
@Component
public class MinioProp {
    private String endpoint;
    private String accesskey;
    private String secretKey;
    private String bucketName;
}
