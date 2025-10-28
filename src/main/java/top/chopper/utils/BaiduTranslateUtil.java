package top.chopper.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.HashMap;

/*
   @Author:ROBOT
   @DateTime:2025/10/27 21:09
   @Version:1.0.0
   @Description: 百度翻译工具类
   */
@Component
@Slf4j
public class BaiduTranslateUtil {
    @Value("${thrids.badu.apiKey}")
    private String apiKey;
    @Value("${thrids.badu.appid}")
    private String appid;
    /**
     * 英文翻译为中文，ai加强
     * @param text 待翻译文本
     * @return 翻译文本
     */
    public String aiTextTranslate(String text){
        if(StrUtil.isEmpty(text)){
           return "";
        }
      try {
          String encodedText = URLEncoder.encode(text, "UTF-8");  // 对文本进行 URL 编码
          String url = "https://fanyi-api.baidu.com/ait/api/aiTextTranslate";
          HttpRequest request = HttpRequest.get(url)
                  .header("Content-Type", "application/x-www-form-urlencoded")
                  .header("Authorization", apiKey);
          HashMap<String, Object> data = new HashMap<>();
          data.put("appid",appid);
          data.put("from","en");
          data.put("to","zh");
          data.put("q",encodedText);
          request.form(data);
          HttpResponse response = request.execute();
          String responseBody = response.body();
          String translatedText = JSONUtil.parseObj(responseBody).getJSONArray("trans_result")
                  .getJSONObject(0).getStr("dst");
          return translatedText;
      }catch ( Exception e ){
          log.error("翻译失败==>{}",e.getMessage());
          return "";
      }
    }
}
