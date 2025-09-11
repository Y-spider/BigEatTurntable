package top.chopper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("top.chopper.mapper")
@EnableCaching  // 开启基础注解的spring缓存
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        System.out.println("后端程序启动成功૮(˶ᵔ ᵕ ᵔ˶)ა~~~~");
    }
}
