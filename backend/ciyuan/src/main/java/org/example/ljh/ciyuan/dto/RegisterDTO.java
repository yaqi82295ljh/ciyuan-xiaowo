package org.example.ljh.ciyuan.dto;

import lombok.Data;

/** 注册入参 */
@Data
public class RegisterDTO {

    private String username;

    private String password;

    /** 可选，不传就用用户名 */
    private String nickname;
}
