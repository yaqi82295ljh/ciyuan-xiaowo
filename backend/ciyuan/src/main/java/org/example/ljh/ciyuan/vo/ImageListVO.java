package org.example.ljh.ciyuan.vo;

import lombok.Data;

import java.util.List;

/** 图片列表分页结果 */
@Data
public class ImageListVO {

    private List<ImageVO> list;

    private int page;

    private long total;

    private boolean hasMore;

    public static ImageListVO of(List<ImageVO> list, int page, long total, boolean hasMore) {
        ImageListVO vo = new ImageListVO();
        vo.setList(list);
        vo.setPage(page);
        vo.setTotal(total);
        vo.setHasMore(hasMore);
        return vo;
    }
}
