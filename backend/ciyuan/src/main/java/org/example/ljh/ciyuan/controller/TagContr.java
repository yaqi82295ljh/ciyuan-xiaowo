package org.example.ljh.ciyuan.controller;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import org.example.ljh.ciyuan.common.Result;
import org.example.ljh.ciyuan.entity.Tag;
import org.example.ljh.ciyuan.service.ImageService;
import org.example.ljh.ciyuan.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/** 标签接口 */
@RestController
@RequestMapping("/tag")
public class TagContr {

    @Resource
    private TagService tagService;
    private ImageService imageService;

    public  TagContr(TagService tagService, ImageService imageService) {
        this.tagService = tagService;
        this.imageService = imageService;
    }

    /** 标签列表 */
    @GetMapping
    public Result<?> tagList() {
        List <Tag> tags = tagService.lambdaQuery()
                .orderByDesc(Tag::getUsageCount)
                .orderByAsc(Tag::getId)
                .list();
        return Result.success(tags);
    }

}
