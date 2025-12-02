package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.BillBook;
import top.chopper.pojo.User;

import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:24
   @Version:1.0.0
   @Description:
   */
public interface BillBookService extends IService<BillBook> {
    void deleteBillBook(Integer id);

    BillBook getCurrentChooseBookBill();


    List<BillBook> listCurrentBillBook();

    void createDefaultBook(String openid,String userName);

    List<User> listAllBookUser();

    void joinShareBook(Integer bookId);

    void changeChooseBillBook(BillBook billBook);
}
