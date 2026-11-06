package org.example.ljh.ciyuan.vo;

import lombok.Data;
import org.example.ljh.ciyuan.entity.Comment;
import org.example.ljh.ciyuan.entity.User;

import java.time.LocalDateTime;

/** 评论 VO：评论本体 + 作者昵称/头像 */
@Data
public class CommentVO {

    private Long id;
    private Long userId;

    private String nickname;  //来自 users 表
    private String avatar;  //来自 users 表

    private String content;
    private LocalDateTime createdAt;

    public static CommentVO from(Comment c, User u) {
        CommentVO vo = new CommentVO();
        vo.setId(c.getId());
        vo.setUserId(c.getUserId());
        vo.setContent(c.getContent());
        vo.setCreatedAt(c.getCreatedAt());
        if (u != null) {
            vo.setNickname(u.getNickname());
            vo.setAvatar(u.getAvatar());
        }
        return vo;
    }
}
