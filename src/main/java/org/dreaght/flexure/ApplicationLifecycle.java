package org.dreaght.flexure;

import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Getter;
import org.dreaght.flexure.loader.FileLoader;
import org.dreaght.flexure.loader.ImageLoader;
import org.dreaght.flexure.mask.*;
import org.dreaght.flexure.util.buffer.BufferedUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class ApplicationLifecycle {

    @Getter
    private FlexureController flexureController;

    public void start(FlexureApplication app, Stage stage) {

        flexureController = app.getFxmlLoader().getController();

        FileLoader.loadFile(stage);

        BufferedUtil bufferedUtil = new BufferedUtil(FileLoader.getFile());

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = bufferedUtil.convertToBuffer(800, 800);
        } catch (IOException e) {
            System.out.println("Failed to convert image to buffer!");
        }

        assert bufferedImage != null;

        ImageLoader imageLoaderTemp = new ImageLoader(app,
                BufferedUtil.createEmpty(FlexureApplication.WIDTH, FlexureApplication.HEIGHT),
                bufferedImage,
                List.of(
                        new FillMask(),
                        new ResizeMask(),
                        new InvertColorMask(),
                        new VectorisationMask(),
                        new GaussianMask(),
                        new RemoveNonWhite(),
                        new GaussianMask(),
                        new GoodbyeHolesMask(),
                        new BorderMask(new Color(144, 238, 144)),
                        new RemoveEverythingExceptMask(new Color(144, 238, 144)),
                        new StretchingMask(),
                        new FillMask(List.of(
                                new Pair<>(Color.BLACK, Color.YELLOW),
                                new Pair<>(Color.BLACK, new Color(144, 238, 144)),
                                new Pair<>(Color.YELLOW, Color.BLACK)
                        ))
                ));

        app.setImageLoader(imageLoaderTemp);
        ImageLoader imageLoader = app.getImageLoader();

        ((FlexureController) app.getFxmlLoader().getController()).onSizeSliderMoved();

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
