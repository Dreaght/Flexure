package org.dreaght.flexure.mask;

import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.util.buffer.BufferedUtil;
import org.dreaght.flexure.util.gaussian.GaussianBlur;
import org.dreaght.flexure.util.gaussian.GaussianBlurResult;

import java.awt.*;
import java.awt.image.BufferedImage;

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

        GaussianBlurResult gaussianBlurResult = GaussianBlur.applyGaussianBlur(bufferedImage, radius, sigma);

        double[][] gradientX = gaussianBlurResult.gradientX();
        double[][] gradientY = gaussianBlurResult.gradientY();

        final BufferedImage blurredImage = gaussianBlurResult.blurredImage();
//        drawArrows(blurredImage, gradientX, gradientY, 20, 0, 0, false, Color.YELLOW);

        final BufferedImage invertedGaussianImage = invertGaussianField(BufferedUtil.copyImage(blurredImage), bufferedImage, gradientX, gradientY);

        if (shouldRenderVectors) {
            drawArrows(invertedGaussianImage, blurredImage, gradientX, gradientY, 20, Color.RED, false);
        }

        return invertedGaussianImage;
    }

    private BufferedImage invertGaussianField(BufferedImage blurredImage, BufferedImage image, double[][] gradientX, double[][] gradientY) {
        BufferedImage tempImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (y >= gradientX.length || y >= gradientY.length) continue;
                if (x >= gradientX[y].length || x >= gradientY[y].length) continue;

                Vector vector = getGradientVectorByCoordinate(blurredImage, x, y);
                int dx = (int) vector.dx;
                int dy = (int) vector.dy;

                try {
                    tempImage.setRGB(
                            Math.max(Math.min(x, tempImage.getWidth() - 1), 0),
                            Math.max(Math.min(y, tempImage.getHeight() - 1), 0),
                            image.getRGB(
                                    (int) Math.max(Math.min(x + (dx * arrowOffsetX), image.getWidth()), 0),
                                    (int) Math.max(Math.min(y + (dy * arrowOffsetY), image.getHeight()), 0)
                            ));
                } catch (Exception ignored) {
                }
            }
        }

        return tempImage;
    }

    private void drawArrows(BufferedImage toImage, BufferedImage source, double[][] gradientX, double[][] gradientY,
                            final int step_size, Color color, boolean debug) {
        Graphics2D g = toImage.createGraphics();
        g.setColor(color);

        for (int y = 0; y < source.getHeight(); y += step_size) {
            for (int x = 0; x < source.getWidth() - 1; x += step_size) {
                if (y >= gradientX.length || y >= gradientY.length) continue;
                if (x >= gradientX[y].length || x >= gradientY[y].length) continue;

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
        try {
            dx = (int) ((image.getRGB(
                    Math.max(x - 5, 0), y) - image.getRGB(Math.min(x + 5, image.getWidth()), y))
                    * arrowLengthCoefficient);
            dy = (int) ((image.getRGB(
                    x, Math.min(y + 5, image.getHeight())) - image.getRGB(x, Math.max(y - 5, 0)))
                    * arrowLengthCoefficient);
        } catch (Exception ignored) {}
        return new Vector(dx, dy);
    }



    private static void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
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

    private record Vector(double dx, double dy) {}
}
