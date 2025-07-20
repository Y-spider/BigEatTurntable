package top.chopper.utils.xcfspider;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.chopper.utils.MinioUtil;

import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/7/19 23:35
   @Version:1.0.0
   @Description: 爬取下厨房的菜谱信息
   */

@Component
@Slf4j
public class SpiderUtil {
    @Autowired
    private MinioUtil minioUtil;
    public static void main(String[] args) {
        String url = "https://www.xiachufang.com/recipe/102205755/";
        String pageContent = HttpUtil.get(url);
//        System.out.println("获取网页信息==>"+pageContent);
        // 正则表达式匹配用料名称和用量
        String namePattern = "<td class=\"name\">\\s*(?:<a href=\"[^\"]*\">)?(.*?)(?:</a>)?\\s*</td>";
        String unitPattern = "<td class=\"unit\">\\s*(.*?)\\s*</td>";
        // 提取用料名称
       List<String> names = ReUtil.findAll(namePattern, pageContent, 1);
        // 提取用量
       List<String> units = ReUtil.findAll(unitPattern, pageContent, 1);
        // 提取JSON字符串
        // 正则表达式匹配<img>标签中的src属性
        String jsonStr = ReUtil.getGroup0("<script type=\"application/ld\\+json\">(.*?)</script>", pageContent);
        // 将JSON字符串转换为JSONObject
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr.substring(jsonStr.indexOf("{")));
       // 提取字段 name 菜品名称 image 菜品封面图 description 菜品描述
        String name = jsonObject.getStr("name");
        String coverUrl = jsonObject.getStr("image");
        String description = jsonObject.getStr("description");
        String regex = "<img\\s+src=\"(https:\\/\\/i2\\.chuimg\\.com/[^\"]+)\"[^>]*alt=\"([^\"]+)\"";
        // 注意这里捕获的前面两个分别为 菜品封面图和作者头像(这两个都是不需要的)
        List<String> imagesUrl = ReUtil.findAll(regex, pageContent, 1);
        List<String> stepDes = ReUtil.findAll(regex, pageContent, 2);
        FoodPreparation  foodPreparation =  FoodPreparation.builder()
                .desc(description)
                .lookCount(0)
                .name(name)
                .coverUrl(coverUrl)
                .build();


    }
}
