package top.chopper.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

/*
   @Author:ROBOT
   @DateTime:2024/4/16 17:59
   @Version:1.0.0
   @Description:
   */
@Schema(name = "R<T>",description = "统一返回前端对象")
@SuppressWarnings("all")
public class R<T>{
    @Schema(name = "errMsg",description = "当前请求失败时，描述错误信息")
    private String errMsg = "";
    @Schema(name = "code",description = "标识此次请求的状态 规定200为成功 -1 为失败")
    private Integer code;
    @Schema(name = "data",description = "返回携带的数据，没有数据则返回空")
    private T data;
    @Schema(name = "cache",description = "标识当前返回的数据是否的来自与缓存")
    private Boolean cache = false;

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public static <T> R<T> SUCCESS(T data){
        R<T> result = new R<>();
        result.setCode(RCode.SUCCESS);
        result.setData(data);
        return result;
    }
    public static <T> R<T> SUCCESS(T data,boolean isCache){
        R<T> result = new R<>();
        result.setCode(RCode.SUCCESS);
        result.setData(data);
        result.setCache(isCache);
        return result;
    }

    public static R<String> SUCCESS(){
        R<String> result = new R<>();
        result.setCode(RCode.SUCCESS);
        result.setData("ok");
        return result;
    }

    public static R<Object> FAIL(String errMsg){
        R<Object> result = new R<>();
        result.setCode(RCode.ERROR);
        result.setData(null);
        result.errMsg = errMsg;
        return  result;
    }

    public static R<Object> FAIL(String errMsg,Integer code){
        R<Object> result = new R<>();
        result.setErrMsg(errMsg);
        result.setCode(code);
        return result;
    }

    public T getData(){
        return data;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }
}
