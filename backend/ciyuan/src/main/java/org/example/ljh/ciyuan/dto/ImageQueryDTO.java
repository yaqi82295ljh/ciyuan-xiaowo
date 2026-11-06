package org.example.ljh.ciyuan.dto;

import lombok.Data;

/**
 * 图片列表查询入参。
 * 字段默认值直接写在初始化里，前端不传就是默认值，效果等于原来 @RequestParam(defaultValue=...)。
 */
@Data
public class ImageQueryDTO {

    private Integer page = 1;

    private Integer size = 20;

    private String tags = "";

    private String keyword = "";

    /** all / only / none */
    private String ai = "all";

    /** new / like / fav */
    private String sort = "new";
}
