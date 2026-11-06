package org.example.ljh.ciyuan.vo;

import lombok.Data;

/** 点赞开关结果 */
@Data
public class LikeVO {

    private boolean liked;

    private Integer likeCount;

    public static LikeVO of(boolean liked, Integer likeCount) {
        LikeVO vo = new LikeVO();
        vo.setLiked(liked);
        vo.setLikeCount(likeCount);
        return vo;
    }
}
