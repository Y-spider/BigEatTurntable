package top.chopper.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2024/4/15 19:38
   @Version:1.0.0
   @Description: 配置类
   */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //log.info("消息转换器-0:{}",converters.get(0));
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(1, messageConverter);
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("吃货大转盘")
                        //描叙
                        .description("吃货大转盘API文档")
                        //版本
                        .version("v1.0.0")
                        //作者信息，自行设置
                        .contact(new Contact().name("chopper").email("3267585160@qq.com").url("https://www.baidu.com"))
                        //设置接口文档的许可证信息
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

}
