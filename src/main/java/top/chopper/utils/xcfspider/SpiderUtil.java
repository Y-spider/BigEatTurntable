package top.chopper.utils.xcfspider;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.chopper.constant.SysConstant;
import top.chopper.mapper.DishMakeMapper;
import top.chopper.mapper.SysDishMapper;
import top.chopper.pojo.DishMake;
import top.chopper.pojo.SysDish;
import top.chopper.utils.MinioUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private SysDishMapper sysDishMapper;
    @Autowired
    private DishMakeMapper dishMakeMapper;
    /**
     * @param url 目标菜品详细页面url
     * @return FootPreparation对象
     */
    public FoodPreparation spiderPreparation(String url) {
        String pageContent = HttpUtil.get(url);
//        System.out.println("获取网页信息==>"+pageContent);
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
        // 提取JSON字符串
        // 正则表达式匹配<img>标签中的src属性
        String jsonStr = ReUtil.getGroup0("<script type=\"application/ld\\+json\">(.*?)</script>", pageContent);
        // 将JSON字符串转换为JSONObject
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr.substring(jsonStr.indexOf("{")));
       // 提取字段 name 菜品名称 image 菜品封面图 description 菜品描述
        String name = jsonObject.getStr("name");
        String coverUrl = minioUtil.uploadFile(jsonObject.getStr("image")).get("url");
        String description = jsonObject.getStr("description");
        String regex = "<img\\s+src=\"(https:\\/\\/i2\\.chuimg\\.com/[^\"]+)\"[^>]*alt=\"([^\"]+)\"";
        // 注意这里捕获的前面两个分别为 菜品封面图和作者头像(这两个都是不需要的)
        List<String> imagesUrl = ReUtil.findAll(regex, pageContent, 1);
        List<String> stepDes = ReUtil.findAll(regex, pageContent, 2);
        Step[] steps = new Step[imagesUrl.size() - 2];
        for (int i = 2; i < imagesUrl.size(); i++) {
            Step step = new Step();
            step.setDesc(stepDes.get(i));
            step.setImgUrl(minioUtil.uploadFile(imagesUrl.get(i) + "-" +System.currentTimeMillis()).get("url"));
            steps[i-2] = step;
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
     * @param pageIndex 搜索页数 表示从第几页开始搜索(默认为1) 最大递归到15页数据
     * @param aimUrl 类型页面 类似于 https://www.xiachufang.com/explore/?page=1 当传入aimUrl时无需传递searchName和pageIndex
     * @return 返回上传成功菜单的名称列表
     * tips: 如果在检索的菜品中有重名的 可能会造成有一部分的菜品遗漏检查不到
     */
    public void spiderFoodPreparationBySearch(String searchName,Integer aimCount,Integer pageIndex,String aimUrl){
        if(aimCount == null){
            aimCount = 1;
        }
        if(pageIndex==null){
            pageIndex = 1;
        }
        int uploadedCount = 0;
        String url = "https://www.xiachufang.com/search/?keyword="+searchName+"&cat=1001&page="+pageIndex;
        if(aimUrl!=null){
            url = aimUrl;
        }
        String htmlContent = HttpUtil.get(url);
        String reg = "<div class=\"info pure-u\">.*?<a href=\"([^\"]+)\"[^>]*>(.*?)<\\/a>";
        List<String> uri = ReUtil.findAll(reg, htmlContent, 1);
        List<String> dish = ReUtil.findAll(reg, htmlContent, 2);
        if(uri.isEmpty() || pageIndex>=15){
            log.info("未找到目标菜品==>{}制作教程",searchName);
            return;
        }
        for (int i = 0; i < uri.size(); i++) {
            LambdaQueryWrapper<SysDish> queryWrapper = new LambdaQueryWrapper<>();
            ArrayList<String> searchDishNames = new ArrayList<>();
            ArrayList<String> targetUris = new ArrayList<>();
            for(int j = i;j < i + aimCount && j < uri.size();j++){
                searchDishNames.add(dish.get(j) + "-" + System.currentTimeMillis());
                targetUris.add(uri.get(j));
            }
            queryWrapper.in(SysDish::getName,searchDishNames);
            List<SysDish> sysDishes = sysDishMapper.selectList(queryWrapper);
            if(sysDishes.isEmpty()){ // 这里找到是数量有可能是不满足要求上传的count数量的
                // 说明找到目标
               for (int l = 0;l<searchDishNames.size();l++,uploadedCount++){
                   // 添加菜品信息
                   // 水面1s 免得没屏蔽掉ip
                   try {
                       Thread.sleep(1000);
                   } catch ( InterruptedException e ) {
                       throw new RuntimeException(e);
                   }
                   SysDish sysDish = new SysDish();
                   sysDish.setCreateTime(LocalDateTime.now());
                   sysDish.setIsDelete(SysConstant.ALIVE);
                   sysDish.setIsMake(true);
                   sysDish.setUpdateTime(LocalDateTime.now());
                   sysDish.setName(searchDishNames.get(l));
                   sysDishMapper.insert(sysDish);
                   // 开始添加菜品制作流程信息
                   DishMake dishMake = new DishMake();
                   dishMake.setDishId(sysDish.getId());
                   dishMake.setIsDelete(SysConstant.ALIVE);
                   dishMake.setCreateTime(LocalDateTime.now());
                   String detailUrl = "https://www.xiachufang.com" + targetUris.get(l);
                   FoodPreparation foodPreparation = spiderPreparation(detailUrl);
                   dishMake.setContent(JSONUtil.toJsonStr(foodPreparation));
                   dishMakeMapper.insert(dishMake);
               }
                log.info("添加菜品==>{}成功", searchDishNames);
               if(uploadedCount < aimCount){
                   spiderFoodPreparationBySearch(searchName,aimCount-uploadedCount,++pageIndex,null);
               }
               return ;
            }
        }
        spiderFoodPreparationBySearch(searchName,aimCount-uploadedCount,++pageIndex,null);
    }

}
