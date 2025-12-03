package org.dromara.common.oss.util;

import cn.hutool.http.ContentType;
import io.minio.*;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import org.dromara.common.oss.config.MinioConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于 MinIO 官方 SDK 的工具类
 */
@Component
public class MinioHelper {

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioConfig.MinioProperties minioProperties;

    // ================== bucket 相关 ==================

    /**
     * 判断配置中的 bucket 是否存在
     */
    public boolean bucketExists() throws Exception {
        String bucket = minioProperties.getBucket();
        return minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(bucket)
                .build()
        );
    }

    /**
     * 如果 bucket 不存在则创建
     */
    public void createBucketIfNotExists() throws Exception {
        String bucket = minioProperties.getBucket();
        boolean exists = bucketExists();
        if (!exists) {
            minioClient.makeBucket(
                MakeBucketArgs.builder()
                    .bucket(bucket)
                    .build()
            );
        }
    }

    // ================== 对象相关操作 ==================

    /**
     * 列出 bucket 下所有对象
     */
    public List<Item> listAll() throws Exception {
        createBucketIfNotExists();

        String bucket = minioProperties.getBucket();
        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucket)
                .recursive(true)
                .build()
        );
        List<Item> list = new ArrayList<>();
        for (Result<Item> r : results) {
            list.add(r.get());
        }
        return list;
    }

    /**
     * 按“目录”（前缀）列出对象，比如 dir = "user/avatar"
     */
    public List<Item> listByDir(String dir) throws Exception {
        createBucketIfNotExists();

        String bucket = minioProperties.getBucket();
        String prefix = normalizePath(dir);
        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(prefix + "/")
                .recursive(false)
                .build()
        );
        List<Item> list = new ArrayList<>();
        for (Result<Item> r : results) {
            list.add(r.get());
        }
        return list;
    }

    /**
     * 返回 MinIO 文件访问前缀
     * 例如：http://192.168.174.129:19000/mall4j/
     */
    public String getUrlPrefix() {
        String prefix = minioProperties.getUrlPrefix();
        if (prefix != null && !prefix.isBlank()) {
            // 确保以 / 结尾
            return prefix.endsWith("/") ? prefix : prefix + "/";
        }

        // 如果没有配置 urlPrefix，则自动拼接
        String endpoint = minioProperties.getUrl();
        String bucket = minioProperties.getBucket();

        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        return base + "/" + bucket + "/";
    }

    /**
     * 获取完整地址（包含 bucket）
     */
    public String getFullUrl(String objectPath) {
        String prefix = getUrlPrefix();
        String normalized = normalizePath(objectPath);
        return prefix + normalized;
    }

    /**
     * 上传 MultipartFile 到指定目录，返回对象路径（不含 bucket）
     * 例如：dir = "user/avatar"，最后路径类似 "user/avatar/xxx.png"
     */
    public String upload(MultipartFile file, String dir) throws Exception {
        createBucketIfNotExists();

        String originalName = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" +
            (originalName == null ? "file" : originalName);

        String objectPath = buildObjectPath(dir, fileName);

        String contentType;
        // 强制保证 Content-Type 正确
        if (objectPath.endsWith(".jpg") || objectPath.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (objectPath.endsWith(".png")) {
            contentType = "image/png";
        } else if (objectPath.endsWith(".gif")) {
            contentType = "image/gif";
        } else {
            contentType = "application/octet-stream";
        }

        try (InputStream in = file.getInputStream()) {
            upload(in, objectPath, contentType);
        }

        return objectPath;
    }

    /**
     * 用 InputStream 上传，传入你想要的完整对象路径
     */
    public void upload(InputStream inputStream, String objectPath, String contentType)
        throws Exception {

        String bucket = minioProperties.getBucket();
        String objectName = normalizePath(objectPath);

        // 强制保证 Content-Type 正确
        if (contentType == null || contentType.isBlank()) {
            if (objectName.endsWith(".jpg") || objectName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (objectName.endsWith(".png")) {
                contentType = "image/png";
            } else if (objectName.endsWith(".gif")) {
                contentType = "image/gif";
            } else {
                contentType = "application/octet-stream";
            }
        }

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .contentType(contentType)
                .stream(inputStream, -1, 10 * 1024 * 1024)
                .build()
        );
    }


    /**
     * 获取文件的 InputStream（用于下载、转成 Resource 等）
     */
    public InputStream download(String objectPath) throws Exception {
        createBucketIfNotExists();

        String bucket = minioProperties.getBucket();
        String objectName = normalizePath(objectPath);

        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build()
        );
    }

    /**
     * 直接把 MinIO 的文件保存成本地文件
     */
    public void downloadToFile(String objectPath, String localFileName) throws Exception {
        createBucketIfNotExists();

        String bucket = minioProperties.getBucket();
        String objectName = normalizePath(objectPath);

        minioClient.downloadObject(
            DownloadObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .filename(localFileName)
                .build()
        );
    }

    /**
     * 删除对象
     */
    public void delete(String objectPath) throws Exception {
        createBucketIfNotExists();

        String bucket = minioProperties.getBucket();
        String objectName = normalizePath(objectPath);

        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build()
        );
    }

    /**
     * 获取对象的完整 URL（前提是 MinIO 该 bucket/对象可通过 HTTP 访问）
     * 例如：http://localhost:9000/test/user/avatar/xxx.png
     */
    public String getObjectUrl(String objectPath) {
        String endpoint = minioProperties.getUrl();    // 例如 http://localhost:9000
        String bucket = minioProperties.getBucket();   // 例如 test

        String normalized = normalizePath(objectPath);

        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        return base + "/" + bucket + "/" + normalized;
    }

    // ----------------------------------------
    // 一些辅助方法
    // ----------------------------------------

    /**
     * 拼接 dir 和 fileName，自动处理 / 问题
     */
    private String buildObjectPath(String dir, String fileName) {
        String d = normalizePath(dir);
        if (d.isEmpty()) {
            return fileName;
        }
        return d + "/" + fileName;
    }

    /**
     * 把路径中的反斜杠替换成 /，去掉首尾多余的 /
     */
    private String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        String p = path.replace("\\", "/");
        // 去掉开头和结尾的 /
        while (p.startsWith("/")) {
            p = p.substring(1);
        }
        while (p.endsWith("/")) {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }
}
