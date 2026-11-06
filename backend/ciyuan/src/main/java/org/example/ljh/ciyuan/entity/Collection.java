package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`collections`")
public class Collection {

    private Long id;

    private Long userId;  //收藏夹属于谁

    private String name;

    private LocalDateTime createdAt;
}
