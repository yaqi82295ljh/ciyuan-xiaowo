package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`comment_likes`")
public class CommentLike {

    private Long userId;

    private Long commentId;

}
