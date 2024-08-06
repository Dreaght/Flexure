package org.dreaght.flexure.mask;

import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BorderMask implements Mask {
    private static final Color LIGHT_GREEN = new Color(144, 238, 144);
    private static final Color BLACK = Color.BLACK;
    private static final Color WHITE = Color.WHITE;
    private Color maskColor = LIGHT_GREEN;
    private static final int BORDER_RADIUS = 3;

    public BorderMask() {}

    public BorderMask(Color color) {
        maskColor = color;
    }

    @Override
    public BufferedImage update(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage newImage = BufferedUtil.copyImage(image);

        // change borders to light green
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isBorderPixel(newImage, x, y, width, height)) {
                    newImage.setRGB(x, y, maskColor.getRGB());
                }
            }
        }

        // change white pixels to black
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isWhite(newImage.getRGB(x, y))) {
                    newImage.setRGB(x, y, BLACK.getRGB());
                }
            }
        }

        // change black pixels to transparent
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isBlack(newImage.getRGB(x, y))) {
                    newImage.setRGB(x, y, 0x00FFFFFF); // Fully transparent
                }
            }
        }

        return newImage;
    }

    private boolean isWhite(int rgb) {
        Color color = new Color(rgb);
        return color.equals(WHITE);
    }

    private boolean isBlack(int rgb) {
        Color color = new Color(rgb, true);
        return color.equals(BLACK);
    }

    private boolean isBorderPixel(BufferedImage image, int x, int y, int width, int height) {
        int pixelColor = image.getRGB(x, y);

        if (pixelColor == BLACK.getRGB()) {
            return false;
        }

        for (int dy = -BORDER_RADIUS; dy <= BORDER_RADIUS; dy++) {
            for (int dx = -BORDER_RADIUS; dx <= BORDER_RADIUS; dx++) {
                if (dx == 0 && dy == 0) continue;

                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (image.getRGB(nx, ny) == BLACK.getRGB()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
