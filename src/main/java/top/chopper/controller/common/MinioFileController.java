package top.chopper.controller.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.chopper.pojo.R;
import top.chopper.utils.MinioUtil;

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
    private MinioUtil minioUtil;

    @PostMapping("/upload")
    @Operation(description = "文件上传操作,返回文件访问url",summary = "文件上传操作,返回文件访问url")
    public R handleFileUpload(@RequestParam(name = "file", required = false) MultipartFile file){
        if(file==null){
            return R.FAIL("上传文件不能为空!");
        }
        else{
            return R.SUCCESS(minioUtil.uploadFile(file));
        }
    }
}
