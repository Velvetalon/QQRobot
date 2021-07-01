package com.velvetalon.utils;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @describe: 图像处理工具类
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 17:35 : 创建文件
 * * copy from https://blog.csdn.net/coderALEX/article/details/90081223
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
        return new Rectangle(new Dimension(des_width, des_height));
    }


    private static boolean cropImage( InputStream inputStream, int x, int y, int w, int h, File file ) throws IOException{
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        bufferedImage = bufferedImage.getSubimage(x, y, w, h);
        boolean write = ImageIO.write(bufferedImage, "jpg", file);
        inputStream.close();
        return write;
    }

    @SneakyThrows
    public static List<String> splitImage( String imagePath, String savePath, int splitCount ){
        List<String> list = new ArrayList<>();
        if (splitCount <= 1) {
            list.add(imagePath);
            return list;
        }

        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
        int imageWidth = bufferedImage.getWidth(null);
        int imageHeight = bufferedImage.getHeight(null);

        int x = 0;
        int y = 0;

        int height = imageHeight / splitCount + 1;


        for (; y < imageHeight; y += height) {
            String fileName = UUID.randomUUID() + ".jpg";
            File file = new File(savePath + "/" + fileName);
            file.createNewFile();
            cropImage(new FileInputStream(imagePath), x, y, imageWidth, y + height > imageHeight ? imageHeight - y : height, file);
            list.add(file.getAbsolutePath());
        }
        return list;
    }

    @SneakyThrows
    public static String confuse( String imagePath, String outPath ){

        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        Graphics graphics = bufferedImage.getGraphics();// 得到画图对象 --- 画笔

        graphics.setColor(getRandColor(200, 250));
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setStroke(new BasicStroke((float) ((width + height) / 2 * 0.05)));


        Random random = new Random();

        g2.setColor(getRandColor(160, 200));
        int x1;
        int x2;
        int y1;
        int y2;
        for (int i = 0; i < 15; i++) {
            x1 = random.nextInt(width);
            y1 = random.nextInt(height / 2);
            x2 = random.nextInt(width);
            y2 = random.nextInt(height / 2) + height / 2 + 1;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.dispose();// 释放资源

        String filePath = outPath + "/" + UUID.randomUUID() + ".jpg";
        File file = new File(filePath);

        ImageIO.write(bufferedImage, "jpg", new FileOutputStream(file));
        return file.getAbsolutePath();
    }

    private static Color getRandColor( int fc, int bc ){
        // 取其随机颜色
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public static boolean isCompleteImage( String imagePath ){
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
            int width = bufferedImage.getWidth();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    public static boolean checkCompleteImageWithException( String imagePath ){
        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
        int width = bufferedImage.getWidth();
        return true;
    }
}
