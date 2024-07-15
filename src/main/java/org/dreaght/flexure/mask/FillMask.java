package org.dreaght.flexure.mask;

import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

public class FillMask implements Mask {
    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        BufferedImage bufImageCopy = BufferedUtil.copyImage(bufferedImage);

        fillArea(bufImageCopy, Color.WHITE.getRGB(), Color.BLUE.getRGB());
        fillArea(bufImageCopy, Color.WHITE.getRGB(), Color.BLACK.getRGB());
        fillArea(bufImageCopy, Color.BLUE.getRGB(), Color.WHITE.getRGB());

        return bufImageCopy;
    }

    private void fillArea(BufferedImage image, int targetColor, int replacementColor) {
        boolean[][] visited = new boolean[image.getWidth()][image.getHeight()];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getRGB(x, y) == targetColor && !visited[x][y]) {
                    floodFill(image, x, y, targetColor, replacementColor, visited);
                    return;
                }
            }
        }
    }

    private void floodFill(BufferedImage image, int startX, int startY, int targetColor, int replacementColor, boolean[][] visited) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][] directions = {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1}
        };

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(startX, startY));
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            Point point = queue.poll();
            int x = point.x;
            int y = point.y;

            if (image.getRGB(x, y) != targetColor) continue;

            image.setRGB(x, y, replacementColor);

            for (int[] direction : directions) {
                int newX = x + direction[0];
                int newY = y + direction[1];
                if (newX >= 0 && newX < width && newY >= 0 && newY < height && !visited[newX][newY]) {
                    queue.add(new Point(newX, newY));
                    visited[newX][newY] = true;
                }
            }
        }
    }
}
