package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`blocks`")
public class Block {

    private Long userId;

    private Long blockedUserId;

    private LocalDateTime createdAt;
}
