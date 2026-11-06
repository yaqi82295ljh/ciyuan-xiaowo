package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`categories`")
public class Category {

    private Long id;

    private String name;

    private String slug;

    private Integer sort;

    private Boolean showAi;

    private String icon;

    private String description;
}
