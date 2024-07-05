package org.dreaght.flexure.util.buffer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;

public class BufferedUtil {

    private final BIStrategy biStrategy;

    public BufferedUtil(File file) {
        if (file.getName().endsWith("png")) {
            biStrategy = new PNGStrategy(file);
        } else {
            biStrategy = new DXFStrategy(file);
        }
    }

    public BufferedImage convertToBuffer(float targetWidth, float targetHeight) throws IOException {
        return biStrategy.convertToBuffer(targetWidth, targetHeight);
    }

    public static BufferedImage createEmpty(int width, int height) {
        return createEmpty(width, height, Color.WHITE);
    }

    public static BufferedImage createEmpty(int width, int height, Color color) {
        BufferedImage bufferedImage = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(color);
        graphics2D.drawRect(0, 0, width, height);
        graphics2D.dispose();

        return bufferedImage;
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage copiedBufferedImage = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D graphics2D = copiedBufferedImage.createGraphics();
        graphics2D.drawImage(source, 0, 0, null);
        graphics2D.dispose();

        return copiedBufferedImage;
    }

    public static BufferedImage resizeImage(BufferedImage source, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public static BufferedImage resizeWithProportions(BufferedImage source, int width, int height) {
        double aspectRatio = (double) source.getWidth() / (double) source.getHeight();

        System.out.println("Resize with proportions (width, height): " + source.getWidth() + " " + source.getHeight());
        System.out.println("Aspect ratio: " + aspectRatio);

        double newWidth;
        double newHeight;
        if (width / aspectRatio <= height) {
            newWidth = width;
            newHeight = (width / aspectRatio);
        } else {
            newWidth = (height / aspectRatio);
            newHeight = height;
        }

        BufferedImage resizedImage = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        g.drawImage(source.getScaledInstance((int) newWidth, (int) newHeight, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();

        return resizedImage;
    }

    public static BufferedImage scaleimage(int wid, int hei, BufferedImage img) {
        int newWidth;
        int newHeight;
        double scale;
        double imw = img.getWidth();
        double imh = img.getHeight();

        if (wid > imw && hei > imh) {
            newWidth = (int) imw;
            newHeight = (int) imh;
        } else if (wid / imw < hei / imh) {
            scale = wid / imw;
            newWidth = (int) (scale * imw);
            newHeight = (int) (scale * imh);
        } else if (wid / imw > hei / imh) {
            scale = hei / imh;
            newWidth = (int) (scale * imw);
            newHeight = (int) (scale * imh);
        } else {
            scale = wid / imw;
            newWidth = (int) (scale * imw);
            newHeight = (int) (scale * imh);
        }

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    public static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        try {
            return src.getSubimage(rect.x, rect.y, rect.width, rect.height);
        } catch (RasterFormatException e) {
            System.out.println("Cannot crop the image! Image size: " +
                    src.getWidth() + "x" + src.getHeight() + ". But you're trying to crop it with: " + rect);
            return src;
        }
    }

    public static BufferedImage extendImage(BufferedImage source, int offsetX, int offsetY) {
        BufferedImage b = new BufferedImage(source.getWidth() + offsetX, source.getHeight() + offsetY, source.getType());
        Graphics2D g = b.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, b.getWidth(), b.getHeight());

        g.drawImage(source, offsetX / 2, offsetY / 2, null);
        g.dispose();

        return b;
    }
}
