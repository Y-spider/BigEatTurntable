package top.chopper.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chopper.Exception.BusinessException;
import top.chopper.constant.TurnTableType;
import top.chopper.mapper.RotationRecordMapper;
import top.chopper.mapper.TurnTableMapper;
import top.chopper.pojo.RotationRecord;
import top.chopper.pojo.TurnTable;
import top.chopper.service.TurnTableService;
import top.chopper.utils.MinioUtil;
import top.chopper.utils.SecurityUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/*
   @Author:ROBOT
   @DateTime:2025/7/6 17:36
   @Version:1.0.0
   @Description:
   */
@Service
@Slf4j
public class TurnTableServiceImpl extends ServiceImpl<TurnTableMapper, TurnTable> implements TurnTableService {
    @Autowired
    private TurnTableMapper mapper;
    @Autowired
    private RotationRecordMapper recordMapper;
    @Autowired
    private MinioUtil minioUtil;


    /**
     * @param turnTable 这里最主要的是接受的是id 和 内容 有以下两种可能：
     *                  1. 用户自定义系统转盘 那就是新建转盘
     *                  2. 修改用户自定义转盘(非系统转盘)
     */
    @Override
    @Transactional
    public Integer updateTurnTable(TurnTable turnTable) {
        TurnTable oldTurntable = mapper.selectById(turnTable.getId());
        if (oldTurntable.getType().equals(TurnTableType.TURN_TABLE_TYPE_SYS) || oldTurntable.getType().equals(TurnTableType.TURN_TABLE_TYPE_HOT)) {
            // 情况1 新建用户自定义转盘,以系统或者热门转盘为基础
            if(checkTitleRepeat(turnTable.getTitle())){
                throw new BusinessException("转盘名已存在!");
            }
            oldTurntable.setCreateTime(LocalDateTime.now());
            oldTurntable.setUpdateTime(LocalDateTime.now());
            oldTurntable.setContent(turnTable.getContent());
            oldTurntable.setId(null);
            oldTurntable.setType(TurnTableType.TURN_TABLE_TYPE_OPT);
            oldTurntable.setOpenid(SecurityUtil.getUserName());
            if(turnTable.getTitle()!=null){
                oldTurntable.setTitle(turnTable.getTitle());
            }
            mapper.insert(oldTurntable);
        } else {
            // 非创建者无修改权限
            if(!oldTurntable.getOpenid().equals(SecurityUtil.getUserName())){
                throw new BusinessException("无修改权限");
            }
            // 情况2 修改用户自定义转盘
            if(!oldTurntable.getTitle().equals(turnTable.getTitle())){
                // 修改记录
                LambdaUpdateWrapper<RotationRecord> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(RotationRecord::getTurntableName,turnTable.getTitle());
                updateWrapper.eq(RotationRecord::getTurntableId,turnTable.getId());
                int updated = recordMapper.update(updateWrapper);
                log.info("用户:{}修改了转盘oldName={}为newName={},旋转记录受影响条数为={}",SecurityUtil.getUserName(),oldTurntable.getTitle(),turnTable.getTitle(),updated);
                oldTurntable.setTitle(turnTable.getTitle());
            }
            oldTurntable.setContent(turnTable.getContent());
            oldTurntable.setUpdateTime(LocalDateTime.now());
            oldTurntable.setIsRepeat(turnTable.getIsRepeat());
            mapper.updateById(oldTurntable);
        }
        return oldTurntable.getId();

    }

    /**
     * @param id
     */
    @Override
    @Transactional
    public void myDeleteTurntableById(Integer id) {
        LambdaQueryWrapper<RotationRecord> recordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        recordLambdaQueryWrapper.eq(RotationRecord::getTurntableId,id);
        TurnTable turnTable = mapper.selectById(id);
        int deleted = recordMapper.delete(recordLambdaQueryWrapper);
        mapper.deleteById(id);
        log.info("成功删除用户==>{}自定义转盘==》{}旋转记录受影响条数为:{}",SecurityUtil.getUserName(),turnTable.getTitle(),deleted);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public HashMap<String, Object> getErCodeUrl(Integer id) {
        HashMap<String, Object> result = new HashMap<>();
        TurnTable turnTable = mapper.selectById(id);
        if(ObjectUtil.isNull(turnTable)){
            log.error("获取ErCodeUrl传入参数[id]不存在==>{}",id);
            throw new BusinessException("系统繁忙，请稍后重试！");
        }
        if(StrUtil.isNotEmpty(turnTable.getErCodeUrl())){
            result.put("url",turnTable.getErCodeUrl());
            return result;
        }
        String accessToken = getAccessToken();
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token="+accessToken;
        HashMap<String, Object> params = new HashMap<>();
        params.put("path","pages/detail/detail?id="+id+"&shareOpenid="+SecurityUtil.getUserName()+"&backUrl=/pages/index/index");
        params.put("width",430);
        // ⚠️ 将参数转为 JSON 字符串发送
        String jsonBody = JSONUtil.toJsonStr(params);
        HttpRequest request = new HttpRequest(requestUrl);
        request.setMethod(Method.POST);
        request.header("Content-Type", "application/json");
        request.body(jsonBody);
        HttpResponse response = request.execute();
        // 判断返回类型
        String contentType = response.header("Content-Type");
        if (contentType != null && contentType.contains("json")) {
            // 如果返回是 JSON，说明有错误
            log.error("请求转盘{}生成二维码错误==>{}", id, response);
            throw new BusinessException("系统繁忙，请稍后再试！");
        }
        else{
            String fileName =  "qrcode/" + UUID.randomUUID() + ".jpg";
            HashMap<String, String> map = minioUtil.uploadFile(response.bodyBytes(), fileName, "image/jpeg");
            result.put("url",map.get("url"));
            turnTable.setErCodeUrl(map.get("url"));
            turnTable.setUpdateTime(LocalDateTime.now());
            mapper.updateById(turnTable);
            return result;
        }
    }

    private boolean checkTitleRepeat(String title){
        LambdaQueryWrapper<TurnTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(TurnTable::getTitle)
                .eq(TurnTable::getOpenid,SecurityUtil.getUserName());
        List<String> titles = mapper.selectList(queryWrapper).stream().map(TurnTable::getTitle).toList();
        for (String tt : titles){
            if(title.equals(tt)){
                return true;
            }
        }
        return false;
    }

    private String getAccessToken(){
        String url = "https://api.weixin.qq.com/cgi-bin/token";
        HashMap<String, Object> params = new HashMap<>();
        params.put("grant_type", "client_credential");
        params.put("appid", "wxfbf664952970b1bb");
        params.put("secret", "9a8381b008724997047978c6b8966bf3");

        String result = HttpUtil.get(url, params);
        JSONObject entries = JSONUtil.parseObj(result);
        return entries.get("access_token").toString();
    }

}
