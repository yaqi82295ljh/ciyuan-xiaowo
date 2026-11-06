package org.example.ljh.ciyuan.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.example.ljh.ciyuan.dto.ImageQueryDTO;
import org.example.ljh.ciyuan.entity.Image;
import org.example.ljh.ciyuan.mapper.ImageMapper;
import org.example.ljh.ciyuan.service.ImageService;
import org.example.ljh.ciyuan.service.TagService;
import org.example.ljh.ciyuan.vo.ImageListVO;
import org.example.ljh.ciyuan.vo.ImageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

    private TagService tagService;

    @Autowired
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    public ImageListVO listImages(ImageQueryDTO query) {

        int page = query.getPage();
        int size = query.getSize();
        String tags = query.getTags() == null ? "" : query.getTags();
        String keyword = query.getKeyword() == null ? "" : query.getKeyword();
        String ai = query.getAi() == null ? "all" : query.getAi();
        String sort = query.getSort() == null ? "new" : query.getSort();

        //要筛选的标签名，去重
        List<String> wantTags = new ArrayList<>();
        for (String part : tags.split("[,，、\\s]+")) {
            String name = part.trim();
            if (!name.isEmpty() && !wantTags.contains(name)) {
                wantTags.add(name);
            }
        }

        List<Image> list = list();

        //图片id -> 标签名列表
        Map<Long, List<String>> tagMap = new HashMap<>();
        for (Map<String, Object> row : tagService.listImageTags()) {
            Long imageId = ((Number) row.get("imageId")).longValue();
            String name = (String) row.get("name");

            if (!tagMap.containsKey(imageId)) {
                tagMap.put(imageId, new ArrayList<>());
            }
            tagMap.get(imageId).add(name);
        }

        for (Image img : list) {
            List<String> t = tagMap.get(img.getId());
            img.setTags(t == null ? List.of() : t);
        }

        if (!wantTags.isEmpty()) {
            list.removeIf(img -> !img.getTags().containsAll(wantTags));
        }

        if (!keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();
            list.removeIf(img -> !matchKeyword(img, kw));
        }

        if (!"all".equals(ai)) {
            boolean wantAi = "only".equals(ai);
            list.removeIf(img -> Boolean.TRUE.equals(img.getIsAi()) != wantAi);
        }

        if ("like".equals(sort)) {
            list.sort((a, b) -> nz(b.getLikeCount()) - nz(a.getLikeCount()));
        } else if ("fav".equals(sort)) {
            list.sort((a, b) -> nz(b.getFavCount()) - nz(a.getFavCount()));
        } else {
            list.sort((a, b) -> {
                if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            });
        }

        long imgCount = list.size();
        int from = (page - 1) * size;
        int to = (int) Math.min((long) from + size, imgCount);

        List<Image> pageList = from >= imgCount ? List.of() : list.subList(from, to);

        List<ImageVO> vos = pageList.stream().map(ImageVO::from).toList();

        return ImageListVO.of(vos, page, imgCount, to < imgCount);
    }

    @Override
    public List<ImageVO> listMy(Long userId) {
        return lambdaQuery()
                .eq(Image::getUserId, userId)
                .orderByDesc(Image::getCreatedAt)
                .list()
                .stream()
                .map(ImageVO::from)
                .toList();
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }

    //关键词命中：标题或任一标签
    private static boolean matchKeyword(Image img, String kw) {
        if (img.getTitle() != null && img.getTitle().toLowerCase().contains(kw)) {
            return true;
        }
        for (String t : img.getTags()) {
            if (t != null && t.toLowerCase().contains(kw)) {
                return true;
            }
        }
        return false;
    }
}
