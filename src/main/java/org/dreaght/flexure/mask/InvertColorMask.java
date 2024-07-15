package org.dreaght.flexure.mask;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class InvertColorMask implements Mask {
    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);

        IntStream.range(0, pixels.length).parallel().forEach(i -> {
            int rgb = pixels[i];
            int alpha = (rgb >> 24) & 0xFF;
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

            red = 255 - red;
            green = 255 - green;
            blue = 255 - blue;

            pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
        });

        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
        return bufferedImage;
    }
}
