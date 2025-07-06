package top.chopper.mapper;

import top.chopper.pojo.UtilComment;

import java.util.List;

/*
   @Author:ROBOT
   @DateTime:2024/4/29 16:24
   @Version:1.0.0
   @Description:
   */
public interface UtilMapper {
    List<UtilComment> getComment(String tableName);
}
