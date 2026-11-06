package org.example.ljh.ciyuan.service;

import com.baomidou.mybatisplus.spring.service.IService;
import org.example.ljh.ciyuan.entity.FavoriteItem;
import org.example.ljh.ciyuan.vo.FavoriteVO;

public interface FavoriteItemService extends IService<FavoriteItem> {

    /** 收藏开关，返回切换后的状态：true=已收藏 */
    boolean toggle(Long userId, Long imageId);

    /**
     * 收藏开关 + 返回最新收藏数。
     *
     * @return 切换结果；图片不存在时返回 null
     */
    FavoriteVO toggleAndCount(Long userId, Long imageId);

    /** 这个用户收藏过这张图没有 */
    boolean isFaved(Long userId, Long imageId);
}
