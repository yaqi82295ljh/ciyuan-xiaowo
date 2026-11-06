package org.example.ljh.ciyuan.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.example.ljh.ciyuan.common.Result;
import org.example.ljh.ciyuan.dto.UserUpdateDTO;
import org.example.ljh.ciyuan.service.ImageService;
import org.example.ljh.ciyuan.service.UserService;
import org.example.ljh.ciyuan.vo.UserVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class MeContr {

    private final UserService userService;
    private final ImageService imageService;

    public MeContr(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    //当前用户
    @GetMapping
    public Result<?> me() {
        UserVO user = userService.getByIdSafe(StpUtil.getLoginIdAsLong());

        if (user == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(user);
    }

    /** 退出登录 */
    @DeleteMapping
    public Result<?> logout() {
        StpUtil.logout();  //让服务器端这份登录态作废
        return Result.success();
    }

    /** 修改当前用户的资料（DTO 只有昵称/头像/暗色开关，改不了 role 之类） */
    @PostMapping
    public Result<?> updateUser(@RequestBody UserUpdateDTO dto) {
        UserVO user = userService.updateProfile(
                StpUtil.getLoginIdAsLong(),
                dto.getNickname(),
                dto.getAvatar(),
                dto.getDarkMode());

        return Result.success(user);
    }

    //我的上传
    @GetMapping("/myImg")
    public Result<?> myImg() {
        return Result.success(imageService.listMy(StpUtil.getLoginIdAsLong()));
    }

}
