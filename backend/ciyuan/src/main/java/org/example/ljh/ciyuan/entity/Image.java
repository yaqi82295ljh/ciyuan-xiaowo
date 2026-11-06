package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/** images 表 */
@Data

@TableName("`images`")
public class Image {

    private Integer width;

    private Integer height;

    private Long id;

    private Long userId;

    private Long categoryId;

    private String title;

    private String url;

    private String thumbUrl;

    private String description;

    private Boolean isAi;

    private Integer likeCount;

    private Integer favCount;

    private Integer commentCount;

    private Byte status;

    private LocalDateTime createdAt;

    private Integer views;

    @TableField(exist = false)
    private List<String> tags;
}
