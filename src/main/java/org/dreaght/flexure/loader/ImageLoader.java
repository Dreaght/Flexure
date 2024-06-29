package org.dreaght.flexure.loader;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.FlexureApplication;
import org.dreaght.flexure.FlexureController;
import org.dreaght.flexure.mask.Mask;
import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Getter @Setter
public class ImageLoader {

    private FlexureApplication flexureApplication;

    private BufferedImage sourceScreenLayer;
    private BufferedImage screenLayer;

    private BufferedImage sketch;
    private BufferedImage sourceSketch;

    private int sketchStartX = 0;
    private int sketchStartY = 0;

    private List<Mask> masks;

    public ImageLoader(FlexureApplication flexureApplication, BufferedImage screenLayer, BufferedImage sketch, List<Mask> masks) {
        this.flexureApplication = flexureApplication;
        this.screenLayer = screenLayer;
        this.masks = masks;
        this.sourceSketch = sketch;
        this.sketch = BufferedUtil.copyImage(sourceSketch);

        sketchStartX = (screenLayer.getWidth() / 2) - (sketch.getWidth() / 2);
        sketchStartY = (screenLayer.getHeight() / 2) - (sketch.getHeight() / 2);
    }

    public void reloadMasksOnSketch() {
        sketch = BufferedUtil.copyImage(sourceSketch);
        masks.forEach(mask -> sketch = BufferedUtil.copyImage(mask.update(sketch)));
    }

    public void drawImage(BufferedImage bufferedImage) {
        WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                pixelWriter.setArgb(x, y, bufferedImage.getRGB(x, y));
            }
        }

        FlexureController flexureController = flexureApplication.getFxmlLoader().getController();
        flexureController.getImageView().setImage(writableImage);
    }

    public void drawSketch() {
        screenLayer = BufferedUtil.createEmpty(screenLayer.getWidth(), screenLayer.getHeight());

        Graphics2D g = screenLayer.createGraphics();
        g.drawImage(sketch, sketchStartX, sketchStartY, sketch.getWidth(), sketch.getHeight(), null);
        g.dispose();

        drawImage(screenLayer);
    }
}
