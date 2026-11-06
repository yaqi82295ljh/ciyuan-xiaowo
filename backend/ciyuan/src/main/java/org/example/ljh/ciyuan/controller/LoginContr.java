package org.example.ljh.ciyuan.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.example.ljh.ciyuan.common.Result;
import org.example.ljh.ciyuan.dto.LoginDTO;
import org.example.ljh.ciyuan.service.UserService;
import org.example.ljh.ciyuan.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 登录接口 */

@RestController
@RequestMapping("/login")
public class LoginContr {

    private final UserService userService;

    @Autowired
    public LoginContr(UserService userService) {
        this.userService = userService;
    }

    /** 登录（前端表单提交，直接绑到 LoginDTO 上） */
    @PostMapping
    public Result<?> login(LoginDTO dto) {

        if (dto.getUsername() == null || dto.getUsername().isBlank()
                || dto.getPassword() == null || dto.getPassword().isBlank()) {
            return Result.error("用户名或密码错误");
        }

        //查人 + 对密码在 service 里
        UserVO user = userService.login(dto.getUsername(), dto.getPassword());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        //记下登录态
        StpUtil.login(user.getId());

        return Result.success(user);
    }
}
