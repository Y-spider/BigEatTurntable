package top.chopper.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.Exception.BusinessException;
import top.chopper.constant.BillConstant;
import top.chopper.mapper.BillBookMapper;
import top.chopper.mapper.BillRecordMapper;
import top.chopper.mapper.BillShareMapper;
import top.chopper.mapper.UserMapper;
import top.chopper.pojo.BillBook;
import top.chopper.pojo.BillRecord;
import top.chopper.pojo.BillShare;
import top.chopper.pojo.User;
import top.chopper.service.BillBookService;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private BillShareMapper billShareMapper;
    @Autowired
    private UserMapper userMapper;
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
        if(billBook.getTitle().equals("默认账本") || billBook.getType().equals(BillConstant.BILL_BOOK_TYPE_DEFAULT)){
            throw new BusinessException("默认账本无法删除!");
        }
        billBookMapper.deleteById(id);
        LambdaQueryWrapper<BillRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillRecord::getBillBookId,id);
        billRecordMapper.delete(queryWrapper);
        // 更新默认选择账单
        if(billBook.getIsChoose()){
            LambdaUpdateWrapper<BillBook> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(BillBook::getOpenid,SecurityUtil.getUserName())
                    .eq(BillBook::getTitle,"默认账本");
            updateWrapper.set(BillBook::getIsChoose,true);
            billBookMapper.update(updateWrapper);
        }
        // 如果为共享账单需要更新主账单的信息
        if(ObjectUtil.isNotEmpty(billBook.getParentId())){
            BillBook parentBillBook = billBookMapper.selectById(billBook.getParentId());
            if(ObjectUtil.isEmpty(parentBillBook)){
                throw new BusinessException("当前账本已经原来作者删除!!!");
            }else{
                parentBillBook.setShareCount(parentBillBook.getShareCount()-1);
            }
            billBookMapper.updateById(parentBillBook);
        }
    }

    /**
     * 获取当前用户的选择的账单
     * @return
     */
    @Override
    public BillBook getCurrentChooseBookBill() {
        LambdaQueryWrapper<BillBook> queryWrapper = new LambdaQueryWrapper<>();
        String openid = SecurityUtil.getUserName();
        queryWrapper.eq(BillBook::getOpenid,openid).eq(BillBook::getIsChoose,true);
        BillBook billBook = billBookMapper.selectOne(queryWrapper);
        if(ObjectUtil.isEmpty(billBook)){
            LambdaQueryWrapper<BillShare> shareLambdaQueryWrapper = new LambdaQueryWrapper<>();
            shareLambdaQueryWrapper.select(BillShare::getBillId);
            shareLambdaQueryWrapper.eq(BillShare::getOpenid,openid).eq(BillShare::getIsChoose,true);
            BillShare billShare = billShareMapper.selectOne(shareLambdaQueryWrapper);
            if(ObjectUtil.isEmpty(billShare)){
                log.error("当前用户没有选择账单，业务不合理，请排查！");
                throw new BusinessException("系统繁忙，请稍后重试！");
            }
            return billBookMapper.selectById(billShare.getBillId());
        }else{
            if(ObjectUtil.isNotEmpty(billBook.getParentId())){
                BillBook parentBillBook = billBookMapper.selectById(billBook.getParentId());
                if(ObjectUtil.isEmpty(parentBillBook)){
                    // 之前选择的已经被删除了，这里切换到默认账单
                    queryWrapper =new  LambdaQueryWrapper<BillBook>();
                    queryWrapper.eq(BillBook::getOpenid,openid).eq(BillBook::getType,BillConstant.BILL_BOOK_TYPE_DEFAULT);
                    return billBookMapper.selectOne(queryWrapper);
                }
            }
            return billBook;
        }
    }

    /**
     * @return
     */
    @Override
    public List<BillBook> listCurrentBillBook() {
        LambdaQueryWrapper<BillBook> queryWrapper = new LambdaQueryWrapper<>();
        String openid = SecurityUtil.getUserName();
        queryWrapper.eq(BillBook::getOpenid,openid);
        List<BillBook> billBooks = billBookMapper.selectList(queryWrapper); // 查询出我创建的(不包含共享的)
        for (BillBook billBook : billBooks) {
            if(StrUtil.equals(billBook.getOpenid(),SecurityUtil.getUserName())){
                billBook.setIsEdit(true);
            }else{
                billBook.setIsEdit(false);
            }
            if(ObjectUtil.isNotEmpty(billBook.getParentId())){
                BillBook parentBillBook = billBookMapper.selectById(billBook.getParentId());
                if(ObjectUtil.isEmpty(parentBillBook)){
                    billBook.setIsParentDelete(true);
                }else{
                    billBook.setIsParentDelete(false);
                    billBook.setBillCreateUserName(parentBillBook.getName());
                }
            }else{
                billBook.setIsParentDelete(false);
                billBook.setBillCreateUserName(billBook.getName());
            }


        }
        return billBooks;
    }

    /**
     * 创建默认账本，不可删除该账本
     */
    @Override
    @Transactional
    public void createDefaultBook(String openid,String userName) {
        BillBook billBook = new BillBook();
        billBook.setIsShare(false);
        billBook.setCreateTime(LocalDateTime.now());
        billBook.setIconUrl("https://www.sunnygo.chat/images/eat-big-turntable/eat_turntable_icon/账本.svg");
        billBook.setOpenid(openid);
        billBook.setIsChoose(true);
        billBook.setName(userName);
        billBook.setType("默认账本");
        billBook.setTitle("默认账本");
        billBook.setShareCount(1);
        billBookMapper.insert(billBook);
    }

    /**
     * 获取当前账本的所有持有用户
     * @return
     */
    @Override
    public List<User> listAllBookUser() {
        BillBook currentChooseBookBill = getCurrentChooseBookBill();
        LambdaQueryWrapper<BillBook> queryWrapper = new LambdaQueryWrapper<>();
        Integer originId = currentChooseBookBill.getId();
        if(ObjectUtil.isNotEmpty(currentChooseBookBill.getParentId())){
            originId = currentChooseBookBill.getParentId();
        }
        // 1. 先获取到原始的账本信息
        BillBook originBillBook = billBookMapper.selectById(originId);
        // 2. 获取参与账本的用户信息
        queryWrapper.eq(BillBook::getParentId,originId);
        List<String> openidList = billBookMapper.selectList(queryWrapper).stream().map(BillBook::getOpenid).collect(Collectors.toList());
        openidList.add(originBillBook.getOpenid());
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getOpenid,openidList)
                .select(User::getId,User::getAvatar,User::getName,User::getLimitUpload,User::getEmail);
        return userMapper.selectList(userLambdaQueryWrapper);
    }

    /**
     * 加入好友分享的账本
     * @param bookId
     */
    @Override
    @Transactional
    public void joinShareBook(Integer bookId) {
        LambdaQueryWrapper<BillBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillBook::getParentId,bookId).eq(BillBook::getOpenid,SecurityUtil.getUserName());
        BillBook existBillBook = billBookMapper.selectOne(queryWrapper);
        if(ObjectUtil.isNotEmpty(existBillBook)){
            return;
        }
        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(User::getOpenid,SecurityUtil.getUserName());
        User user = userMapper.selectOne(queryWrapper1);
        BillBook billBook = billBookMapper.selectById(bookId);
        BillBook newBillBook = new BillBook();
        newBillBook.setOpenid(SecurityUtil.getUserName());
        newBillBook.setParentId(billBook.getId());
        newBillBook.setTitle(billBook.getTitle());
        newBillBook.setType(billBook.getType());
        newBillBook.setCreateTime(LocalDateTime.now());
        newBillBook.setIconUrl(billBook.getIconUrl());
        newBillBook.setIsChoose(false);
        newBillBook.setShareCount(billBook.getShareCount()+1);
        newBillBook.setIsShare(true);
        newBillBook.setName(user.getName());
        billBookMapper.insert(newBillBook);
        // 修改billbook的分享人数
        billBook.setShareCount(billBook.getShareCount() + 1);
        billBookMapper.updateById(billBook);
        // 切换用户的选择账本
        changeChooseBillBook(newBillBook);
    }

    /**
     * @param billBook
     */
    @Override
    @Transactional
    public void changeChooseBillBook(BillBook billBook) {
        String openid = SecurityUtil.getUserName();
        LambdaUpdateWrapper<BillBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BillBook::getOpenid,openid);
        updateWrapper.set(BillBook::getIsChoose,false);
        billBookMapper.update(updateWrapper);
        billBook.setIsChoose(true);
        billBook.setUpdateTime(LocalDateTime.now());
        billBookMapper.updateById(billBook);
    }
}
