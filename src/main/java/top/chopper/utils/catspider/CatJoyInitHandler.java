package top.chopper.utils.catspider;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.chopper.mapper.CatJoyMapper;
import top.chopper.utils.BaiduTranslateUtil;

import java.util.concurrent.atomic.AtomicInteger;

/*
   @Author:ROBOT
   @DateTime:2025/10/27 18:37
   @Version:1.0.0
   @Description: 猫咪冷笑话数据库初始化处理器
   */
@Component
@Slf4j
public class CatJoyInitHandler {
    @Autowired
    private BaiduTranslateUtil baiduTranslateUtil;
    @Autowired
    private CatJoyMapper catJoyMapper;
    private AtomicInteger processedCount = new AtomicInteger(0);  // 线程安全的计数器

    /**
     * 使用多线程处理（3个线程）
     * @param count 初始化冷知识条数 / 3
     */
//    public void initCatJoy(int count,int threads){
//        ExecutorService service = Executors.newFixedThreadPool(threads);
//        for(int i = 0;i < threads;i++){
//            service.submit(new RequestCatJoyThread(baiduTranslateUtil, catJoyMapper,processedCount,count));
//        }
////        RequestCatJoyThread thread_1 = new RequestCatJoyThread(baiduTranslateUtil, catJoyMapper,processedCount,count);
////        RequestCatJoyThread thread_2 = new RequestCatJoyThread(baiduTranslateUtil, catJoyMapper,processedCount,count);
////        RequestCatJoyThread thread_3 = new RequestCatJoyThread(baiduTranslateUtil, catJoyMapper,processedCount,count);
////        service.execute(thread_1);
////        service.execute(thread_2);
////        service.execute(thread_3);
//        //3. 关闭连接池
//        service.shutdown();
//    }
    public void initCatJoy(int count){
        String url = "https://meowfacts.herokuapp.com/";
      for (int i = 0;i < count;i++){
          try {
              Thread.sleep(1000);
          } catch ( InterruptedException e ) {
              throw new RuntimeException(e);
          }
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
          log.info("笑话==》{}  保存第{}成功~~~",enText,i+1);
      }
    }

    private boolean checkIsRepeat(String enText){
        LambdaQueryWrapper<CatJoy> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CatJoy::getEnText,enText);
        return catJoyMapper.exists(queryWrapper);
    }
}
