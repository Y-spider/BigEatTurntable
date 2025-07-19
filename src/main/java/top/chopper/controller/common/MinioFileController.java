package top.chopper.controller.common;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.chopper.Exception.BusinessException;
import top.chopper.pojo.MinioProp;
import top.chopper.pojo.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/*
   @Author:ROBOT
   @DateTime:2025/7/19 22:06
   @Version:1.0.0
   @Description: minio 文件操作接口
   */
@RestController
@RequestMapping("/file")
@Slf4j
@Tag(description = "minio文件操作接口",name = "MinioFileController")
public class MinioFileController {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProp minioProp;

    @PostMapping("/upload")
    @Operation(description = "文件上传操作,返回文件访问url",summary = "文件上传操作,返回文件访问url")
    public R handleFileUpload(@RequestParam(name = "file", required = false) MultipartFile file){
        if(file==null){
            return R.FAIL("上传文件不能为空!");
        }
        else{
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
            String fileUrl = minioProp.getEndpoint() + "/" + minioProp.getBucketName() + "/" + fileName;
            data.put("url",fileUrl);
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return R.SUCCESS(data);
        }
    }
}
