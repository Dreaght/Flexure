package org.dreaght.flexure.mask;

import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.util.gaussian.GaussianBlur;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

@Getter @Setter

public class GaussianMask implements Mask {
    private int radius = 200;
    private int sigma = 45;
    private double arrowLengthCoefficient = 0.000015;
    private double arrowOffsetX = 1;
    private double arrowOffsetY = -1;
    private boolean shouldRenderVectors = true;

    @Override
    public BufferedImage update(BufferedImage bufferedImage) {

        long startTime = System.currentTimeMillis();
        BufferedImage blurredImage = GaussianBlur.applyGaussianBlur(bufferedImage, radius, sigma);
        System.out.println("Applying has took: " + (System.currentTimeMillis() - startTime));

        long startTime2 = System.currentTimeMillis();
        BufferedImage invertedGaussianImage = invertGaussianField(blurredImage, bufferedImage);
        System.out.println("Inverting has took: " + (System.currentTimeMillis() - startTime2));

        if (shouldRenderVectors) {
            drawArrows(invertedGaussianImage, blurredImage, 20, Color.RED, false);
        }

        return invertedGaussianImage;
    }

    private BufferedImage invertGaussianField(BufferedImage blurredImage, BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage tempImage = new BufferedImage(width, height, image.getType());

        IntStream.range(0, height - 1).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                Vector vector = getGradientVectorByCoordinate(blurredImage, x, y);
                int dx = (int) vector.dx;
                int dy = (int) vector.dy;

                int newX = (int) Math.min(Math.max(x + (dx * arrowOffsetX), 0), tempImage.getWidth() - 1);
                int newY = (int) Math.min(Math.max(y + (dy * arrowOffsetY), 0), tempImage.getHeight() - 1);

                tempImage.setRGB(newX, newY, image.getRGB(x, y));
            }
        });

        return tempImage;
    }

    private void drawArrows(BufferedImage toImage, BufferedImage source, int stepSize, Color color, boolean debug) {
        Graphics2D g = toImage.createGraphics();
        g.setColor(color);

        int width = source.getWidth();
        int height = source.getHeight();

        for (int y = 0; y < height; y += stepSize) {
            for (int x = 0; x < width; x += stepSize) {
                Vector vector = getGradientVectorByCoordinate(source, x, y);
                int dx = (int) vector.dx;
                int dy = (int) vector.dy;

                if (debug) {
                    System.out.println(dx + " " + dy);
                }

                if (dx == 0 && dy == 0) continue;
                drawArrow(g, x - dx, y + dy, x, y);
            }
        }

        g.dispose();
    }

    private Vector getGradientVectorByCoordinate(BufferedImage image, int x, int y) {
        int dx = 0;
        int dy = 0;
        int width = image.getWidth();
        int height = image.getHeight();

        // Ensure x-5 and x+5 are within image bounds
        int x1 = Math.max(x - 5, 0);
        int x2 = Math.min(x + 5, width - 1);

        // Ensure y-5 and y+5 are within image bounds
        int y1 = Math.max(y - 5, 0);
        int y2 = Math.min(y + 5, height - 1);

        dx = (int) ((image.getRGB(x1, y) - image.getRGB(x2, y)) * arrowLengthCoefficient);
        dy = (int) ((image.getRGB(x, y2) - image.getRGB(x, y1)) * arrowLengthCoefficient);

        return new Vector(dx, dy);
    }

    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowHeadLength = 5;
        int arrowHeadWidth = 3;

        int x3 = (int) (x2 - arrowHeadLength * Math.cos(angle - Math.PI / 6));
        int y3 = (int) (y2 - arrowHeadLength * Math.sin(angle - Math.PI / 6));
        int x4 = (int) (x2 - arrowHeadLength * Math.cos(angle + Math.PI / 6));
        int y4 = (int) (y2 - arrowHeadLength * Math.sin(angle + Math.PI / 6));

        int[] xPoints = {x2, x3, x4};
        int[] yPoints = {y2, y3, y4};
        g.fillPolygon(xPoints, yPoints, 3);
    }

    private static class Vector {
        double dx, dy;

        Vector(double dx, double dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }
}
