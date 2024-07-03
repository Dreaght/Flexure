package org.dreaght.flexure.mask;

import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.util.gaussian.GaussianBlur;
import org.dreaght.flexure.util.gaussian.GaussianBlurResult;

import java.awt.*;
import java.awt.image.BufferedImage;

@Getter @Setter
public class GaussianMask implements Mask {

    private int radius = 50;
    private int sigma = 50;

    @Override
    public BufferedImage update(BufferedImage bufferedImage) {

        GaussianBlurResult gaussianBlurResult = GaussianBlur.applyGaussianBlur(bufferedImage, radius, sigma);

        double[][] gradientX = gaussianBlurResult.gradientX();
        double[][] gradientY = gaussianBlurResult.gradientY();

        BufferedImage blurredImage = gaussianBlurResult.blurredImage();
//        drawArrows(blurredImage, gradientX, gradientY, 20, 0, 0, false, Color.YELLOW);

        BufferedImage invertedGaussianImage = invertGaussianField(blurredImage, bufferedImage, gradientX, gradientY);
//        drawArrows(invertedGaussianImage, gradientX, gradientY, 20, 0, 0, false, Color.RED);

        return invertedGaussianImage;
    }

    private BufferedImage invertGaussianField(BufferedImage blurredImage, BufferedImage image, double[][] gradientX, double[][] gradientY) {
        BufferedImage tempImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (y >= gradientX.length || y >= gradientY.length) continue;
                if (x >= gradientX[y].length || x >= gradientY[y].length) continue;

                int dx = 0;
                int dy = 0;
                try {
                    dx = (blurredImage.getRGB(Math.max(x - 1, 0), y) - blurredImage.getRGB(Math.min(x + 1, blurredImage.getWidth()), y)) / 20000;
                    dy = (blurredImage.getRGB(x, Math.min(y + 1, blurredImage.getHeight())) - blurredImage.getRGB(x, Math.max(y - 1, 0))) / 20000;
                } catch (Exception ignored) {}

                try {
                    tempImage.setRGB(
                            Math.max(Math.min(x, tempImage.getWidth() - 1), 0),
                            Math.max(Math.min(y, tempImage.getHeight() - 1), 0),
                            image.getRGB(
                                    Math.max(Math.min(x + dx, image.getWidth()), 0),
                                    Math.max(Math.min(y + dy, image.getHeight()), 0)
                            ));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }

        return tempImage;
    }

    private void drawArrows(BufferedImage image, double[][] gradientX, double[][] gradientY,
                            final int step_size,
                            final int vector_offset_X, final int vector_offset_Y,
                            boolean debug, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color);

        for (int y = 0; y < image.getHeight(); y += step_size) {
            for (int x = 0; x < image.getWidth() - 1; x += step_size) {
                if (y >= gradientX.length || y >= gradientY.length) continue;
                if (x >= gradientX[y].length || x >= gradientY[y].length) continue;

//                double dx = gradientX[x][y] + vector_offset_X;
//                double dy = gradientY[x][y] + vector_offset_Y;

                //dX(x:y) = val(x-1 : y) - val(x+1 : y)
                //dY(x:y) = val(x:y+1) - val(x:y-1)
                int dx = (image.getRGB(Math.max(x - 1, 0), y) - image.getRGB(Math.min(x + 1, image.getWidth()), y)) / 1000000;
                int dy = (image.getRGB(x, Math.min(y + 1, image.getHeight())) - image.getRGB(x, Math.max(y - 1, 0))) / 1000000;

                if (debug) {
                    System.out.println(dx + " " + dy);
                }

                drawArrow(g, x - dx, y + dy, x, y);
            }
        }

        g.dispose();
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
}
