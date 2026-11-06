package org.example.ljh.ciyuan.vo;

import lombok.Data;
import org.example.ljh.ciyuan.entity.User;

import java.time.LocalDateTime;

/** 返回给前端的用户信息：没有 password 字段，从结构上保证密码不会漏出去 */
@Data
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private Byte role;

    private Byte status;

    private Boolean darkMode;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static UserVO from(User u) {
        if (u == null) return null;

        UserVO vo = new UserVO();
        vo.setId(u.getId());
        vo.setUsername(u.getUsername());
        vo.setNickname(u.getNickname());
        vo.setAvatar(u.getAvatar());
        vo.setRole(u.getRole());
        vo.setStatus(u.getStatus());
        vo.setDarkMode(u.getDarkMode());
        vo.setCreatedAt(u.getCreatedAt());
        vo.setUpdatedAt(u.getUpdatedAt());
        return vo;
    }
}
