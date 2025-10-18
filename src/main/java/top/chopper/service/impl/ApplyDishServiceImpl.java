package top.chopper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.Exception.BusinessException;
import top.chopper.constant.ApplyDishConstant;
import top.chopper.mapper.ApplyDishMapper;
import top.chopper.mapper.UserMapper;
import top.chopper.pojo.ApplyDish;
import top.chopper.pojo.ApplyDishLog;
import top.chopper.pojo.User;
import top.chopper.service.ApplyDishLogService;
import top.chopper.service.ApplyDishService;
import top.chopper.utils.SecurityUtil;
import top.chopper.utils.xcfspider.SpiderUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;


/*
   @Author:ROBOT
   @DateTime:2025/9/11 23:13
   @Version:1.0.0
   @Description:
   */
@Service
public class ApplyDishServiceImpl extends ServiceImpl<ApplyDishMapper, ApplyDish> implements ApplyDishService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ApplyDishMapper applyDishMapper;
    @Autowired
    private SpiderUtil spiderUtil;
    @Autowired
    private ApplyDishLogService applyDishLogService;


    /**
     * @param applyDish
     */
    @Override
    @Transactional
    public void myAddApplyDish(ApplyDish applyDish) {
        String openid = SecurityUtil.getUserName();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid,openid);
        Integer userId = userMapper.selectOne(queryWrapper).getId();
        applyDish.setCreateTime(LocalDateTime.now());
        applyDish.setUserId(userId);
        applyDish.setStatus(ApplyDishConstant.NEED_HANDLE);
        applyDishMapper.insert(applyDish);
    }

    /**
     * 审核菜品申请处理
     * @param params
     * @return
     */
    @Override
    @Transactional
    public int reviewApplyDish(HashMap<String, Object> params) {
        ApplyDish applyDish = new ApplyDish();
        applyDish.setId((Integer) params.get("id"));
        applyDish.setStatus((String) params.get("status"));
        applyDish.setRemark((String)params.get("remark"));
        applyDish.setUpdateTime(LocalDateTime.now());
       if(applyDish.getStatus().equals("通过")){
           List<String> dishNameList = spiderUtil.spiderFoodPreparationBySearch((String) params.get("dishName"), 1, 1, null, 999, null);
           applyDish.setApplyDishName(dishNameList.get(0));
           if(dishNameList.isEmpty()){
               throw new BusinessException("未成功上传菜品信息");
           }
       }
        applyDishMapper.updateById(applyDish);
        // 判断是否需要生成通知日志
        if((boolean)params.get("generateLog") && applyDish.getStatus().equals("通过") ){
            ApplyDishLog applyDishLog = new ApplyDishLog();
            applyDishLog.setApplyDishId(applyDish.getId());
            applyDishLog.setApplyDishName(applyDish.getApplyDishName().split("-")[0]);
            applyDishLog.setCreateTime(LocalDateTime.now());
            applyDishLogService.save(applyDishLog);
        }
        return 1;
    }

}
