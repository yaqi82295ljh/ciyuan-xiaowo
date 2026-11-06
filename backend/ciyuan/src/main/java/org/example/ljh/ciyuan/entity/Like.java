package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`likes`")
public class Like {

    private Long userId;

    private Long imageId;

    private LocalDateTime createdAt;
}
