package top.chopper.Exception;

/*
   @Author:ROBOT
   @DateTime:2024/4/17 22:26
   @Version:1.0.0
   @Description:
   */
public class SystemException extends RuntimeException{
    public SystemException() {
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
