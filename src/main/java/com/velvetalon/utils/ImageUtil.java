package com.velvetalon.utils;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @describe: 图像处理工具类
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 17:35 : 创建文件
 * * copy for https://blog.csdn.net/coderALEX/article/details/90081223
 */


public class ImageUtil {

    @SneakyThrows
    public static void mirror( String imagePath ){
        File file = new File(imagePath);
        BufferedImage image = null;

        image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        for (int j = 0; j < height; j++) {
            int l = 0, r = width - 1;
            while (l < r) {
                int pl = image.getRGB(l, j);
                int pr = image.getRGB(r, j);

                image.setRGB(l, j, pr);
                image.setRGB(r, j, pl);

                l++;
                r--;
            }
        }

        file = new File(imagePath);
        ImageIO.write(image, "png", file);
    }

    @SneakyThrows
    public static BufferedImage rotateImage( String imagePath, int angel ){

        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
        if (bufferedImage == null) {
            return null;
        }
        if (angel < 0) {
            // 将负数角度，纠正为正数角度
            angel = angel + 360;
        }
        int imageWidth = bufferedImage.getWidth(null);
        int imageHeight = bufferedImage.getHeight(null);
        // 计算重新绘制图片的尺寸
        Rectangle rectangle = calculatorRotatedSize(new Rectangle(new Dimension(imageWidth, imageHeight)), angel);
        // 获取原始图片的透明度
        int type = bufferedImage.getColorModel().getTransparency();
        BufferedImage newImage = null;
        newImage = new BufferedImage(rectangle.width, rectangle.height, type);
        Graphics2D graphics = newImage.createGraphics();
        // 平移位置
        graphics.translate((rectangle.width - imageWidth) / 2, (rectangle.height - imageHeight) / 2);
        // 旋转角度
        graphics.rotate(Math.toRadians(angel), imageWidth / 2, imageHeight / 2);
        // 绘图
        graphics.drawImage(bufferedImage, null, null);
        return newImage;
    }

    /**
     * 旋转图片
     *
     * @param image 图片
     * @param angel 旋转角度
     * @return
     */
    public static BufferedImage rotateImage( Image image, int angel ){
        if (image == null) {
            return null;
        }
        if (angel < 0) {
            // 将负数角度，纠正为正数角度
            angel = angel + 360;
        }
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        Rectangle rectangle = calculatorRotatedSize(new Rectangle(new Dimension(imageWidth, imageHeight)), angel);
        BufferedImage newImage = null;
        newImage = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = newImage.createGraphics();
        // transform
        graphics.translate((rectangle.width - imageWidth) / 2, (rectangle.height - imageHeight) / 2);
        graphics.rotate(Math.toRadians(angel), imageWidth / 2, imageHeight / 2);
        graphics.drawImage(image, null, null);
        return newImage;
    }

    /**
     * 计算旋转后的尺寸
     *
     * @param src
     * @param angel
     * @return
     */
    private static Rectangle calculatorRotatedSize( Rectangle src, int angel ){
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new java.awt.Rectangle(new Dimension(des_width, des_height));
    }


    private static boolean cropImage( InputStream inputStream, int x, int y, int w, int h, String sufix, File file ) throws IOException{
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        bufferedImage = bufferedImage.getSubimage(x, y, w, h);
        boolean write = ImageIO.write(bufferedImage, sufix, file);
        inputStream.close();
        return write;
    }

    @SneakyThrows
    public static List<String> splitImage( String imagePath, String savePath, int splitCount ){
        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
        int imageWidth = bufferedImage.getWidth(null);
        int imageHeight = bufferedImage.getHeight(null);

        int x = 0;
        int y = 0;

        int height = imageHeight / splitCount + 1;

        List<String> list = new ArrayList<>();

        for (; y < imageHeight; y += height) {
            String fileName = UUID.randomUUID() + ".jpg";
            File file = new File(savePath + "/" + fileName);
            file.createNewFile();
            cropImage(new FileInputStream(imagePath), x, y, imageWidth, y + height > imageHeight ? imageHeight - y : height, "jpg", file);
            list.add(file.getAbsolutePath());
        }
        return list;
    }
}
