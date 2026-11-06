package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`messages`")
public class Message {

    private Long id;

    private Long senderId;

    private Long receiverId;

    private String content;

    private Boolean isRead;  //是否已读

    private LocalDateTime createdAt;
}
