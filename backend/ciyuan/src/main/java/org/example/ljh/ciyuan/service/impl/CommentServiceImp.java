package org.example.ljh.ciyuan.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.example.ljh.ciyuan.entity.Comment;
import org.example.ljh.ciyuan.entity.Image;
import org.example.ljh.ciyuan.entity.User;
import org.example.ljh.ciyuan.mapper.CommentMapper;
import org.example.ljh.ciyuan.service.CommentService;
import org.example.ljh.ciyuan.service.ImageService;
import org.example.ljh.ciyuan.service.UserService;
import org.example.ljh.ciyuan.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentServiceImp extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private ImageService imageService;
    private UserService userService;

    @Autowired
    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<CommentVO> listByImage(Long imageId) {
        List<Comment> list = lambdaQuery()
                .eq(Comment::getImageId, imageId)
                .eq(Comment::getStatus, 0)
                .orderByDesc(Comment::getCreatedAt)
                .orderByDesc(Comment::getId)
                .list();

        //一次把作者查出来，避免每条评论查一遍用户表
        Set<Long> userIds = list.stream().map(Comment::getUserId).collect(Collectors.toSet());

        Map<Long, User> userMap = userIds.isEmpty()
                ? Map.of()
                : userService.listByIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));

        return list.stream()
                .map(c -> CommentVO.from(c, userMap.get(c.getUserId())))
                .toList();
    }

    @Override
    @Transactional
    public CommentVO addToImage(Long userId, Long imageId, String content) {
        if (imageService.getById(imageId) == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setImageId(imageId);
        save(comment);

        //评论数跟着 +1
        imageService.lambdaUpdate()
                .eq(Image::getId, imageId)
                .setSql("comment_count = comment_count + 1")
                .update();

        return CommentVO.from(getById(comment.getId()), userService.getById(userId));
    }
}
