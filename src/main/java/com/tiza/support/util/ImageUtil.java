package com.tiza.support.util;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.madgag.gif.fmsware.GifDecoder;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Description: ImageUtil
 * Author: DIYILIU
 * Update: 2018-09-19 13:27
 */
public class ImageUtil {


    /**
     * 压缩图片
     *
     * @param imagePath
     * @param outPath
     * @param size
     * @param max
     * @throws IOException
     */
    public static void zipQuality(String imagePath, String outPath, int size, int max) throws IOException {
        if (size < 3){
            Thumbnails.of(imagePath).scale(1).toFile(outPath);
            return;
        }

        double quality = new BigDecimal(max * 2).divide(new BigDecimal(size), 1, RoundingMode.HALF_UP).doubleValue();
        //保持纵横比，质量降低
        Thumbnails.of(imagePath).scale(1).outputQuality(quality).toFile(outPath);
    }

    /**
     * 裁切
     *
     * @param imagePath
     * @param x
     * @param y
     * @param width
     * @param height
     * @param outPath
     */
    public static void crop(String imagePath, int x, int y, int width, int height, String outPath) throws Exception {
        if (imagePath.toLowerCase().endsWith(".gif")) {
            cropGif(imagePath, x, y, width, height, outPath);

            return;
        }

        Thumbnails.of(imagePath).sourceRegion(x, y, width, height).scale(1).toFile(outPath);
    }

    /**
     * 缩放
     *
     * @param imagePath
     * @param width
     * @param height
     * @param outPath
     */
    public static void scale(String imagePath, int width, int height, String outPath) throws Exception {
        if (imagePath.toLowerCase().endsWith(".gif")) {
            zoomGif(imagePath, width, height, outPath);

            return;
        }

        Thumbnails.of(imagePath).size(width, height).keepAspectRatio(false).toFile(outPath);

    }

    /**
     * GIF 图片缩放
     *
     * @param imagePath
     * @param width
     * @param height
     * @param outputPath
     * @throws IOException
     */
    private static void zoomGif(String imagePath, int width, int height, String outputPath) throws IOException {
        // GIF需要特殊处理
        GifDecoder decoder = new GifDecoder();
        int status = decoder.read(imagePath);
        if (status != GifDecoder.STATUS_OK) {
            throw new IOException("read image " + imagePath + " error!");
        }

        // 拆分一帧一帧的压缩之后合成
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(outputPath);
        encoder.setRepeat(decoder.getLoopCount());

        for (int i = 0; i < decoder.getFrameCount(); i++) {
            encoder.setDelay(decoder.getDelay(i));// 设置播放延迟时间
            BufferedImage bufferedImage = decoder.getFrame(i);// 获取每帧BufferedImage流

            BufferedImage zoomImage = new BufferedImage(width, height, bufferedImage.getType());
            Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            Graphics gc = zoomImage.getGraphics();
            gc.setColor(Color.WHITE);
            gc.drawImage(image, 0, 0, null);
            encoder.addFrame(zoomImage);
        }
        encoder.finish();
        File outFile = new File(outputPath);
        BufferedImage image = ImageIO.read(outFile);
        ImageIO.write(image, outFile.getName(), outFile);
    }


    /**
     * GIF 图片裁切
     *
     * @param imagePath
     * @param x
     * @param y
     * @param width
     * @param height
     * @param outputPath
     * @throws IOException
     */
    private static void cropGif(String imagePath, int x, int y, int width, int height, String outputPath) throws IOException {
        // GIF需要特殊处理
        GifDecoder decoder = new GifDecoder();
        int status = decoder.read(imagePath);
        if (status != GifDecoder.STATUS_OK) {
            throw new IOException("read image " + imagePath + " error!");
        }

        // 拆分一帧一帧的压缩之后合成
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(outputPath);
        encoder.setRepeat(decoder.getLoopCount());

        for (int i = 0; i < decoder.getFrameCount(); i++) {
            encoder.setDelay(decoder.getDelay(i));// 设置播放延迟时间
            BufferedImage bufferedImage = decoder.getFrame(i);// 获取每帧BufferedImage流

            BufferedImage zoomImage = bufferedImage.getSubimage(x, y, width, height);
            Graphics gc = zoomImage.getGraphics();
            gc.setColor(Color.WHITE);
            encoder.addFrame(zoomImage);
        }
        encoder.finish();
        File outFile = new File(outputPath);
        BufferedImage image = ImageIO.read(outFile);
        ImageIO.write(image, outFile.getName(), outFile);
    }
}
