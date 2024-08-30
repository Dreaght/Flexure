package org.dreaght.flexure.loader;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.FlexureApplication;
import org.dreaght.flexure.FlexureController;
import org.dreaght.flexure.mask.*;
import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Getter @Setter
public class ImageLoader {

    private final FlexureApplication flexureApplication;

    private BufferedImage sourceScreenLayer;
    private BufferedImage screenLayer;

    private BufferedImage sketch;
    private final BufferedImage sourceSketch;
    private BufferedImage sourceSketchThatWasMasked;

    private int sketchStartX = 0;
    private int sketchStartY = 0;

    private List<Mask> masks;

    private boolean hasAlreadyRendered = false;
    private boolean showInputLayer = true;

    public ImageLoader(FlexureApplication flexureApplication, BufferedImage screenLayer, BufferedImage sketch, List<Mask> masks) {
        this.flexureApplication = flexureApplication;
        this.screenLayer = screenLayer;
        this.masks = masks;
        this.sourceSketch = sketch;
        this.sketch = BufferedUtil.copyImage(sourceSketch);
        this.sourceSketchThatWasMasked = BufferedUtil.copyImage(sourceSketch);

        sketchStartX = (screenLayer.getWidth() / 2) - (sketch.getWidth() / 2);
        sketchStartY = (screenLayer.getHeight() / 2) - (sketch.getHeight() / 2);
    }

    public void reloadMasksOnSketch() {
        BufferedImage newSketch = BufferedUtil.copyImage(sourceSketch);

        for (Mask mask : masks) {
            if (!hasAlreadyRendered && !((mask instanceof FillMask) ||
                    (mask instanceof ResizeMask) || mask instanceof InvertColorMask ||
                    mask instanceof BorderMask)) {
                continue;
            }

            newSketch = mask.update(newSketch);
        }

        for (Mask mask : masks) {
            if (mask instanceof DebugMask debugMask) {
                newSketch = debugMask.updateDebug(newSketch);
            }
        }

        sketch = newSketch;

        if (!hasAlreadyRendered) {
            this.sourceSketchThatWasMasked = BufferedUtil.copyImage(sketch);
            this.sourceSketchThatWasMasked = new BorderMask().update(this.sourceSketchThatWasMasked);
            hasAlreadyRendered = true;
        }
    }

    public void drawImage(BufferedImage image) {
        drawImage(image, 255);
    }

    public void drawImage(BufferedImage bufferedImage, int alpha) {
        WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] rgbArray = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, rgbArray, 0, width);
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), rgbArray, 0, width);

        FlexureController flexureController = flexureApplication.getFxmlLoader().getController();
        flexureController.getImageView().setImage(writableImage);
    }

    public void drawSketch() {
        screenLayer = BufferedUtil.createEmpty(4000, 4000);

        Graphics2D g = screenLayer.createGraphics();
        g.drawImage(sketch, sketchStartX, sketchStartY, sketch.getWidth(), sketch.getHeight(), null);
        g.dispose();

        drawImage(screenLayer);

        if (showInputLayer) {
            BufferedImage markedSketch = BufferedUtil.copyImage(screenLayer);
            Graphics2D g2 = markedSketch.createGraphics();
            g2.drawImage(sourceSketchThatWasMasked, sketchStartX, sketchStartY, sketch.getWidth(), sketch.getHeight(), null);
            g2.dispose();

            drawImage(markedSketch, 100);
        }
    }
}
