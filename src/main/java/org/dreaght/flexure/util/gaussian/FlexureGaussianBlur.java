package org.dreaght.flexure.util.gaussian;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class FlexureGaussianBlur implements GaussianBlur {

    @Override
    public BufferedImage applyGaussianBlur(BufferedImage srcImage, int radius, double sigma) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        long startTime = System.currentTimeMillis();
        double[] kernel = createKernel(radius, sigma);
        System.out.println("Kernel creation has took: " + (System.currentTimeMillis() - startTime));

        long startTime2 = System.currentTimeMillis();
        // Horizontal pass | From srcImage to tempImage.
        processImage(srcImage, tempImage, width, height, kernel, radius, true);
        System.out.println("Horizontal pass has took: " + (System.currentTimeMillis() - startTime2));

        long startTime3 = System.currentTimeMillis();
        // Vertical pass | From tempImage to blurredImage.
        processImage(tempImage, blurredImage, width, height, kernel, radius, false);
        System.out.println("Vertical has took: " + (System.currentTimeMillis() - startTime3));

        return blurredImage;
    }

    private static void processImage(BufferedImage srcImage, BufferedImage destImage, int width, int height,
                                     double[] kernel, int radius, boolean isHorizontal) {
        if (isHorizontal) {
            IntStream.range(0, height).parallel().forEach(y -> {
                for (int x = 0; x < width; x++) {
                    applyKernel(srcImage, destImage, width, height, kernel, radius, x, y, isHorizontal);
                }
            });
        } else {
            IntStream.range(0, width).parallel().forEach(x -> {
                for (int y = 0; y < height; y++) {
                    applyKernel(srcImage, destImage, width, height, kernel, radius, x, y, isHorizontal);
                }
            });
        }
    }

    private static void applyKernel(BufferedImage srcImage, BufferedImage destImage, int width, int height,
                                    double[] kernel, int radius, int x, int y, boolean isHorizontal) {
        float red = 0.0f, green = 0.0f, blue = 0.0f;
        float sum = 0.0f;

        int minIndex = -radius;
        int maxIndex = radius;
        int kernelRadius = radius;

        for (int i = minIndex; i <= maxIndex; i++) {
            int currentX = isHorizontal ? x + i : x;
            int currentY = isHorizontal ? y : y + i;

            // Clamp pixel coordinates to image boundaries
            if (currentX < 0) currentX = 0;
            if (currentX >= width) currentX = width - 1;
            if (currentY < 0) currentY = 0;
            if (currentY >= height) currentY = height - 1;

            int color = srcImage.getRGB(currentX, currentY);
            float weight = (float) kernel[i + kernelRadius];

            red += ((color >> 16) & 0xFF) * weight;
            green += ((color >> 8) & 0xFF) * weight;
            blue += (color & 0xFF) * weight;
            sum += weight;
        }

        if (sum != 0.0f) {
            red /= sum;
            green /= sum;
            blue /= sum;
        }

        int r = (int) Math.min(Math.max(red, 0.0f), 255.0f);
        int g = (int) Math.min(Math.max(green, 0.0f), 255.0f);
        int b = (int) Math.min(Math.max(blue, 0.0f), 255.0f);

        destImage.setRGB(x, y, (r << 16) | (g << 8) | b | (srcImage.getRGB(x, y) & 0xFF000000));
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
