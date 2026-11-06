package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`tags`")
public class Tag {

    private Long id;

    private String name;

    private Long categoryId;

    private Integer usageCount;
}
