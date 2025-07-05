package top.chopper.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/*
   @Author:ROBOT
   @DateTime:2024/4/11 20:51
   @Version:1.0.0
   @Description: 有关jwt令牌的相关操作
   */
public class JWTUtil {
    private static HashMap<String,Object> map = new HashMap<>();
    /**
     * @param ClaimKey 存储信息key
     * @param ClaimValue 存储信息value
     * @param secretKet 设置的签名
     * @param liveTime 生成token存存活的时间 单位为分钟
     * @return 返回生成的token字符串
     */
    public static String creatJWT(String ClaimKey,String ClaimValue,String secretKet,int liveTime){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE,liveTime);  // 在当前时间加上指定的时间
        JWTCreator.Builder builder = JWT.create();
        builder.withHeader(map); // 可以不设置
        builder.withClaim(ClaimKey,ClaimValue); // 存储信息，也就是加载在payload中的数据，可以设置多个
        builder.withExpiresAt(instance.getTime()); // 指定令牌的过期时间
        return builder.sign(Algorithm.HMAC256(secretKet)); // 设置加密密钥，并且生成token字符串
    }

    public static String createJWT(String ClaimKey,String ClaimValue,String secretKey){
//        Calendar instance = Calendar.getInstance();
//        instance.add(Calendar.DAY_OF_WEEK,7); // 默认设置过期时间为7天，取消注释，不设置过期时间
        JWTCreator.Builder builder = JWT.create();
//        builder.withExpiresAt(instance.getTime());
        builder.withClaim(ClaimKey,ClaimValue); // 存储信息，也就是加载在payload中的数据，可以设置多个
        return builder.sign(Algorithm.HMAC256(secretKey)); // 设置加密密钥，并且生成token字符串
    }

    /**
     * @param token token字符串
     * @param secretKet 密钥
     * @param  ClaimKey 数据键值对的key
     * @return 返回token中的数据返  如果id=ExpiresAt,返回过期时间
     */
    public static String getDecodeJWTData(String token, String secretKet,String ClaimKey){
        // 通过签名（密钥）生成验证对象
        JWTVerifier build = JWT.require(Algorithm.HMAC256(secretKet)).build();
        try {
            DecodedJWT verify = build.verify(token);
            if("ExpiresAt".equals(ClaimKey)){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(verify.getExpiresAt()!=null){
                    return simpleDateFormat.format(verify.getExpiresAt());
                }else{
                    return "Not Set Expire Time";
                }
            }
            return verify.getClaim(ClaimKey).asString();
        }catch ( SignatureVerificationException e){
//            TempExceptionMsg.exMsg = "密钥错误";
            throw new RuntimeException("密钥错误");
        }catch ( TokenExpiredException e ){
//            TempExceptionMsg.exMsg = "令牌已经过期";
            throw new RuntimeException("令牌已经过期");
        }

    }


}