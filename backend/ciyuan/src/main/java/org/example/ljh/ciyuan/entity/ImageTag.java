package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`image_tags`")
public class ImageTag {

    private Long imageId;

    private Long tagId;

}
