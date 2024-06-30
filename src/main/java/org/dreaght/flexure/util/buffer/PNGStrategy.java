package org.dreaght.flexure.util.buffer;

import lombok.AllArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@AllArgsConstructor
class PNGStrategy implements BIStrategy {

    private final File file;

    @Override
    public BufferedImage convertToBuffer(float targetWidth, float targetHeight) throws IOException {
        return BufferedUtil.resizeWithProportions(ImageIO.read(file), (int) targetWidth, (int) targetHeight);
    }
}
