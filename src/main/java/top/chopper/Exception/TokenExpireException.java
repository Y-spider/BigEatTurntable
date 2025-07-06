package top.chopper.Exception;

/*
   @Author:ROBOT
   @DateTime:2024/9/1 15:27
   @Version:1.0.0
   @Description: 自定义token失效异常 TokenExpireException
   */
public class TokenExpireException extends RuntimeException{
    public TokenExpireException() {
    }
    public TokenExpireException(String message) {
        super(message);
    }

    public  TokenExpireException(String message, Throwable cause) {
        super(message, cause);
    }
}
