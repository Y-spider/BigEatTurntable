package top.chopper.utils.catspider;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.chopper.mapper.CatJoyMapper;
import top.chopper.utils.BaiduTranslateUtil;

import java.util.concurrent.atomic.AtomicInteger;

/*
   @Author:ROBOT
   @DateTime:2025/10/27 22:05
   @Version:1.0.0
   @Description: 注意这个请求国内无法请求需要VPN翻墙
   */
@Slf4j
@AllArgsConstructor
class RequestCatJoyThread implements Runnable {
    private BaiduTranslateUtil baiduTranslateUtil;
    private CatJoyMapper catJoyMapper;
    private AtomicInteger processedCount;  // 计数器
    private Integer count;
    /**
     *
     */
    @Override
    public void run() {
        String url = "https://meowfacts.herokuapp.com/";
        System.out.println("执行了~~" +count);
       for (int i = 0;i < count;i++){
           System.out.println("jdkada===========>" + i);
           try {
               HttpResponse response = HttpRequest.get(url).execute();
               JSONArray data = JSONUtil.parseObj(response.body()).getJSONArray("data");
               String text = data.toString();
               String enText = text.substring(2, text.length() - 2);
               String zhText = baiduTranslateUtil.aiTextTranslate(text);
               if(checkIsRepeat(enText)){
                   --i;
                   continue;
               }
               CatJoy catJoy = new CatJoy();
               catJoy.setEnText(enText);
               catJoy.setZhText(zhText);
               catJoyMapper.insert(catJoy);
               log.info("笑话==》{}  保存成功~~~",enText);
               int count = processedCount.addAndGet(1);
               log.info("线程==》{},成功上传当前已上传:{}个joy",Thread.currentThread().getName(),count);
           }catch ( Exception e ){
               e.printStackTrace();
           }
       }
    }

    private boolean checkIsRepeat(String enText){
        LambdaQueryWrapper<CatJoy> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CatJoy::getEnText,enText);
        return catJoyMapper.exists(queryWrapper);
    }

}