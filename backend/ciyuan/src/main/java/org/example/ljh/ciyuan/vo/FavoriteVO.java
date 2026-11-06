package org.example.ljh.ciyuan.vo;

import lombok.Data;

/** 收藏开关结果 */
@Data
public class FavoriteVO {

    private boolean faved;

    private Integer favCount;

    public static FavoriteVO of(boolean faved, Integer favCount) {
        FavoriteVO vo = new FavoriteVO();
        vo.setFaved(faved);
        vo.setFavCount(favCount);
        return vo;
    }
}
