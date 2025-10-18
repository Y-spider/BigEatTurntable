package top.chopper.utils;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.chopper.Exception.BusinessException;
import top.chopper.pojo.MinioProp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

/*
   @Author:ROBOT
   @DateTime:2025/7/20 12:20
   @Version:1.0.0
   @Description: minio 操作工具类
   */
@Component
@Slf4j
public class MinioUtil {
    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProp minioProp;
    /**
     * @param file 上传文件
     * @return  {fileName:"",contentType:"",url:"",timestamp:""}
     */
    public HashMap<String,String> uploadFile(MultipartFile file){
        HashMap<String, String> data = new HashMap<>();
        String fileName = file.getOriginalFilename();
        data.put("fileName",fileName);
        String contentType = file.getContentType(); // 获取文件类型
        data.put("contentType",contentType);
        InputStream in = null;
        try {
            in = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .contentType(contentType)
                    .stream(in,in.available(),-1)
                    .object(fileName)
                    .bucket(minioProp.getBucketName())
                    .build());
        } catch ( RuntimeException | IOException | ErrorResponseException | InsufficientDataException |
                  InternalException | InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
                  ServerException | XmlParserException e ) {
            log.error("上传文件==》"+fileName+"<====发送错误" + e);
            throw new BusinessException("文件操作失败" + e);
        }
        // 访问url
        String fileUrl = "https://www.sunnygo.chat/images" + "/" + minioProp.getBucketName() + "/" + fileName;
        data.put("url",fileUrl);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return data;
    }


    /** 网络地址上传
     * @param url 文件url
     * @return 返回文件访问地址
     */
    public HashMap<String,String> uploadFile(String url){
        HashMap<String,String> data = new HashMap<>();
        try {
            // 避免被禁止掉ip
            Thread.sleep(30);
            byte[] contentBytes =HttpUtil.downloadBytes(url);
            String contentType = getFileType(url);
            String fileName = UUID.randomUUID() + "." + contentType;
            InputStream in = new ByteArrayInputStream(contentBytes);
            minioClient.putObject(PutObjectArgs.builder()
                    .contentType("image/jpeg")
                    .stream(in,contentBytes.length,-1)
                    .object(fileName)
                    .bucket(minioProp.getBucketName())
                    .build());
            String fileUrl = "https://www.sunnygo.chat/images" + "/" + minioProp.getBucketName() + "/" + fileName;
            data.put("url",fileUrl);
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));
            data.put("fileName",fileName);
            data.put("contentType",contentType);
        } catch ( ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                  InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                  XmlParserException | InterruptedException |  HttpException e ) {
            log.error("上传文件==》"+url+"<====发送错误" + e);
            throw new BusinessException("网络文件保存失败==>"+url + "\n" + e);
        }
        return data;
    }
        /**
         * 获取文件类型
         * @param url 网络地址
         * @return 返回文件类型
         */
        private String getFileType(String url) {
            // 将 URL 转换为小写，以确保匹配时不受大小写影响
            String lowerUrl = url.toLowerCase();

            // 使用正则表达式匹配文件扩展名
            if (Pattern.compile("\\.jpg").matcher(lowerUrl).find()) {
                return "jpg";
            } else if (Pattern.compile("\\.png").matcher(lowerUrl).find()) {
                return "png";
            } else if (Pattern.compile("\\.gif").matcher(lowerUrl).find()) {
                return "gif";
            } else if (Pattern.compile("\\.bmp").matcher(lowerUrl).find()) {
                return "bmp";
            } else if (Pattern.compile("\\.webp").matcher(lowerUrl).find()) {
                return "webp";
            } else {
                return "file"; // 默认值
            }
        }
}
