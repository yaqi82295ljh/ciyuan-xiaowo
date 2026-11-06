package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 插一行数据就行 */
@Data
@TableName("`site_settings`")
public class SiteSetting {

    private Long id;

    @TableField("`key`")
    private String key;

    private String value;
}
