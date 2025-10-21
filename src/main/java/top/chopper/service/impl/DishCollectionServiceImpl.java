package top.chopper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.mapper.DishCollectionMapper;
import top.chopper.pojo.DishCollection;
import top.chopper.service.DishCollectionService;
import top.chopper.utils.SecurityUtil;

/*
   @Author:ROBOT
   @DateTime:2025/10/3 14:42
   @Version:1.0.0
   @Description:
   */
@Service
public class DishCollectionServiceImpl extends ServiceImpl<DishCollectionMapper, DishCollection> implements DishCollectionService {
    @Autowired
    private DishCollectionMapper dishCollectionMapper;


    /**
     * @param id
     * @return
     */
    @Override
    @Transactional
    public int myDeleteById(Integer id) {
        return dishCollectionMapper.deleteMyCollectionBuId(id, SecurityUtil.getUserName());
    }
}
