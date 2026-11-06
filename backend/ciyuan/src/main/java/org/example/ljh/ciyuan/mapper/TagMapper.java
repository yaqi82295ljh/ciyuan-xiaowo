package org.example.ljh.ciyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.ljh.ciyuan.entity.Tag;

import java.util.List;
import java.util.Map;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    @Select("select it.image_id as imageId, t.name as name " +
            "from image_tags it " +
            "join tags t on t.id = it.tag_id " +
            "order by it.image_id, t.usage_count desc, t.id")
    List<Map<String, Object>> selectImageTags();


    /** 按名字查标签 id */
    @Select("select id from tags where name = #{name} limit 1")
    Long selectIdByName(@Param("name") String name);

    @Insert("insert ignore into image_tags (image_id, tag_id) values (#{imageId}, #{tagId})")
    int bindImageTag(@Param("imageId") Long imageId, @Param("tagId") Long tagId);

    @Update("update tags set usage_count = usage_count + 1 where id = #{tagId}")
    int incUsageCount(@Param("tagId") Long tagId);
}
