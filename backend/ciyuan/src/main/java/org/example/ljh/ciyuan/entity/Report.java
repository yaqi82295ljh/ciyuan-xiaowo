package org.example.ljh.ciyuan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`reports`")
public class Report {

    private Long id;

    private Byte type;  //举报目标类型

    private Long targetId;  //被举报对象的 id

    private Long reporterId;

    private String reason;

    private String detail;

    private Byte status;  //审核状态

    private LocalDateTime createdAt;
}
