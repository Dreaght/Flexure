package org.dreaght.flexure;

import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;
import lombok.Getter;
import org.dreaght.flexure.loader.FileLoader;
import org.dreaght.flexure.loader.ImageLoader;
import org.dreaght.flexure.mask.FillMask;
import org.dreaght.flexure.mask.GaussianMask;
import org.dreaght.flexure.mask.InvertColorMask;
import org.dreaght.flexure.mask.VectorisationMask;
import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class ApplicationLifecycle {

    @Getter
    private FlexureController flexureController;

    public void start(FlexureApplication app, Stage stage) throws IOException {

        flexureController = app.getFxmlLoader().getController();

        FileLoader.loadFile(stage);

        BufferedUtil bufferedUtil = new BufferedUtil(FileLoader.getFile());
        BufferedImage bufferedImage = bufferedUtil.convertToBuffer(800, 800);

        app.setImageLoader(new ImageLoader(app,
                BufferedUtil.createEmpty(FlexureApplication.WIDTH, FlexureApplication.HEIGHT),
                bufferedImage,
                List.of(
                        new FillMask(),
                        new GaussianMask(),
                        new VectorisationMask(),
                        new InvertColorMask()
                )));
        ImageLoader imageLoader = app.getImageLoader();

        imageLoader.drawImage(imageLoader.getScreenLayer());
        imageLoader.drawSketch();

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            imageLoader.setSketchStartX((int) ((stage.getWidth() / 2) - ((double) imageLoader.getSketch().getWidth() / 2)));
            imageLoader.setSketchStartY((int) ((stage.getHeight() / 2) - ((double) imageLoader.getSketch().getWidth() / 2)));
            imageLoader.drawSketch();
        };

        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    public void stop() {
        FlexureApplication.getInstance().getStage().close();
        System.exit(0);
    }

}
