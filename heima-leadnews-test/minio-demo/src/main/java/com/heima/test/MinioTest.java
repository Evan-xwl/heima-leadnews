package com.heima.test;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author ruoling
 * @date 2023/12/26 19:46:08
 * @description
 */
public class MinioTest {
    public static void main(String[] args) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("E:\\我爱学习\\java\\黑马\\day2\\资料\\模板文件\\plugins\\css/index.css");
            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123")
                    .endpoint("http:192.168.241.133:9000")
                    .build();
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("plugins/css/index.css")
                    .contentType("css")
                    .bucket("leadnews")
                    .stream(fileInputStream, fileInputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            System.out.println("上传成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
