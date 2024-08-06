package org.dreaght.flexure.mask;

import org.dreaght.flexure.util.gaussian.BoxGaussianBlur;
import org.dreaght.flexure.util.gaussian.GaussianBlur;

import java.awt.image.BufferedImage;

public class GoodbyeHolesMask implements Mask {
    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        GaussianBlur gaussianBlur = new BoxGaussianBlur();
        BufferedImage blurredImage = gaussianBlur.applyGaussianBlur(bufferedImage, 2, 1);

        return blurredImage;
    }
}
