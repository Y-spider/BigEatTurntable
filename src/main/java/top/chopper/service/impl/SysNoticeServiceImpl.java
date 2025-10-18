package top.chopper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chopper.Exception.BusinessException;
import top.chopper.mapper.SysNoticeMapper;
import top.chopper.pojo.SysNotice;
import top.chopper.service.SysNoticeService;

/*
   @Author:ROBOT
   @DateTime:2025/7/26 15:56
   @Version:1.0.0
   @Description:
   */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    @Autowired
    private SysNoticeMapper sysNoticeMapper;

    /**
     * @param id
     * @return
     */
    @Override
    public int myDeleteNoticeById(Integer id) {
        SysNotice sysNotice = sysNoticeMapper.selectById(id);
        if(sysNotice==null){
            throw new BusinessException("通知不存在");
        }
        if(sysNotice.getActive()){
            throw new BusinessException("不能删除已生效通知");
        }
        Long count = sysNoticeMapper.selectCount(null);
        if(count <= 1){
            throw  new BusinessException("至少存在一个通知信息");
        }
        sysNoticeMapper.deleteById(id);
        return 1;
    }
}
