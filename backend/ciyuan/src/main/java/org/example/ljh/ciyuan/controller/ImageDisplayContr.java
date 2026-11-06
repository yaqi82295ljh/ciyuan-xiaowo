package org.example.ljh.ciyuan.controller;

import org.example.ljh.ciyuan.common.Result;
import org.example.ljh.ciyuan.dto.ImageQueryDTO;
import org.example.ljh.ciyuan.service.ImageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 图片列表接口 */
@RestController
@RequestMapping("/img")
public class ImageDisplayContr {

    private final ImageService imageService;

    public ImageDisplayContr(ImageService imageService) {
        this.imageService = imageService;
    }

    /** 查图片列表（查询参数直接绑到 ImageQueryDTO，默认值在 DTO 里） */
    @GetMapping
    public Result<?> getImage(ImageQueryDTO query) {
        return Result.success(imageService.listImages(query));
    }
}
