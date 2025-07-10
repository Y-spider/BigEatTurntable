package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;
import top.chopper.constant.TurnTableType;
import top.chopper.dto.QueryPageDto;
import top.chopper.pojo.R;
import top.chopper.pojo.TurnTable;
import top.chopper.service.TurnTableService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:38
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/truntable")
@Tag(name = "轮盘操作接口")
public class TurnTableController {
    @Autowired
    private TurnTableService service;

    /**
     * 获取当前用户的所有定义的转盘，一般都不会太多，所以这里就不采用分页的形式了
     * @return
     */
    @Operation(description = "根据openid获取微信用户的随机转盘",summary = "根据openid获取微信用户的随机转盘")
    @GetMapping("/list/user")
    public R listByOpenid(){
        String openid = SecurityUtil.getUserName();
        LambdaQueryWrapper<TurnTable> queryWrapper = new LambdaQueryWrapper<TurnTable>().eq(TurnTable::getOpenid, openid);
        return R.SUCCESS(service.list(queryWrapper));
    }

    @Operation(description = "获取系统定义的所有转盘",summary = "获取系统定义的所有转盘")
    @GetMapping("/list/system")
    public R getById() {
        LambdaQueryWrapper<TurnTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TurnTable::getType, TurnTableType.TURN_TABLE_TYPE_SYS);
        return R.SUCCESS(service.list(queryWrapper));
    }

    @Operation(description = "根据转盘id获转盘信息",summary="根据转盘id获转盘信息")
    @GetMapping("/querySingle/{id}")
    public R querySingleTurntableById(@PathVariable("id") Integer id){
        return R.SUCCESS(service.getById(id));
    }

    @Operation(description = "后台条件分页获取轮盘信息",summary = "后台条件分页获取轮盘信息")
    @PostMapping("/list/page")
    public R handleListByPage(@RequestBody  QueryPageDto queryPageDto){
        LambdaQueryWrapper<TurnTable> queryWrapper = new LambdaQueryWrapper<>();
        Page<TurnTable> page = new Page<>(queryPageDto.getPage(),queryPageDto.getLimit());
        queryWrapper.like(queryPageDto.queryConditionIsExists("title"),TurnTable::getTitle,queryPageDto.getQueryConditionValue("title"))
                .eq(queryPageDto.queryConditionIsExists("type"),TurnTable::getType,(Integer)queryPageDto.getQueryConditionValue("type"))
                .eq(queryPageDto.queryConditionIsExists("openid"),TurnTable::getOpenid,queryPageDto.getQueryConditionValue("openid"));
        return R.SUCCESS( service.page(page,queryWrapper));
    }

    @Operation(description = "删除轮盘信息根据id",summary = "删除轮盘信息根据id")
    @DeleteMapping("/delete/{id}")
    public R handleDeleteById(@PathVariable("id") Integer id){
        return R.SUCCESS(service.removeById(id));
    }

    @Operation(description = "修改轮盘信息根据轮盘id",summary = "修改轮盘信息根据轮盘id")
    @PutMapping("/update/{id}")
    public R handleUpdateTurntable(@RequestBody TurnTable turnTable){
        turnTable.setUpdateTime(LocalDateTime.now());
        return R.SUCCESS(service.updateById(turnTable));
    }

    @Operation(description = "添加轮盘信息",summary = "添加轮盘信息")
    @PostMapping("/add")
    public R handleAddTurntable(@RequestBody TurnTable turnTable){
        // 这里需要判断具体的添加的角色，来进行一些信息的填充
        turnTable.setCreateTime(LocalDateTime.now());
        return R.SUCCESS(service.save(turnTable));
    }







}
