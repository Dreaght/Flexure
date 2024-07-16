package org.dreaght.flexure.util.gaussian;

import java.awt.image.BufferedImage;

public interface GaussianBlur {
    BufferedImage applyGaussianBlur(BufferedImage srcImage, int radius, double sigma);
}
