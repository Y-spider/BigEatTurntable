package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.BillBook;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:24
   @Version:1.0.0
   @Description:
   */
public interface BillBookService extends IService<BillBook> {
    void deleteBillBook(Integer id);
}
