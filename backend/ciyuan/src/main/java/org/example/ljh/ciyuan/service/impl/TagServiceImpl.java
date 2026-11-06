package org.example.ljh.ciyuan.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.example.ljh.ciyuan.entity.Tag;
import org.example.ljh.ciyuan.mapper.TagMapper;
import org.example.ljh.ciyuan.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper,Tag> implements TagService {

    @Override
    public List<Map<String, Object>> listImageTags() {
        return baseMapper.selectImageTags();
    }

    /** 把一串标签绑到某张图片上 */
    @Override
    @Transactional
    public void bindTags(Long imageId, Long categoryId, String raw) {

        if (raw == null || raw.trim().isEmpty()) return;

        //拆标签
        for (String part : raw.split("[,，、\\s]+")) {
            String name = part.trim();
            if (name.isEmpty()) continue;

            Long tagId = baseMapper.selectIdByName(name);

            if (tagId == null) {
                Tag tag = new Tag();
                tag.setName(name);
                tag.setCategoryId(categoryId);
                tag.setUsageCount(0);
                save(tag);
                tagId = tag.getId();
            }

            //建关联
            int rows = baseMapper.bindImageTag(imageId, tagId);

            if (rows > 0) baseMapper.incUsageCount(tagId);
        }
    }
}
