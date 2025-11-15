package top.chopper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.chopper.pojo.BillBook;
import top.chopper.pojo.BillShare;

import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/11/15 22:13
   @Version:1.0.0
   @Description:
   */
public interface BillShareService extends IService<BillShare> {
    List<BillBook> listBillBookByOpenid(String openid);
}
