package org.dreaght.flexure.mask;

import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.image.BufferedImage;

@Getter @Setter
public class ResizeMask implements Mask {

    double sizeCoefficient = 50; // 0-100

    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        int targetWidth = Math.max((int) (bufferedImage.getWidth() * (sizeCoefficient / 50)), 300);
        int targetHeight = Math.max((int) (bufferedImage.getHeight() * (sizeCoefficient / 50)), 300);

        bufferedImage = BufferedUtil.resizeWithProportions(bufferedImage, targetWidth, targetHeight);
        bufferedImage = BufferedUtil.extendImage(bufferedImage, targetWidth, targetHeight);

        return bufferedImage;
    }
}
