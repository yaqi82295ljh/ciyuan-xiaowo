package org.example.ljh.ciyuan.vo;

import lombok.Data;
import org.example.ljh.ciyuan.entity.Image;

import java.time.LocalDateTime;
import java.util.List;

/** 返回给前端的图片信息（字段与前端用到的 JSON 键一一对应） */
@Data
public class ImageVO {

    private Long id;

    private Long userId;

    private Long categoryId;

    private String title;

    private String url;

    private String thumbUrl;

    private String description;

    private Boolean isAi;

    private Integer likeCount;

    private Integer favCount;

    private Integer commentCount;

    private Byte status;

    private Integer views;

    private Integer width;

    private Integer height;

    private LocalDateTime createdAt;

    /** 标签不在 images 表里，是查出来拼上的 */
    private List<String> tags;

    public static ImageVO from(Image img) {
        if (img == null) return null;

        ImageVO vo = new ImageVO();
        vo.setId(img.getId());
        vo.setUserId(img.getUserId());
        vo.setCategoryId(img.getCategoryId());
        vo.setTitle(img.getTitle());
        vo.setUrl(img.getUrl());
        vo.setThumbUrl(img.getThumbUrl());
        vo.setDescription(img.getDescription());
        vo.setIsAi(img.getIsAi());
        vo.setLikeCount(img.getLikeCount());
        vo.setFavCount(img.getFavCount());
        vo.setCommentCount(img.getCommentCount());
        vo.setStatus(img.getStatus());
        vo.setViews(img.getViews());
        vo.setWidth(img.getWidth());
        vo.setHeight(img.getHeight());
        vo.setCreatedAt(img.getCreatedAt());
        vo.setTags(img.getTags() == null ? List.of() : img.getTags());
        return vo;
    }
}
