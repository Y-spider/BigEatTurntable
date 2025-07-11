package top.chopper.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/*
   @Author:ROBOT
   @DateTime:2025/7/10 21:27
   @Version:1.0.0
   @Description: 封装用户分页按条件获取用户信息
   */
@Data
public class QueryPageDto {
    private HashMap<String,Object> queryMap;
    private Integer page;
    private Integer limit;

    public void setQueryMap(HashMap<String, Object> queryMap) {
        this.queryMap = queryMap;
    }

    public QueryPageDto(){
        // 默认分数数量为10
        this.limit = 10;
        // 默认起始页为1
        this.page = 1;
        // 默认query参数为空
        this.queryMap = new HashMap<String,Object>();
    }

    /**
     * 返回是否能否获取到对应查询字段的值，如果有则返回值，没有则返回false
     * @param queryName 条件名称
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean queryConditionIsExists(String queryName) {
        // 使用Optional来安全地获取值
        return queryMap.get(queryName)!=null;
    }

    public Object getQueryConditionValue(String queryName){
        return  queryMap.get(queryName);
    }



}
