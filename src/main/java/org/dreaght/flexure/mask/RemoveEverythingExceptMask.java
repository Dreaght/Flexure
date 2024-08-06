package org.dreaght.flexure.mask;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RemoveEverythingExceptMask implements Mask {

    private final Color maskColor;

    public RemoveEverythingExceptMask(Color maskColor) {
        this.maskColor = maskColor;
    }

    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = bufferedImage.getRGB(x, y);
                Color color = new Color(rgb, true);

                if (color.equals(maskColor)) {
                    resultImage.setRGB(x, y, maskColor.getRGB());
                } else {
                    resultImage.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return resultImage;
    }
}
