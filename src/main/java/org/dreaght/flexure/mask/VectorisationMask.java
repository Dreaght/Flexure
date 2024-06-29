package org.dreaght.flexure.mask;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.FlexureApplication;
import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter @Setter
public class VectorisationMask implements Mask {
    private Point2D massCenter = new Point2D((double) FlexureApplication.WIDTH / 2, (double) FlexureApplication.WIDTH / 2);

    private double radialCoefficient = 0.0005;
    private double centralCoefficient = 1.1;

    private Point2D deformPixel(Point2D pixel) {
        double r = Math.sqrt(Math.pow((pixel.getX() - massCenter.getX()), 2) + Math.pow((pixel.getY() - massCenter.getY()), 2));

        double trX = (massCenter.getX() + (pixel.getX() - massCenter.getX()) * (centralCoefficient + radialCoefficient * r));
        double trY = (massCenter.getY() + (pixel.getY() - massCenter.getY()) * (centralCoefficient + radialCoefficient * r));

        return new Point2D(trX, trY);
    }

    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        BufferedImage copiedBufferedImage = BufferedUtil.createEmpty(
                bufferedImage.getWidth(), bufferedImage.getHeight()
        );

        for (int y = 0; y < bufferedImage.getHeight() - 1; y++) {
            for (int x = 0; x < bufferedImage.getWidth() - 1; x++) {
                Point2D pixel = deformPixel(new Point2D(x, y));

                double newX = Math.min(Math.max(pixel.getX(), 0), copiedBufferedImage.getWidth() - 1);
                double newY = Math.min(Math.max(pixel.getY(), 0), copiedBufferedImage.getHeight() - 1);

                copiedBufferedImage.setRGB((int) newX, (int) newY, bufferedImage.getRGB(x, y));
            }
        }

        Graphics2D graphics2D = copiedBufferedImage.createGraphics();
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawOval((int) massCenter.getX(), (int) massCenter.getY(), 10, 10);
        graphics2D.dispose();

        return copiedBufferedImage;
    }

    public static BufferedImage removeRedundant(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int minX = width;
        int minY = height;
        int maxX = 0;
        int maxY = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bufferedImage.getRGB(x, y);
                Color color = new Color(pixel);

                if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        if (minX == width || minY == height || maxX == 0 || maxY == 0) {
            return bufferedImage;
        }

        int newWidth = maxX - minX + 1;
        int newHeight = maxY - minY + 1;
        BufferedImage croppedImage = new BufferedImage(newWidth, newHeight, bufferedImage.getType());

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int pixel = bufferedImage.getRGB(x, y);
                croppedImage.setRGB(x - minX, y - minY, pixel);
            }
        }

        return croppedImage;
    }
}
