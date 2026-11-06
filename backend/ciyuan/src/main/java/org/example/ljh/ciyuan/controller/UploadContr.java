package org.example.ljh.ciyuan.controller;

import org.example.ljh.ciyuan.common.Result;
import org.example.ljh.ciyuan.utils.UploadUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/** 通用文件上传接口（当前用于头像） */
@RestController
public class UploadContr {

    /** 收一个文件 */
    @PostMapping("/upload")
    public Result<?> upload(@RequestParam(value = "file", required = false) MultipartFile file)
            throws IOException {

        //校验有没有文件
        if (file == null || file.isEmpty()) {
            return Result.error("没有上传文件");
        }

        String name = UploadUtils.save(file);

        return Result.success(UploadUtils.urlOf(name));
    }

}
