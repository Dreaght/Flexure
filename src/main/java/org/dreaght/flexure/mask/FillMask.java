package org.dreaght.flexure.mask;

import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

public class FillMask implements Mask {
    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        return fillBufferedImage(bufferedImage);
    }

    private BufferedImage fillBufferedImage(BufferedImage bufferedImage) {
        BufferedImage bufImageCopy = BufferedUtil.copyImage(bufferedImage);

        fillArea(bufImageCopy, Color.WHITE.getRGB(), Color.BLUE.getRGB());
        fillArea(bufImageCopy, Color.WHITE.getRGB(), Color.BLACK.getRGB());
        fillArea(bufImageCopy, Color.BLUE.getRGB(), Color.WHITE.getRGB());

        return bufImageCopy;
    }

    private void fillArea(BufferedImage image, int targetColor, int replacementColor) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getRGB(x, y) == targetColor) {
                    floodFill(image, x, y, targetColor, replacementColor);
                    return;
                }
            }
        }
    }

    private void floodFill(BufferedImage image, int startX, int startY, int targetColor, int replacementColor) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][] directions = {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(startX, startY));

        while (!queue.isEmpty()) {
            Point point = queue.poll();
            int x = point.x;
            int y = point.y;

            if (x < 0 || x >= width || y < 0 || y >= height) continue;
            if (image.getRGB(x, y) != targetColor) continue;

            image.setRGB(x, y, replacementColor);

            for (int[] direction : directions) {
                queue.add(new Point(x + direction[0], y + direction[1]));
            }
        }
    }
}
