package org.dreaght.flexure.util.buffer;

import java.awt.image.BufferedImage;
import java.io.IOException;

interface BIStrategy {
    BufferedImage convertToBuffer(float targetWidth, float targetHeight) throws IOException;
}
