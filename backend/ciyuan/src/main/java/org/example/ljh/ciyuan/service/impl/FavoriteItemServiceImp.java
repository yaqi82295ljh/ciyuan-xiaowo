package org.example.ljh.ciyuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.example.ljh.ciyuan.entity.Collection;
import org.example.ljh.ciyuan.entity.FavoriteItem;
import org.example.ljh.ciyuan.entity.Image;
import org.example.ljh.ciyuan.mapper.FavoriteItemMapper;
import org.example.ljh.ciyuan.service.CollectionService;
import org.example.ljh.ciyuan.service.FavoriteItemService;
import org.example.ljh.ciyuan.service.ImageService;
import org.example.ljh.ciyuan.vo.FavoriteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteItemServiceImp extends ServiceImpl<FavoriteItemMapper,FavoriteItem> implements FavoriteItemService {
    private CollectionService collectionService;
    private ImageService imageService;

    @Autowired
    public void setFavoriteItemService(CollectionService collectionService, ImageService imageService) {
        this.imageService = imageService;
        this.collectionService = collectionService;
    }

    @Override
    @Transactional
    public boolean toggle(Long userId, Long imageId) {
        Collection collection = firstCollectionOf(userId);

        boolean exists = lambdaQuery()
                .eq(FavoriteItem::getCollectionId, collection.getId())
                .eq(FavoriteItem::getImageId,imageId)
                .exists();

        boolean faved ;
        if(exists){
            remove(new LambdaQueryWrapper<FavoriteItem>()
                    .eq(FavoriteItem::getCollectionId, collection.getId())
                    .eq(FavoriteItem::getImageId, imageId));
            faved = false;
        }else{
            FavoriteItem item = new FavoriteItem();
            item.setCollectionId(collection.getId());
            item.setImageId(imageId);
            save(item);
            faved = true;
        }

        imageService.lambdaUpdate()
                .eq(Image::getId, imageId)
                .setSql(faved ? "fav_count = fav_count + 1"
                        : "fav_count = GREATEST(fav_count - 1, 0)")
                .update();
        return faved;
    }

    @Override
    @Transactional
    public FavoriteVO toggleAndCount(Long userId, Long imageId) {
        if (imageService.getById(imageId) == null) {
            return null;
        }

        boolean faved = toggle(userId, imageId);

        return FavoriteVO.of(faved, imageService.getById(imageId).getFavCount());
    }

    @Override
    public boolean isFaved(Long userId, Long imageId) {
        List<Long> colIds = collectionService.lambdaQuery()
                .eq(Collection::getUserId, userId)
                .list()
                .stream()
                .map(Collection::getId)
                .toList();

        if (colIds.isEmpty()) {
            return false;
        }
        return lambdaQuery()
                .in(FavoriteItem::getCollectionId, colIds)
                .eq(FavoriteItem::getImageId, imageId)
                .exists();
    }

    /** 用户的第一个收藏夹，没有就建一个"默认收藏夹" */
    private Collection firstCollectionOf(Long userId) {
        Collection collection = collectionService.lambdaQuery()
                .eq(Collection::getUserId, userId)
                .orderByAsc(Collection::getId)
                .last("limit 1")
                .one();

        if (collection == null) {
            collection = new Collection();
            collection.setUserId(userId);
            collection.setName("默认收藏夹");
            collectionService.save(collection);
        }
        return collection;
    }
}
