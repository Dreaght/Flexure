package org.dreaght.flexure;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.dreaght.flexure.loader.ImageLoader;

import java.io.IOException;

@Getter
public class FlexureApplication extends Application {

    @Getter
    private static FlexureApplication instance;

    public static final int WIDTH = 1940;
    public static final int HEIGHT = 1080;

    private ApplicationLifecycle applicationLifecycle;
    @Setter
    private ImageLoader imageLoader;
    private FXMLLoader fxmlLoader;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        try {
            instance = this;
            this.stage = stage;

            fxmlLoader = new FXMLLoader(FlexureApplication.class.getResource("hello-view.fxml"));

            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                System.out.println("Failed to load the scene!");
            }

            assert scene != null;
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            });

            setStageProperties(stage, scene);

            applicationLifecycle = new ApplicationLifecycle();
            applicationLifecycle.start(this, stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStageProperties(Stage stage, Scene scene) {
        stage.setOpacity(.8);

        stage.setMinWidth(800);
        stage.setMinHeight(800);

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        applicationLifecycle.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}
