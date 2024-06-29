package org.dreaght.flexure.util.buffer;

import com.aspose.cad.Color;
import com.aspose.cad.Image;
import com.aspose.cad.imageoptions.CadRasterizationOptions;
import com.aspose.cad.imageoptions.ImageOptionsBase;
import com.aspose.cad.imageoptions.PngOptions;
import lombok.AllArgsConstructor;
import org.dreaght.flexure.FlexureApplication;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@AllArgsConstructor
class DXFStrategy implements BIStrategy {

    private static final int WATERMARK_OFFSET_Y = 150;
    private final File file;

    @Override
    public BufferedImage convertToBuffer(float targetWidth, float targetHeight) {

        InputStream targetStream = getTargetStream();
        Image image = loadImage(targetStream);
        CadRasterizationOptions rasterizationOptions = getRasterisationOptions(targetWidth,
                targetHeight + WATERMARK_OFFSET_Y);
        ImageOptionsBase options = new PngOptions();
        options.setVectorRasterizationOptions(rasterizationOptions);

        tryToSaveCache(image);

        return convertImageToBufferedImage(image, options);
    }

    private InputStream getTargetStream() {
        InputStream targetStream = null;

        try {
            targetStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            FlexureApplication.getInstance().getApplicationLifecycle().stop();
        }

        return targetStream;
    }

    private Image loadImage(InputStream targetStream) {
        return Image.load(targetStream);
    }

    private CadRasterizationOptions getRasterisationOptions(float width, float height) {
        CadRasterizationOptions rasterizationOptions = new CadRasterizationOptions();
        rasterizationOptions.setBackgroundColor(Color.getWhite());
        rasterizationOptions.setPageWidth(width);
        rasterizationOptions.setPageHeight(height);

        return rasterizationOptions;
    }

    private void tryToSaveCache(Image image) {
        if (!image.isCached()) image.cacheData();
    }

    private BufferedImage convertImageToBufferedImage(Image image, ImageOptionsBase options) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.save(byteArrayOutputStream, options);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            return removeWatermark(bufferedImage);
        } catch (IOException e) {
            System.out.println("Your image is corrupted, idk... :/");
            FlexureApplication.getInstance().getApplicationLifecycle().stop();
        }

        return null;
    }

    private BufferedImage removeWatermark(BufferedImage bufferedImage) {
        return BufferedUtil.cropImage(BufferedUtil.extendImage(bufferedImage, 0, WATERMARK_OFFSET_Y),
                new Rectangle(0, WATERMARK_OFFSET_Y, bufferedImage.getWidth(), bufferedImage.getHeight()));
    }
}
