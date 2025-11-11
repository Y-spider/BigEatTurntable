package top.chopper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.chopper.pojo.BillBook;
import top.chopper.pojo.R;
import top.chopper.service.BillBookService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;

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

    @PostMapping("/add")
    @Operation(description = "新增账本",summary = "新增账本")
    @Transactional
    public R handleAddBook(@RequestBody BillBook billBook){
        billBook.setCreateTime(LocalDateTime.now());
        billBook.setOpenid(SecurityUtil.getUserName());
        billBook.setIsShare(false);
        billBookService.save(billBook);
        return R.SUCCESS();
    }

    @GetMapping("/list")
    @Operation(description = "获取当前用户所有账本",summary = "获取当前用户所有账本")
    public R listAllBillBook(){
        String openid = SecurityUtil.getUserName();
        LambdaQueryWrapper<BillBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillBook::getOpenid,openid);
        List<BillBook> list = billBookService.list(queryWrapper);
        return R.SUCCESS(list);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(description = "删除账本(只有创建者有权限)",summary = "删除账本(只有创建者有权限)")
    public R handleDelete(@PathVariable("id") Integer id){
            billBookService.deleteBillBook(id);
            return R.SUCCESS();
    }

    // 暂时不提供修改接口

}
