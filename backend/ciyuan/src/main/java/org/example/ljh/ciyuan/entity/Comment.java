package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** comments 表 */
@Data
@TableName("`comments`")
public class Comment {

    private Long id;

    private Long imageId;  //属于哪张图

    private Long userId;  //谁发的

    private Long parentId;

    private String content;

    private Integer likeCount;

    private Byte status;

    private LocalDateTime createdAt;
}
