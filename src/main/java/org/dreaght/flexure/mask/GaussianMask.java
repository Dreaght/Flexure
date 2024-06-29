package org.dreaght.flexure.mask;

import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.util.GaussianBlur;

import java.awt.image.BufferedImage;

@Getter @Setter
public class GaussianMask implements Mask {

    private int radius = 50;
    private int sigma = 50;

    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        return GaussianBlur.applyGaussianBlur(bufferedImage, radius, sigma);
    }
}
