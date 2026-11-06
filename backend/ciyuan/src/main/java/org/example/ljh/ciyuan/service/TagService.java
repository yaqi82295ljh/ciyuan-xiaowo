package org.example.ljh.ciyuan.service;

import com.baomidou.mybatisplus.spring.service.IService;
import org.example.ljh.ciyuan.entity.Tag;

import java.util.List;
import java.util.Map;

public interface TagService extends IService<Tag> {

    List<Map<String, Object>> listImageTags();

    /** 把一串标签绑到某张图片上 */
    void bindTags(Long imageId, Long categoryId, String raw);
}
