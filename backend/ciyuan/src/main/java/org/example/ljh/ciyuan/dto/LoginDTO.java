package org.example.ljh.ciyuan.dto;

import lombok.Data;

/** 登录入参（前端表单提交：username=xx&password=xx） */
@Data
public class LoginDTO {

    private String username;

    private String password;
}
