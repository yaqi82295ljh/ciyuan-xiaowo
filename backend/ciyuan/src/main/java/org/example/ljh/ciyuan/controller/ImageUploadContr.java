package org.example.ljh.ciyuan.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.example.ljh.ciyuan.common.Result;
import org.example.ljh.ciyuan.entity.Image;
import org.example.ljh.ciyuan.service.ImageService;
import org.example.ljh.ciyuan.service.TagService;
import org.example.ljh.ciyuan.utils.ThumbUtils;
import org.example.ljh.ciyuan.utils.UploadUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/** 上传作品接口 */
@RestController
@RequestMapping("/imgUpload")
public class ImageUploadContr {

    private final ImageService imageService;
    private final TagService tagService;

    public ImageUploadContr(ImageService imageService, TagService tagService) {
        this.imageService = imageService;
        this.tagService = tagService;
    }

    /** 上传一张作品 */
    @PostMapping
    public Result<?> addImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "wall") String cat,
            @RequestParam(defaultValue = "false") boolean ai,
            @RequestParam(defaultValue = "0") int width,
            @RequestParam(defaultValue = "0") int height,
            @RequestParam(defaultValue = "") String tags) throws IOException {

        //校验有没有文件
        if (file == null || file.isEmpty()) {
            return Result.error("没有上传文件");
        }

        //落盘
        String name = UploadUtils.save(file);

        //缩略图失败不拦着发布，thumbUrl 允许为空
        String thumbName = ThumbUtils.make(name);
        String thumbUrl = thumbName == null ? null : UploadUtils.urlOf(thumbName);

        Image image = new Image();
        image.setUserId(StpUtil.getLoginIdAsLong());
        image.setCategoryId("char".equals(cat) ? 2L : 1L);
        image.setTitle(title.trim());
        image.setUrl(UploadUtils.urlOf(name));
        image.setIsAi(ai);
        image.setWidth(width);
        image.setHeight(height);
        image.setThumbUrl(thumbUrl);

        imageService.save(image);

        //写标签
        try {
            tagService.bindTags(image.getId(), image.getCategoryId(), tags);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.success(image.getUrl());
    }
}
