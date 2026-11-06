package org.example.ljh.ciyuan.service;

import com.baomidou.mybatisplus.spring.service.IService;
import org.example.ljh.ciyuan.entity.User;
import org.example.ljh.ciyuan.vo.UserVO;

public interface UserService extends IService<User> {

    /** 登录校验：成功返回用户信息，失败返回 null */
    UserVO login(String username, String password);

    /** 用户名是否已被占用 */
    boolean usernameExists(String username);

    /** 建号：密码加密后入库，返回新用户信息 */
    UserVO create(String username, String rawPassword, String nickname);

    /** 按 id 查用户信息（不含密码） */
    UserVO getByIdSafe(Long userId);

    /** 改资料：只改昵称/头像/暗色开关，返回最新资料 */
    UserVO updateProfile(Long userId, String nickname, String avatar, Boolean darkMode);
}
