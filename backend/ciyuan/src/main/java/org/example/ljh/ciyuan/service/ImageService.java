package org.example.ljh.ciyuan.service;

import com.baomidou.mybatisplus.spring.service.IService;
import org.example.ljh.ciyuan.dto.ImageQueryDTO;
import org.example.ljh.ciyuan.entity.Image;
import org.example.ljh.ciyuan.vo.ImageListVO;
import org.example.ljh.ciyuan.vo.ImageVO;

import java.util.List;

public interface ImageService extends IService<Image> {

    /** 图片列表：打标签、按标签/关键词/AI 过滤、排序、分页 */
    ImageListVO listImages(ImageQueryDTO query);

    /** 某个用户上传的作品，按时间倒序 */
    List<ImageVO> listMy(Long userId);
}
