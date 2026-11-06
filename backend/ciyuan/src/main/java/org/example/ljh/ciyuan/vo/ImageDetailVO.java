package org.example.ljh.ciyuan.vo;

import lombok.Data;

/** 图片详情：图片 + 作者 + 当前用户是否赞/藏 */
@Data
public class ImageDetailVO {

    private ImageVO image;

    private UserVO author;

    private String authorName;

    private boolean liked;

    private boolean faved;
}
