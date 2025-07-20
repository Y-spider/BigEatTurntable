package top.chopper.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chopper.pojo.DishMake;
import top.chopper.pojo.R;
import top.chopper.service.DishMakeService;
import top.chopper.utils.xcfspider.SpiderUtil;
import java.time.LocalDateTime;
import java.util.HashMap;

/*
   @Author:ROBOT
   @DateTime:2025/7/20 14:09
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/make")
@Tag(description = "菜品制作接口",name = "DishMakeController")
public class DishMakeController {
    @Autowired
    private DishMakeService service;
    @Autowired
    private SpiderUtil spiderUtil;

    @Operation(description = "根据id获取制作教程",summary = "根据id获取制作教程")
    @GetMapping("/select/{id}")
    public R getDishMakeById(@PathVariable("id") Integer id){
        return R.SUCCESS(service.getById(id));
    }

    @Operation(description = "为菜品添加制作教程",summary = "为菜品添加制作教程")
    @PostMapping("/add")
    public R handleAdd(@RequestBody DishMake dishMake){
        service.save(dishMake);
        return R.SUCCESS();
    }

    @Operation(description = "修改菜品制作教程",summary = "修改菜品制作教程")
    @PutMapping("/update")
    public R handleUpdate(@RequestBody DishMake dishMake){
        dishMake.setUpdateTime(LocalDateTime.now());
        service.updateById(dishMake);
        return R.SUCCESS();
    }

    @PostMapping("/add/url")
    @Operation(description = "传入菜单详情页面url自动上传菜单（目前只支持https://www.xiachufang.com）",
            summary = "传入菜单详情页面url自动上传菜单（目前只支持https://www.xiachufang.com）")
    public R saveByUrl(@RequestBody HashMap<String,String> params){
        service.addDishMakeByUrl(params.get("url"), Integer.valueOf(params.get("id")));
        return R.SUCCESS();
    }

    @PostMapping("/add/search/｛searchName｝/{count}/{startPage}")
    @Operation(description = "按照菜品名称搜索添加菜品+制作过程",summary = "按照菜品名称搜索添加菜品+制作过程")
    public R handAddWithSearch(@PathVariable("searchName") String searchName,@PathVariable("count") Integer count,@PathVariable("startPage") Integer startPage){
        spiderUtil.spiderFoodPreparationBySearch(searchName,count,startPage,null);
        return R.SUCCESS();
    }

    @PostMapping("/add/type/｛typeUrl｝/{count}/")
    @Operation(description = "按照给出的菜品分类URL 添加菜品URL(类似于https://www.xiachufang.com/category/40076/)",
            summary = "按照给出的菜品分类URL 添加菜品URL(类似于https://www.xiachufang.com/category/40076/)")
    public R handAddWithListPageUrl(@PathVariable("typeUrl") String typeUrl,@PathVariable("count") Integer count){
        spiderUtil.spiderFoodPreparationBySearch(null,count,null,typeUrl);
        return R.SUCCESS();
    }


}
