package org.example.ljh.ciyuan.utils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 上传相关的小工具：目录、扩展名、存盘、拼访问地址。
 * 原来这些散在 UploadContr 里，ImageUploadContr 还得 import static 它的成员。
 */
public final class UploadUtils {

    /** 上传根目录：优先 -Dapp.upload-dir，其次环境变量 APP_UPLOAD_DIR，最后本地默认值 */
    public static final File DIR = new File(
            System.getProperty("app.upload-dir",
                    System.getenv().getOrDefault("APP_UPLOAD_DIR", "D:/comhabby/uploads")));

    private UploadUtils() {
    }

    /** 确保上传目录存在 */
    public static void ensureDir() throws IOException {
        if (!DIR.exists() && !DIR.mkdirs()) {
            throw new IOException("上传目录创建失败：" + DIR.getAbsolutePath());
        }
    }

    /** 取扩展名（含点，如 ".png"），没有后缀返回空串 */
    public static String extOf(String originalFilename) {
        if (originalFilename == null) return "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot < 0) return "";
        return originalFilename.substring(dot);
    }

    /** 随机文件名 + 原扩展名 */
    public static String newName(String originalFilename) {
        return UUID.randomUUID().toString().replace("-", "") + extOf(originalFilename);
    }

    /** 存盘，返回生成的文件名 */
    public static String save(MultipartFile file) throws IOException {
        ensureDir();
        String name = newName(file.getOriginalFilename());
        file.transferTo(new File(DIR.getAbsoluteFile(), name));
        return name;
    }

    /**
     * 文件名 → 可访问网址。
     * 不能写死 localhost:9090：线上入库就成了 http://localhost:9090/...，
     * 用户浏览器会去连自己电脑，必然裂图。按当前请求反推真实地址。
     */
    public static String urlOf(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/").path(fileName).toUriString();
    }
}
