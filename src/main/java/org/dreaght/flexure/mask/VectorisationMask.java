package org.dreaght.flexure.mask;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.FlexureApplication;
import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

@Getter @Setter
public class VectorisationMask implements Mask {
    private Point2D massCenter = new Point2D((double) FlexureApplication.WIDTH / 2, (double) FlexureApplication.WIDTH / 2);
    private double radialCoefficient = 0;
    private double centralCoefficient = 1;

    private Point2D deformPixel(Point2D pixel) {
        double r = Math.sqrt(Math.pow((pixel.getX() - massCenter.getX()), 2) + Math.pow((pixel.getY() - massCenter.getY()), 2));
        double factor = centralCoefficient + radialCoefficient * r;
        double trX = (massCenter.getX() + (pixel.getX() - massCenter.getX()) * factor);
        double trY = (massCenter.getY() + (pixel.getY() - massCenter.getY()) * factor);
        return new Point2D(trX, trY);
    }

    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage copiedBufferedImage = BufferedUtil.createEmpty(width, height);

        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                Point2D pixel = deformPixel(new Point2D(x, y));
                int newX = (int) Math.min(Math.max(pixel.getX(), 0), width - 1);
                int newY = (int) Math.min(Math.max(pixel.getY(), 0), height - 1);
                copiedBufferedImage.setRGB(newX, newY, bufferedImage.getRGB(x, y));
            }
        });

        Graphics2D graphics2D = copiedBufferedImage.createGraphics();
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawOval((int) massCenter.getX(), (int) massCenter.getY(), 10, 10);
        graphics2D.dispose();

        return copiedBufferedImage;
    }
}
