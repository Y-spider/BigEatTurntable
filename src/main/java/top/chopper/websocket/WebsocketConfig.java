package top.chopper.websocket;

import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/*
   @Author:ROBOT
   @DateTime:2025/7/27 15:57
   @Version:1.0.0
   @Description:
   */
//@Configuration
//@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebsocketConfig {
    //    websocket依赖
//    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return  new ServerEndpointExporter();
    }
}
