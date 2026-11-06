package org.example.ljh.ciyuan.dto;

import lombok.Data;

/**
 * 改资料入参。
 * 只有这三个字段能被用户自己改——id 取自登录态，role/status 根本不在 DTO 里，
 * 前端就算传了 role:2 也落不到库里。
 */
@Data
public class UserUpdateDTO {

    private String nickname;

    private String avatar;

    private Boolean darkMode;
}
