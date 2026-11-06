package org.example.ljh.ciyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.example.ljh.ciyuan.entity.Image;
import org.example.ljh.ciyuan.entity.Like;
import org.example.ljh.ciyuan.mapper.LikeMapper;
import org.example.ljh.ciyuan.service.ImageService;
import org.example.ljh.ciyuan.service.LikeService;
import org.example.ljh.ciyuan.vo.LikeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeServiceImp extends ServiceImpl<LikeMapper, Like> implements LikeService {

    private ImageService imageService;

    @Autowired
    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    @Transactional
    public LikeVO toggle(Long userId, Long imageId) {
        if (imageService.getById(imageId) == null) {
            return null;
        }

        Like exist = lambdaQuery()
                .eq(Like::getUserId, userId)
                .eq(Like::getImageId, imageId)
                .one();

        boolean liked;
        if (exist != null) {
            remove(new LambdaQueryWrapper<Like>()
                    .eq(Like::getUserId, userId)
                    .eq(Like::getImageId, imageId));
            liked = false;
        } else {
            Like like = new Like();
            like.setUserId(userId);
            like.setImageId(imageId);
            save(like);
            liked = true;
        }

        //点赞数跟着变，减到 0 就不再往下减
        imageService.lambdaUpdate()
                .eq(Image::getId, imageId)
                .setSql(liked ? "like_count = like_count + 1"
                        : "like_count = GREATEST(like_count - 1, 0)")
                .update();

        return LikeVO.of(liked, imageService.getById(imageId).getLikeCount());
    }

    @Override
    public boolean isLiked(Long userId, Long imageId) {
        return lambdaQuery()
                .eq(Like::getUserId, userId)
                .eq(Like::getImageId, imageId)
                .exists();
    }
}
