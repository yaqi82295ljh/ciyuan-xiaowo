package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`users`")
public class User {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private Byte role;

    private Byte status;

    private Boolean darkMode;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
