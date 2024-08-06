package org.dreaght.flexure.mask;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RemoveNonWhite implements Mask {
    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = bufferedImage.getRGB(x, y);
                Color color = new Color(rgb, true);

                if (color.equals(Color.WHITE)) {
                    resultImage.setRGB(x, y, Color.WHITE.getRGB());
                } else if (color.equals(Color.RED)) {
                    resultImage.setRGB(x, y, Color.RED.getRGB());
                } else {
                    resultImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return resultImage;
    }
}
