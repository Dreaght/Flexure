package org.dreaght.flexure.mask;

import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.image.BufferedImage;

@Getter @Setter
public class StretchingMask implements Mask {
    private double stretch_X = 1;
    private double stretch_Y = 1;

    @Override
    public BufferedImage update(BufferedImage bufferedImage) {
        return BufferedUtil.stretchImage(bufferedImage, stretch_X, stretch_Y);
    }
}
