package top.chopper.pojo;

/*
   @Author:ROBOT
   @DateTime:2024/5/2 23:11
   @Version:1.0.0
   @Description:关于统一返回实体类中的code码进行统一管理
   */
@SuppressWarnings("all")
public class RCode {
    public static final Integer ERROR = -1;
    public static final Integer SUCCESS = 200;
    public static final Integer SENSITIVE = 110;

    public static final Integer EXPIRETOKEN = -99; // 用户信息已经过期，需要用户重新登陆


}
