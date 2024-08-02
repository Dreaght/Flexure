package org.dreaght.flexure.mask;

import java.awt.image.BufferedImage;

public interface Mask {
    BufferedImage update(BufferedImage bufferedImage);
}
