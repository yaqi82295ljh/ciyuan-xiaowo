package org.example.ljh.ciyuan.service;

import com.baomidou.mybatisplus.spring.service.IService;
import org.example.ljh.ciyuan.entity.Comment;
import org.example.ljh.ciyuan.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /** 某张图下的可见评论（带作者昵称/头像），按时间倒序 */
    List<CommentVO> listByImage(Long imageId);

    /**
     * 发一条评论并给图片评论数 +1。
     *
     * @return 新评论；图片不存在时返回 null
     */
    CommentVO addToImage(Long userId, Long imageId, String content);
}
