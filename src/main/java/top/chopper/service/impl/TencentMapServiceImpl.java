package top.chopper.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.chopper.service.TencentMapService;

import java.util.HashMap;

/*
   @Author:ROBOT
   @DateTime:2025/10/4 21:38
   @Version:1.0.0
   @Description:
   */
@Service
public class TencentMapServiceImpl implements TencentMapService {
    @Value("${tencent.secret-key}")
    private String secretKey;

    public static void main(String[] args) {
        String key = "fi7lyF6Ng82zEEXZnhnIuDwpYEEFxTAE";
        String url = "https://apis.map.qq.com/ws/place/v1/search";
        HashMap<String, Object> params = new HashMap<>();
        params.put("key",key);
        params.put("orderby","_distance");// 排序按照距离，从远到近
        params.put("page_size",10); // 每页数量
        params.put("page_index",1); // 获取页数

    }


}
