package org.example.ljh.ciyuan.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** 缩略图工具：给上传的原图生成 thumb_xxx.jpg */
public final class ThumbUtils {

    /** 缩略图宽度 */
    public static final int THUMB_WIDTH = 480;

    /** JPEG 压缩质量 */
    public static final float THUMB_QUALITY = 0.95f;

    private ThumbUtils() {
    }

    /**
     * 为上传目录里的原图生成缩略图。
     *
     * @param sourceName 原图文件名
     * @return 缩略图文件名（thumb_xxx.jpg）；读不出图或失败返回 null，调用方按"没有缩略图"处理
     */
    public static String make(String sourceName) {
        try {
            BufferedImage src = ImageIO.read(new File(UploadUtils.DIR.getAbsoluteFile(), sourceName));
            if (src == null) return null;

            int tw = Math.min(THUMB_WIDTH, src.getWidth());
            int th = Math.max(1, src.getHeight() * tw / src.getWidth()); //高度按原比例算

            BufferedImage dst = new BufferedImage(tw, th, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = dst.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, tw, th);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(src, 0, 0, tw, th, null);
            g.dispose();

            int dot = sourceName.lastIndexOf('.');
            String base = dot > 0 ? sourceName.substring(0, dot) : sourceName;
            String thumbName = "thumb_" + base + ".jpg";
            writeJpeg(dst, new File(UploadUtils.DIR.getAbsoluteFile(), thumbName), THUMB_QUALITY);

            return thumbName;
        } catch (Exception e) {
            //缩略图失败不致命，原图照常可用
            e.printStackTrace();
            return null;
        }
    }

    private static void writeJpeg(BufferedImage img, File out, float quality) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        try (ImageOutputStream os = ImageIO.createImageOutputStream(out)) {
            writer.setOutput(os);
            writer.write(null, new IIOImage(img, null, null), param);
        } finally {
            writer.dispose();
        }
    }
}
