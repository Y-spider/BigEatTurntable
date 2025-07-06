package top.chopper.Exception;

/*
   @Author:ROBOT
   @DateTime:2024/4/17 22:28
   @Version:1.0.0
   @Description:
   */
public class BusinessException extends RuntimeException{
    public BusinessException() {
    }
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
