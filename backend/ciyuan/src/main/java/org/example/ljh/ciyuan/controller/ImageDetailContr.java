package org.example.ljh.ciyuan.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.example.ljh.ciyuan.common.Result;
import org.example.ljh.ciyuan.dto.CommentDTO;
import org.example.ljh.ciyuan.entity.Image;
import org.example.ljh.ciyuan.service.CommentService;
import org.example.ljh.ciyuan.service.FavoriteItemService;
import org.example.ljh.ciyuan.service.ImageService;
import org.example.ljh.ciyuan.service.LikeService;
import org.example.ljh.ciyuan.service.UserService;
import org.example.ljh.ciyuan.vo.CommentVO;
import org.example.ljh.ciyuan.vo.FavoriteVO;
import org.example.ljh.ciyuan.vo.ImageDetailVO;
import org.example.ljh.ciyuan.vo.ImageVO;
import org.example.ljh.ciyuan.vo.LikeVO;
import org.example.ljh.ciyuan.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 图片详情与互动：评论、点赞、收藏 */
@RestController
@RequestMapping("/img")
public class ImageDetailContr {

    private final CommentService commentService;
    private final ImageService imageService;
    private final LikeService likeService;
    private final FavoriteItemService favoriteItemService;
    private final UserService userService;

    public ImageDetailContr(CommentService commentService,
                            ImageService imageService,
                            LikeService likeService,
                            FavoriteItemService favoriteItemService,
                            UserService userService) {
        this.commentService = commentService;
        this.imageService = imageService;
        this.likeService = likeService;
        this.favoriteItemService = favoriteItemService;
        this.userService = userService;
    }

    /** 查评论 */
    @GetMapping("/{id}/comments")
    public Result<List<CommentVO>> getComments(@PathVariable("id") Long imageId) {
        return Result.success(commentService.listByImage(imageId));
    }

    /** 发评论 */
    @PostMapping("/{id}/comments")
    public Result<CommentVO> addComment(@PathVariable("id") Long imageId,
                                        @RequestBody CommentDTO dto) {

        String content = dto.getContent();
        if (content == null || content.trim().isEmpty()) {
            return Result.error("评论不能为空");
        }
        content = content.trim();
        if (content.length() > 300) {
            return Result.error("字数不要超过300字");
        }

        CommentVO vo = commentService.addToImage(StpUtil.getLoginIdAsLong(), imageId, content);
        if (vo == null) {
            return notFound();
        }
        return Result.success(vo);
    }

    /** 点赞开关 */
    @PostMapping("/{id}/like")
    public Result<LikeVO> toggleLike(@PathVariable("id") Long imageId) {
        LikeVO vo = likeService.toggle(StpUtil.getLoginIdAsLong(), imageId);
        if (vo == null) {
            return notFound();
        }
        return Result.success(vo);
    }

    /** 收藏开关 */
    @PostMapping("/{id}/favorite")
    public Result<FavoriteVO> toggleFavorite(@PathVariable("id") Long imageId) {
        FavoriteVO vo = favoriteItemService.toggleAndCount(StpUtil.getLoginIdAsLong(), imageId);
        if (vo == null) {
            return notFound();
        }
        return Result.success(vo);
    }

    /** 查详情 */
    @GetMapping("/{id}")
    public Result<ImageDetailVO> getDetail(@PathVariable("id") Long imageId) {

        Image image = imageService.getById(imageId);
        if (image == null) {
            return notFound();
        }

        ImageDetailVO vo = new ImageDetailVO();
        vo.setImage(ImageVO.from(image));

        //作者信息里没有 password 字段
        UserVO author = userService.getByIdSafe(image.getUserId());
        vo.setAuthor(author);

        String authorName = "未知用户";
        if (author != null) {
            authorName = (author.getNickname() != null && !author.getNickname().isEmpty())
                    ? author.getNickname()
                    : author.getUsername();
        }
        vo.setAuthorName(authorName);

        //登录了才知道自己赞过/收藏过没有
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            vo.setLiked(likeService.isLiked(userId, imageId));
            vo.setFaved(favoriteItemService.isFaved(userId, imageId));
        }

        return Result.success(vo);
    }

    private static <T> Result<T> notFound() {
        return Result.error(404, "这张图片不存在");
    }
}
