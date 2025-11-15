package top.chopper.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chopper.mapper.BillBookMapper;
import top.chopper.mapper.BillShareMapper;
import top.chopper.pojo.BillBook;
import top.chopper.pojo.BillShare;
import top.chopper.service.BillShareService;
import top.chopper.utils.SecurityUtil;

import java.util.ArrayList;
import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2025/11/15 22:13
   @Version:1.0.0
   @Description:
   */
@Service
public class BillShareServiceImpl extends ServiceImpl<BillShareMapper, BillShare> implements BillShareService {

    @Autowired
    private BillShareMapper billShareMapper;
    @Autowired
    private BillBookMapper billBookMapper;
    /**
     * @param openid 目标用户的标识，如果为null，则表示查询当前用户的
     * @return
     */
    @Override
    public List<BillBook> listBillBookByOpenid(String openid) {
        LambdaQueryWrapper<BillShare> queryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isEmpty(openid)){
            queryWrapper.eq(BillShare::getOpenid, SecurityUtil.getUserName());
        }else{
            queryWrapper.eq(BillShare::getOpenid,openid);
        }
        List<BillShare> billShares = billShareMapper.selectList(queryWrapper);
        List<Integer> billIds = billShares.stream().map(BillShare::getBillId).toList();
        // 查询目标账单
        if(ObjectUtil.isEmpty(billIds)){
            return new ArrayList<BillBook>();
        }else{
            return billBookMapper.selectBatchIds(billIds);
        }
    }
}
