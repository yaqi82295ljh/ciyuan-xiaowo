package org.example.ljh.ciyuan.controller;

import org.example.ljh.ciyuan.common.Result;
import org.example.ljh.ciyuan.dto.RegisterDTO;
import org.example.ljh.ciyuan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegisterContr {

    private final UserService userService;

    @Autowired
    public RegisterContr(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Result<?> register(RegisterDTO dto) {

        String username = dto.getUsername();
        String password = dto.getPassword();

        //格式校验留在 controller，因为要给用户具体的提示文案
        if (username == null || !username.matches("^[A-Za-z0-9]{2,32}$")) {
            return Result.error("用户名只能用英文字母和数字，2~32 位");
        }

        if (password == null || password.length() < 6) {
            return Result.error("密码至少 6 位");
        }

        String nick = (dto.getNickname() == null) ? "" : dto.getNickname().trim();

        if (nick.length() > 50) {
            return Result.error("昵称最多 50 个字");
        }
        if (nick.isEmpty()) {
            nick = username;
        }

        //查重、加密、入库在 service 里
        if (userService.usernameExists(username)) {
            return Result.error("这个用户名已经有人用了");
        }

        userService.create(username, password, nick);

        return Result.success();
    }

}
