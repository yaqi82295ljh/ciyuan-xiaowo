package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`loli_applications`")
public class LoliApplication {

    private Long id;

    private Long userId;  //申请人

    private String reason;

    private Byte status;  //审核状态

    private Long reviewedBy;

    private LocalDateTime createdAt;
}
