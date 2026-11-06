package org.example.ljh.ciyuan.service;

import com.baomidou.mybatisplus.spring.service.IService;
import org.example.ljh.ciyuan.entity.Like;
import org.example.ljh.ciyuan.vo.LikeVO;

public interface LikeService extends IService<Like> {

    /**
     * 点赞开关：已赞则取消，未赞则点赞，同时维护图片的 like_count。
     *
     * @return 切换结果；图片不存在时返回 null
     */
    LikeVO toggle(Long userId, Long imageId);

    /** 这个用户赞过这张图没有 */
    boolean isLiked(Long userId, Long imageId);
}
