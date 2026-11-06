package org.example.ljh.ciyuan.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.example.ljh.ciyuan.entity.User;
import org.example.ljh.ciyuan.mapper.UserMapper;
import org.example.ljh.ciyuan.service.UserService;
import org.example.ljh.ciyuan.vo.UserVO;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public UserVO login(String username, String password) {
        User user = lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        //用户不存在和密码错误都返回 null，由 controller 统一给一句"用户名或密码错误"
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            return null;
        }

        return UserVO.from(user);
    }

    @Override
    public boolean usernameExists(String username) {
        return lambdaQuery().eq(User::getUsername, username).exists();
    }

    @Override
    public UserVO create(String username, String rawPassword, String nickname) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
        user.setNickname(nickname);
        save(user);

        return getByIdSafe(user.getId());
    }

    @Override
    public UserVO getByIdSafe(Long userId) {
        return UserVO.from(getById(userId));
    }

    @Override
    public UserVO updateProfile(Long userId, String nickname, String avatar, Boolean darkMode) {
        User patch = new User();
        patch.setId(userId);
        if (nickname != null) {
            patch.setNickname(nickname.trim());
        }
        patch.setAvatar(avatar);
        patch.setDarkMode(darkMode);

        updateById(patch);

        return getByIdSafe(userId);
    }
}
