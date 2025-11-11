package top.chopper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.Exception.BusinessException;
import top.chopper.mapper.BillBookMapper;
import top.chopper.mapper.BillRecordMapper;
import top.chopper.pojo.BillBook;
import top.chopper.pojo.BillRecord;
import top.chopper.service.BillBookService;
import top.chopper.utils.SecurityUtil;

/*
   @Author:ROBOT
   @DateTime:2025/11/9 17:26
   @Version:1.0.0
   @Description:
   */
@Service
@Slf4j
public class BillBookServiceImpl extends ServiceImpl<BillBookMapper, BillBook> implements BillBookService {

    @Autowired
    private BillRecordMapper billRecordMapper;
    @Autowired
    private BillBookMapper billBookMapper;
    /**
     * @param id
     */
    @Override
    @Transactional
    public void deleteBillBook(Integer id) {
        if(id==null){
            throw new BusinessException("参数错误!id=null");
        }
        BillBook billBook = billBookMapper.selectById(id);
        String openid = SecurityUtil.getUserName();
        if(!billBook.getOpenid().equals(openid)){
            log.error("用户{}尝试删除用户{}创建的账本:{}",openid,billBook.getOpenid(),billBook.getTitle());
            throw new BusinessException("没有删除权限");
        }
        if(billBook.getIsShare()){
            log.error("好友：{}准备删除用户：{}创建的账本=>{}",openid,billBook.getOpenid(),billBook.getTitle());
            throw new BusinessException("不是账本创建者，无权限删除！");
        }

        billBookMapper.deleteById(id);
        LambdaQueryWrapper<BillRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillRecord::getBillBookId,id);
        billRecordMapper.delete(queryWrapper);
    }
}
