package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`favorite_items`")
public class FavoriteItem {

    private Long collectionId;

    private Long imageId;

    private LocalDateTime createdAt;
}
