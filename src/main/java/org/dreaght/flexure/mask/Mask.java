package org.dreaght.flexure.mask;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface Mask {
    BufferedImage update(BufferedImage bufferedImage);
}
