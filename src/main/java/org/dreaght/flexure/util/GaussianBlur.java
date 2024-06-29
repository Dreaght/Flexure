package org.dreaght.flexure.util;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class GaussianBlur {

    public static BufferedImage applyGaussianBlur(BufferedImage srcImage, int radius, double sigma) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        double[] kernel = createKernel(radius, sigma);

        // Horizontal pass
        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                double red = 0.0, green = 0.0, blue = 0.0;
                double sum = 0.0;

                for (int i = -radius; i <= radius; i++) {
                    int currentX = Math.min(width - 1, Math.max(0, x + i));
                    int color = srcImage.getRGB(currentX, y);
                    double weight = kernel[i + radius];

                    red += ((color & 0x00ff0000) >> 16) * weight;
                    green += ((color & 0x0000ff00) >> 8) * weight;
                    blue += (color & 0x000000ff) * weight;
                    sum += weight;
                }

                int r = Math.min(Math.max((int) Math.round(red / sum), 0), 255);
                int g = Math.min(Math.max((int) Math.round(green / sum), 0), 255);
                int b = Math.min(Math.max((int) Math.round(blue / sum), 0), 255);

                tempImage.setRGB(x, y, (r << 16) | (g << 8) | b | (srcImage.getRGB(x, y) & 0xFF000000));
            }
        });

        // Vertical pass
        IntStream.range(0, width).parallel().forEach(x -> {
            for (int y = 0; y < height; y++) {
                double red = 0.0, green = 0.0, blue = 0.0;
                double sum = 0.0;

                for (int i = -radius; i <= radius; i++) {
                    int currentY = Math.min(height - 1, Math.max(0, y + i));
                    int color = tempImage.getRGB(x, currentY);
                    double weight = kernel[i + radius];

                    red += ((color & 0x00ff0000) >> 16) * weight;
                    green += ((color & 0x0000ff00) >> 8) * weight;
                    blue += (color & 0x000000ff) * weight;
                    sum += weight;
                }

                int r = Math.min(Math.max((int) Math.round(red / sum), 0), 255);
                int g = Math.min(Math.max((int) Math.round(green / sum), 0), 255);
                int b = Math.min(Math.max((int) Math.round(blue / sum), 0), 255);

                blurredImage.setRGB(x, y, (r << 16) | (g << 8) | b | (tempImage.getRGB(x, y) & 0xFF000000));
            }
        });

        return blurredImage;
    }

    private static double[] createKernel(int radius, double sigma) {
        int size = radius * 2 + 1;
        double[] kernel = new double[size];
        double sum = 0.0;

        for (int i = -radius; i <= radius; i++) {
            kernel[i + radius] = Math.exp(-(i * i) / (2 * sigma * sigma));
            sum += kernel[i + radius];
        }

        // Normalize the kernel
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }

        return kernel;
    }
}

