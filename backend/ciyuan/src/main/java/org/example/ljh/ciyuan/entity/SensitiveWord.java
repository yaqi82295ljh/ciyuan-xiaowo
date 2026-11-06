package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`sensitive_words`")
public class SensitiveWord {

    private Long id;

    private String word;

}
