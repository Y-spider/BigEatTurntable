package top.chopper.utils;


import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.chopper.Exception.BusinessException;

import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/1/31 20:33
   @Version:1.0.0
   @Description:微信小程序发送订阅模版消息工具类
   */
@Component
@Slf4j
@Data
public class WxSendTemplateMessageUtil {
    @Value("${wexi.appid}")
    private String appid;

    @Value("${wexi.secret}")
    private String secret;

    @Value("${wexi.mini_program_status}")
    private String status;

    private String templateId;
    private String toPagePath;
    private String toUser;
    private List<WxMaSubscribeMessage.MsgData> dataList;


    // 发送模版消息
    public void sendTemplateMessage(){
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        // 设置小程序appid
        config.setAppid(appid);
        // 设置小程序secret
        config.setSecret(secret);
        WxMaServiceImpl service = new WxMaServiceImpl();
        service.setWxMaConfig(config);

        WxMaSubscribeMessage message = new WxMaSubscribeMessage();
        message.setTemplateId(templateId);
        message.setPage(toPagePath);
        message.setToUser(toUser);
        message.setLang("zh_CN");
        message.setMiniprogramState(status);
        message.setData(dataList);
        try {
            service.getMsgService().sendSubscribeMsg(message);
            log.info("向用户:{}==>发送模版消息成功",toUser);
        } catch ( WxErrorException e ) {
            log.error("向用户:{}==>发送模版消息失败:{}",toUser,e.getMessage());
            throw new BusinessException(e.toString());
        }
    }
}
