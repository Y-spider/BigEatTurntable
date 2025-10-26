package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chopper.Exception.BusinessException;
import top.chopper.constant.TurnTableType;
import top.chopper.dto.QueryPageDto;
import top.chopper.pojo.R;
import top.chopper.pojo.TurnTable;
import top.chopper.service.TurnTableService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:38
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/turntable")
@Tag(name = "轮盘操作接口")
@Slf4j
public class TurnTableController {
    @Autowired
    private TurnTableService service;

    /**
     * 获取当前用户的所有定义的转盘，一般都不会太多，所以这里就不采用分页的形式了
     * @return
     */
    @Operation(description = "根据openid获取微信用户的转盘的名称和id,createTime,type",summary = "根据openid获取微信用户的转盘的名称和id,createTime,type")
    @GetMapping("/list/user")
    public R listByOpenid(){
        String openid = SecurityUtil.getUserName();
        LambdaQueryWrapper<TurnTable> queryWrapper = new LambdaQueryWrapper<TurnTable>()
                .select(TurnTable::getId,TurnTable::getTitle,TurnTable::getCreateTime,TurnTable::getUpdateTime,TurnTable::getType)
                .eq(TurnTable::getOpenid, openid)
                .orderByDesc(TurnTable::getUpdateTime)
                .orderByDesc(TurnTable::getCreateTime);
        return R.SUCCESS(service.list(queryWrapper));
    }



    @Operation(description = "获取系统定义的所有转盘",summary = "获取系统定义的所有转盘")
    @GetMapping("/list/system")
    public R getById() {
        LambdaQueryWrapper<TurnTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TurnTable::getType, TurnTableType.TURN_TABLE_TYPE_SYS)
                .orderByDesc(TurnTable::getOrderNumber)
                .orderByDesc(TurnTable::getCreateTime);
        return R.SUCCESS(service.list(queryWrapper));
    }

    @Operation(description = "获取全部热门转盘",summary = "获取全部热门转盘")
    @GetMapping("/list/hot")
    public R getHotTurntable(){
        LambdaQueryWrapper<TurnTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TurnTable::getType, TurnTableType.TURN_TABLE_TYPE_HOT)
                .orderByDesc(TurnTable::getOrderNumber);
        return R.SUCCESS(service.list(queryWrapper));
    }

    @Operation(description = "根据转盘id获转盘信息",summary="根据转盘id获转盘信息")
    @GetMapping("/querySingle/{id}")
    public R querySingleTurntableById(@PathVariable("id") Integer id){
        TurnTable turnTable = service.getById(id);
       if(turnTable.getType().equals(TurnTableType.TURN_TABLE_TYPE_OPT)){
           if(!turnTable.getOpenid().equals(SecurityUtil.getUserName())){
               turnTable.setCanEdit(false);
           }
       }
        return R.SUCCESS(turnTable);
    }

    @Operation(description = "后台条件分页获取轮盘信息",summary = "后台条件分页获取轮盘信息")
    @PostMapping("/list/page")
    public R handleListByPage(@RequestBody  QueryPageDto queryPageDto){
        LambdaQueryWrapper<TurnTable> queryWrapper = new LambdaQueryWrapper<>();
        Page<TurnTable> page = new Page<>(queryPageDto.getPage(),queryPageDto.getLimit());
        queryWrapper.like(queryPageDto.queryConditionIsExists("title"),TurnTable::getTitle,queryPageDto.getQueryConditionValue("title"))
                .eq(queryPageDto.queryConditionIsExists("type"),TurnTable::getType,queryPageDto.getQueryConditionValue("type"))
                .eq(false,TurnTable::getOpenid,queryPageDto.getQueryConditionValue("openid"))
                .orderByDesc(TurnTable::getOrderNumber)
                .orderByDesc(TurnTable::getCreateTime);
        return R.SUCCESS( service.page(page,queryWrapper));
    }

    @Operation(description = "删除轮盘信息根据id",summary = "删除轮盘信息根据id")
    @DeleteMapping("/delete/{id}")
    public R handleDeleteById(@PathVariable("id") Integer id){
        service.myDeleteTurntableById(id);
        return R.SUCCESS();
    }

    @Operation(description = "修改轮盘信息根据轮盘id",summary = "修改轮盘信息根据轮盘id")
    @PutMapping("/update")
    public R handleUpdateTurntable(@RequestBody TurnTable turnTable){
        if(!turnTable.getIsRepeat()){
            Set duplicate = this.checkHaveRepeatPrize(turnTable.getContent());
            if(!duplicate.isEmpty()){
                throw new BusinessException("不允许重复奖项:"+duplicate.stream().toList());
            }
        }
        turnTable.setUpdateTime(LocalDateTime.now());
        service.updateTurnTable(turnTable);
        return R.SUCCESS();
    }

    @Operation(description = "修改轮盘信息根据轮盘id",summary = "修改轮盘信息根据轮盘id")
    @PutMapping("/update/admin")
    public R handleUpdateTurntableAdmin(@RequestBody TurnTable turnTable){
        service.updateById(turnTable);
        return R.SUCCESS();
    }

    @Operation(description = "用户端添加轮盘信息",summary = "用户端添加轮盘信息")
    @PostMapping("/client/add")
    public R handleAddTurntableClinet(@RequestBody TurnTable turnTable){
        // 这里需要判断具体的添加的角色，来进行一些信息的填充
        turnTable.setOpenid(SecurityUtil.getUserName());
        // 不允许用户下有相同名称的转盘
        LambdaQueryWrapper<TurnTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TurnTable::getOpenid,turnTable.getOpenid())
                .eq(TurnTable::getTitle,turnTable.getTitle());
        boolean exists = service.exists(queryWrapper);
        if(exists){
            throw new BusinessException("转盘名已存在!");
        }
        // 不重复抽，不允许有相同的奖品名称
        if(!turnTable.getIsRepeat()){
            Set duplicate = this.checkHaveRepeatPrize(turnTable.getContent());
            if(!duplicate.isEmpty()){
                throw new BusinessException("不允许重复奖项:"+duplicate.stream().toList());
            }
        }
        LocalDateTime now = LocalDateTime.now();
        turnTable.setUpdateTime(now);
        turnTable.setType(TurnTableType.TURN_TABLE_TYPE_OPT);
        turnTable.setCreateTime(now);
        return R.SUCCESS(service.save(turnTable));
    }

    @Operation (description = "后台上传转盘",summary = "后台上传转盘")
    @PostMapping("/admin/add")
    public R handleAddTurntableAdmin(@RequestBody TurnTable turnTable){
        turnTable.setCreateTime(LocalDateTime.now());
        return R.SUCCESS(service.save(turnTable));
    }


    /**
     * @param content 奖品JSON字符串
     * @return 返回是否含有相同奖品名称
     */
    private Set checkHaveRepeatPrize(String content){
        Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(content);
        // 2️⃣ 检查是否有重复
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        while(matcher.find()){
            String textValue = matcher.group(1);
            if(!seen.add(textValue)){
                duplicates.add(textValue);
            }
        }
      return duplicates;
    }

    /**
     * @param conten 奖品JSON字符串
     * @return
     */
    private String handlePrizeShowText(String conten){
        return "";
    }

}
