package org.dreaght.flexure.util.gaussian;

import java.awt.image.BufferedImage;

public record GaussianBlurResult(BufferedImage blurredImage, double[][] gradientX, double[][] gradientY) {}
