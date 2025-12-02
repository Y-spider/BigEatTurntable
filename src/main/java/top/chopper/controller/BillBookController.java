package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.chopper.Exception.BusinessException;
import top.chopper.pojo.BillBook;
import top.chopper.pojo.R;
import top.chopper.service.BillBookService;
import top.chopper.service.UserService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:31
   @Version:1.0.0
   @Description:
   */
@RestController
@RequestMapping("/book")
@Tag(description = "账单处理器",name = "BillBookController")
public class BillBookController {
    @Autowired
    private BillBookService billBookService;
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @Operation(description = "新增账本",summary = "新增账本")
    @Transactional
    public R handleAddBook(@RequestBody BillBook billBook){
        String openid = SecurityUtil.getUserName();
        LambdaUpdateWrapper<BillBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BillBook::getOpenid,openid);
        updateWrapper.set(BillBook::getIsChoose,false);
        billBookService.update(updateWrapper);
        // 添加新账本
        LambdaQueryWrapper<BillBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillBook::getOpenid,openid).eq(BillBook::getTitle,billBook.getTitle());
        if (billBookService.exists(queryWrapper)) {
            throw new BusinessException(String.format("名称[%s]已存在",billBook.getTitle()));
        }
        billBook.setCreateTime(LocalDateTime.now());
        billBook.setOpenid(openid);
        billBook.setName(userService.getCurrentUser().getName());
        billBook.setIsShare(true);
        billBook.setIsChoose(true);
        billBook.setShareCount(1);
        billBookService.save(billBook);
        return R.SUCCESS();
    }

    @GetMapping("/list")
    @Operation(description = "获取当前用户所有账本",summary = "获取当前用户所有账本")
    public R listAllBillBook(){
        return R.SUCCESS(billBookService.listCurrentBillBook());
    }

    @DeleteMapping("/delete/{id}")
    @Operation(description = "删除账本(只有创建者有权限)",summary = "删除账本(只有创建者有权限)")
    public R handleDelete(@PathVariable("id") Integer id){
            billBookService.deleteBillBook(id);
            return R.SUCCESS();
    }

    // 暂时不提供修改接口
    @GetMapping("/choose")
    @Operation(description = "获取当前选中的账本信息",summary = "获取当前选中的账本信息")
    public R handleGetCurrentBillBook(){
        return R.SUCCESS(billBookService.getCurrentChooseBookBill());
    }

    @PutMapping("/change")
    @Operation(description ="修改当前账单",summary = "修改当前账单")
    public R handleChangeChooseBillBook(@RequestBody BillBook billBook){
        billBookService.changeChooseBillBook(billBook);
        return R.SUCCESS();
    }

    @GetMapping("/share/user")
    @Operation(description ="获取当前账本所有的用户信息",summary = "获取当前账本所有的用户信息")
    public R handleListAllShareUser(){
        return R.SUCCESS(billBookService.listAllBookUser());
    }

    @PostMapping("/join/{id}")
    @Operation(description ="加入共享账本",summary = "加入共享账本")
    public R handleJoinBook(@PathVariable("id") Integer id){
        billBookService.joinShareBook(id);
        return R.SUCCESS();
    }
}
