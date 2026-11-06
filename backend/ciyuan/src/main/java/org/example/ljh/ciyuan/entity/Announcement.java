package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`announcements`")
public class Announcement {

    private Long id;

    private String title;

    private String content;

    private Boolean enabled;  //是否启用

    private Boolean pinned;  //是否置顶

    private LocalDateTime createdAt;
}
