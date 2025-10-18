package top.chopper.utils.xcfspider;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.chopper.Exception.BusinessException;
import top.chopper.constant.SysConstant;
import top.chopper.mapper.DishMakeMapper;
import top.chopper.mapper.DishTypeMapper;
import top.chopper.mapper.SysDishMapper;
import top.chopper.pojo.DishMake;
import top.chopper.pojo.DishType;
import top.chopper.pojo.SysDish;
import top.chopper.utils.MinioUtil;
import top.chopper.utils.SecurityUtil;
import top.chopper.websocket.WebSocketServer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @Autowired
    private SysDishMapper sysDishMapper;
    @Autowired
    private DishMakeMapper dishMakeMapper;
    @Autowired
    private DishTypeMapper dishTypeMapper;
    /**
     * @param url 目标菜品详细页面url
     * @return FootPreparation对象
     */
    public FoodPreparation spiderPreparation(String url) {
        HttpResponse response = HttpRequest.get(url)
                .setFollowRedirects(true) // 开启自动重定向
                .execute();
        String pageContent = response.body();
        // 正则表达式匹配用料名称和用量
        String namePattern = "<td class=\"name\">\\s*(?:<a href=\"[^\"]*\">)?(.*?)(?:</a>)?\\s*</td>";
        String unitPattern = "<td class=\"unit\">\\s*(.*?)\\s*</td>";
        // 提取用料名称
       List<String> names = ReUtil.findAll(namePattern, pageContent, 1);
        // 提取用量
       List<String> units = ReUtil.findAll(unitPattern, pageContent, 1);
        // 提取tips
       String tipReg = "<div class=\"tip\">(.*?)</div>";
       List<String> tips = ReUtil.findAll(tipReg, pageContent, 1);
       // 构建用料表信息
        Ingredient[] ingredients = new Ingredient[names.size()];
        for (int i = 0; i < names.size(); i++) {
            Ingredient ingredient = new Ingredient();
            ingredient.setName(names.get(i));
            ingredient.setAmount(units.get(i));
            ingredients[i] = ingredient;
        }
       // 正则表达式匹配<img>标签中的src属性
        // 提取菜品名称和封面图片
        String titleRes = "<h1[^>]*class=\"page-title\"[^>]*>(.*?)</h1>";
        Pattern coverImagePattern = Pattern.compile("style=\"background-image:\\s*url\\((.*?)\\)");
        Matcher matcher = coverImagePattern.matcher(pageContent);
        String coverImageUrl = "";
        if(matcher.find()){
            coverImageUrl = matcher.group(1);
        }
        String desRes = "<div\\s+class=\"desc mt30\">\\s*(.*?)\\s*</div>";
        List<String> title = ReUtil.findAll(titleRes, pageContent, 1);
        List<String> des = ReUtil.findAll(desRes, pageContent, 1);
        String name = "";
        String coverUrl = "";
        String description = "";
        if(!title.isEmpty()){
            name = title.get(0).replaceAll("\\s+", " ").trim();
        }
        if(!coverImageUrl.isEmpty()){
            coverUrl = minioUtil.uploadFile(coverImageUrl).get("url");
        }
        if(!des.isEmpty()){
            description = des.get(0);
        }
        String regex = "<li\\b[^>]*class=\"container\"[^>]*>\\s*<p\\b[^>]*>([\\s\\S]*?)<\\/p>\\s*<img\\b[^>]*src=\"([^\"\\s]+)\"[^>]*>\n";
        List<String> imagesUrl = ReUtil.findAll(regex, pageContent, 2);
        List<String> stepDes = ReUtil.findAll(regex, pageContent, 1);
        Step[] steps = new Step[imagesUrl.size()];
        for (int i = 0; i < imagesUrl.size(); i++) {
            Step step = new Step();
            step.setDesc(stepDes.get(i));
            step.setImgUrl(minioUtil.uploadFile(imagesUrl.get(i)).get("url"));
            steps[i] = step;
        }
        FoodPreparation build = FoodPreparation.builder()
                .desc(description)
                .name(name)
                .coverUrl(coverUrl)
                .name(name)
                .ingredients(ingredients)
                .steps(steps)
                .build();
        if(!tips.isEmpty()){
            build.setTips(tips.get(0));
        }
        return build;

    }


    /**
     * @param searchName 搜索的菜品名称
     * @param aimCount 想要保存菜品的数量
     * @param startPage 搜索页数 表示从第几页开始搜索(默认为1) 最大递归到15页数据
     * @param aimUrl 类型页面 类似于 https://www.xiachufang.com/explore/?page=1 当传入aimUrl时无需传递searchName
     * @param endPage 查找页数，如果在startPage-endPage中间都没找到符合要求的数量，会在找完endPage停止
     * @return 返回上传成功菜单的名称列表
     * tips: 如果在检索的菜品中有重名的 可能会造成有一部分的菜品遗漏检查不到
     */
    public List<String> spiderFoodPreparationBySearch(
            String searchName, Integer aimCount, Integer startPage,
            String aimUrl, Integer endPage, Integer typeCode) {

        if (aimCount == null || aimCount <= 0) {
            return Collections.emptyList(); // 不需要再抓
        }
        if (startPage == null) {
            startPage = 1;
        }
        if (endPage == null) {
            endPage = 99;
        }
        if (searchName != null && !searchName.isEmpty()) {
            typeCode = SysConstant.DISH_DEFAULT_TYPE;
        }

        List<String> uploadedNames = new ArrayList<>();

        String url = "https://www.xiachufang.com/search/?keyword=" + searchName + "&cat=1001&page=" + startPage;
        if (aimUrl != null && !aimUrl.contains("?")) {
            url = aimUrl + "?page=" + startPage;
        }
        log.info("请求网址为===>{}", url);
        String htmlContent = HttpUtil.get(url);
        if ("redirect".equals(htmlContent) || htmlContent.contains("301")) {
            throw new BusinessException("网址" + url + "==>重定向，请稍后再试");
        }
        if(htmlContent.contains("请滑动完成验证")){
            throw new BusinessException("网址" + url + "==>触发滑动验证，请稍后再试");
        }

        String reg = "<div class=\"info pure-u\">.*?<a href=\"([^\"]+)\"[^>]*>(.*?)<\\/a>";
        List<String> uri = ReUtil.findAll(reg, htmlContent, 1);
        List<String> dish = ReUtil.findAll(reg, htmlContent, 2);
        if (uri.isEmpty() || startPage >= endPage) {
            log.info("未找到目标菜品==>{}制作教程", searchName);
           throw new BusinessException("未找到目标菜品==>"+searchName+"制作教程");
        }

        int uploadedCount = 0;

        for (int i = 0; i < uri.size() && uploadedCount < aimCount; i++) {
            ArrayList<String> searchDishNames = new ArrayList<>();
            ArrayList<String> targetUris = new ArrayList<>();
            for (int j = i; j < i + aimCount && j < uri.size(); j++) {
                searchDishNames.add(dish.get(j).replaceAll("\\s+", "") + "-" + startPage + "-" + (j + 1));
                targetUris.add(uri.get(j));
            }

            // 去数据库查重
            LambdaQueryWrapper<SysDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysDish::getName, searchDishNames);
            queryWrapper.eq(SysDish::getIsDelete,false);
            List<SysDish> sysDishes = sysDishMapper.selectList(queryWrapper);

            if (sysDishes.isEmpty()) {
                for (int l = 0; l < searchDishNames.size() && uploadedCount < aimCount; l++, uploadedCount++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    SysDish sysDish = new SysDish();
                    sysDish.setCreateTime(LocalDateTime.now());
                    sysDish.setIsDelete(SysConstant.ALIVE);
                    sysDish.setIsMake(true);
                    sysDish.setUpdateTime(LocalDateTime.now());
                    sysDish.setName(searchDishNames.get(l));
                    sysDish.setTypeId(typeCode);

                    DishMake dishMake = new DishMake();
                    dishMake.setIsDelete(SysConstant.ALIVE);
                    dishMake.setCreateTime(LocalDateTime.now());
                    String detailUrl = "https://www.xiachufang.com" + targetUris.get(l);
                    FoodPreparation foodPreparation = spiderPreparation(detailUrl);

                    dishMake.setContent(JSONUtil.toJsonStr(foodPreparation));
                    sysDish.setCoverUrl(foodPreparation.getCoverUrl());
                    sysDish.setDishDes(foodPreparation.getDesc());

                    sysDishMapper.insert(sysDish);
                    dishMake.setDishId(sysDish.getId());
                    dishMakeMapper.insert(dishMake);
                    log.info("成功添加菜品==>{}",searchDishNames.get(l));
                    sendSocketMessage(searchDishNames.get(l));
                    uploadedNames.add(searchDishNames.get(l));
                }
            }
        }

        // ✅ 如果还没满足数量，递归翻页继续
        if (uploadedNames.size() < aimCount) {
            uploadedNames.addAll(
                    spiderFoodPreparationBySearch(
                            searchName, aimCount - uploadedNames.size(),
                            startPage + 1, aimUrl, endPage, typeCode
                    )
            );
        }

        return uploadedNames;
    }




    /**
     * @param typeName 分类名称 查看是否
     * @param aimCount
     * @param startPage
     * @param aimUrl
     * @param endPage
     */
    public void spiderFoodPreparationByType(String typeName,Integer aimCount,Integer startPage,String aimUrl,Integer endPage){
        LambdaQueryWrapper<DishType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishType::getName,typeName);
        DishType dishType = dishTypeMapper.selectOne(queryWrapper);
        if(dishType==null){
            dishType = new DishType();
            dishType.setIsDelete(SysConstant.ALIVE);
            dishType.setName(typeName);
            dishType.setCreateTime(LocalDateTime.now());
            dishType.setTypeUrl(aimUrl);
            dishTypeMapper.insert(dishType);
        }
        this.spiderFoodPreparationBySearch(null,aimCount,startPage,aimUrl,endPage,dishType.getId());
    }

    private void sendSocketMessage(String message){
        HashMap<String, WebSocketServer> map = WebSocketServer.getWebSocketMap();
        String account = SecurityUtil.getUserName();
        WebSocketServer webSocketServer = map.get(account);
        if(webSocketServer!=null){
            try {
                webSocketServer.sendMessage(message);
            } catch ( IOException e ) {
                log.error("websocket发送消息 '{}' 给用户 [{}] 失败", message, account,e);
            }
        }
    }

}
