package org.dreaght.flexure;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import lombok.Getter;
import org.dreaght.flexure.loader.FileLoader;
import org.dreaght.flexure.loader.ImageLoader;
import org.dreaght.flexure.mask.GaussianMask;
import org.dreaght.flexure.mask.ResizeMask;
import org.dreaght.flexure.mask.StretchingMask;
import org.dreaght.flexure.mask.VectorisationMask;
import org.dreaght.flexure.util.ValidatorUtil;

import java.awt.image.BufferedImage;

public class FlexureController {

    @FXML @Getter
    private ImageView imageView;
    private double imageScale = 1.0;
    private double imageOffsetX = 0;
    private double imageOffsetY = 0;
    private double dragAnchorX;
    private double dragAnchorY;

    @FXML
    private TextField radialCoefficient;
    @FXML
    private TextField centralCoefficient;
    @FXML
    private TextField radius;
    @FXML
    private TextField sigma;
    @FXML
    private Slider size;
    @FXML
    private TextField arrowLength;
    @FXML
    private RadioButton renderOption;
    @FXML
    private Button vectorStateOption;
    @FXML
    private RadioButton showInputLayer;
    @FXML
    private TextField stretch_X;
    @FXML
    private TextField stretch_Y;

    @FXML
    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        if (delta > 0) {
            imageScale *= 1.1;  // Zoom in
        } else {
            imageScale *= 0.9;  // Zoom out
        }
        imageView.setScaleX(imageScale);
        imageView.setScaleY(imageScale);
    }

    // Storing the initial mouse press coordinates
    @FXML
    private void handleMousePressed(MouseEvent event) {
        dragAnchorX = event.getSceneX() - imageOffsetX;
        dragAnchorY = event.getSceneY() - imageOffsetY;
    }

    // Dragging the image by adjusting its translation based on mouse movement
    @FXML
    private void handleMouseDragged(MouseEvent event) {
        imageOffsetX = event.getSceneX() - dragAnchorX;
        imageOffsetY = event.getSceneY() - dragAnchorY;
        imageView.setTranslateX(imageOffsetX);
        imageView.setTranslateY(imageOffsetY);
    }

    @FXML
    protected void onMouseClicked(MouseEvent event) {
        ImageLoader imageLoader = FlexureApplication.getInstance().getImageLoader();

        imageLoader.getMasks().stream().filter(mask -> mask instanceof VectorisationMask).forEach(mask ->
                ((VectorisationMask) mask).setMassCenter(new Point2D(
                        event.getX() - imageLoader.getSketchStartX(),
                        event.getY() - imageLoader.getSketchStartY()
                )));
        imageLoader.reloadMasksOnSketch();
        imageLoader.drawSketch();
    }

    @FXML
    protected void onRadialCoefficientTextFieldUpdate(KeyEvent event) {
        onVectorCoefficientUpdate(event);
    }

    @FXML
    protected void onCentralCoefficientTextFieldUpdate(KeyEvent event) {
        onVectorCoefficientUpdate(event);
    }

    private void onVectorCoefficientUpdate(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER || event.getText().isEmpty()) {
            return;
        }
        if (!ValidatorUtil.isNumbers(radialCoefficient.getText(), centralCoefficient.getText())) {
            return;
        }

        FlexureApplication.getInstance().getImageLoader().getMasks().stream().filter(mask -> mask instanceof VectorisationMask).forEach(mask -> {
            ((VectorisationMask) mask).setRadialCoefficient(getRadialCoefficient());
            ((VectorisationMask) mask).setCentralCoefficient(getCentralCoefficient());
            FlexureApplication.getInstance().getImageLoader().reloadMasksOnSketch();
            FlexureApplication.getInstance().getImageLoader().drawSketch();
        });
    }

    private double getRadialCoefficient() {
        return Double.parseDouble(radialCoefficient.getText());
    }

    private double getCentralCoefficient() {
        return Double.parseDouble(centralCoefficient.getText());
    }

    @FXML
    protected void onRadiusUpdate(KeyEvent event) {
        onGaussianCoefficientUpdate(event);
    }

    @FXML
    protected void onSigmaUpdate(KeyEvent event) {
        onGaussianCoefficientUpdate(event);
    }

    private void onGaussianCoefficientUpdate(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER || event.getText().isEmpty()) {
            return;
        }
        if (!ValidatorUtil.isNumbers(radius.getText(), sigma.getText())) {
            return;
        }

        FlexureApplication.getInstance().getImageLoader().getMasks().stream().filter(mask -> mask instanceof GaussianMask).forEach(mask -> {
            ((GaussianMask) mask).setRadius((int) Double.parseDouble(radius.getText()));
            ((GaussianMask) mask).setSigma((int) Double.parseDouble(sigma.getText()));
            FlexureApplication.getInstance().getImageLoader().reloadMasksOnSketch();
            FlexureApplication.getInstance().getImageLoader().drawSketch();
        });
    }

    @FXML
    protected void onSizeSliderMoved() {
        FlexureApplication.getInstance().getImageLoader().getMasks().stream().filter(mask -> mask instanceof ResizeMask).forEach(mask -> {
            ((ResizeMask) mask).setSizeCoefficient(size.getValue());
            FlexureApplication.getInstance().getImageLoader().reloadMasksOnSketch();
//            FlexureApplication.getInstance().getImageLoader().drawSketch();
        });

        ImageLoader imageLoader = FlexureApplication.getInstance().getImageLoader();
        Stage stage = FlexureApplication.getInstance().getStage();
        imageLoader.setSketchStartX((int) ((stage.getWidth() / 2) - ((double) imageLoader.getSketch().getWidth() / 2)));
        imageLoader.setSketchStartY((int) ((stage.getHeight() / 2) - ((double) imageLoader.getSketch().getWidth() / 2)));
        imageLoader.drawSketch();
    }

    @FXML
    protected void onGaussianVectorStateOptionUpdate() {
        vectorStateOption.setText(vectorStateOption.getText().equals("Compression") ? "Expansion" : "Compression");

        onGaussianVectorCoefficientUpdate();
    }

    @FXML
    protected void onGaussianVectorRenderOptionUpdate() {
        onGaussianVectorCoefficientUpdate();
    }

    @FXML
    protected void onGaussianVectorLengthUpdate(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER || event.getText().isEmpty()) {
            return;
        }
        onGaussianVectorCoefficientUpdate();
    }

    private void onGaussianVectorCoefficientUpdate() {
        if (!ValidatorUtil.isNumber(arrowLength.getText())) {
            return;
        }

        FlexureApplication.getInstance().getImageLoader().getMasks().stream().filter(mask -> mask instanceof GaussianMask).forEach(mask -> {
            ((GaussianMask) mask).setArrowOffsetX(vectorStateOption.getText().equals("Compression") ? 1 : -1);
            ((GaussianMask) mask).setArrowOffsetY(vectorStateOption.getText().equals("Compression") ? -1 : 1);
            ((GaussianMask) mask).setArrowLengthCoefficient(Double.parseDouble(arrowLength.getText()));
            ((GaussianMask) mask).setShouldRenderVectors(renderOption.isSelected());
        });

        reloadRenderedImage();
    }

    private void reloadRenderedImage() {
        FlexureApplication.getInstance().getImageLoader().reloadMasksOnSketch();
        FlexureApplication.getInstance().getImageLoader().drawSketch();
    }

    @FXML
    private void onExport() {
        System.out.println("Exporting the image...");
        BufferedImage finalImage = FlexureApplication.getInstance().getImageLoader().getSketch();
        FileLoader.exportFinalImage(finalImage);
    }

    @FXML
    private void onShowInputLayer() {
        FlexureApplication.getInstance().getImageLoader().setShowInputLayer(showInputLayer.isSelected());
        reloadRenderedImage();
    }

    @FXML
    private void onStretchUpdate(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER || event.getText().isEmpty()) {
            return;
        }
        if (!ValidatorUtil.isNumbers(stretch_X.getText(), stretch_Y.getText())) {
            return;
        }

        FlexureApplication.getInstance().getImageLoader().getMasks().stream().filter(mask -> mask instanceof StretchingMask).forEach(mask -> {
            ((StretchingMask) mask).setStretch_X(Double.parseDouble(stretch_X.getText()));
            ((StretchingMask) mask).setStretch_Y(Double.parseDouble(stretch_Y.getText()));
            FlexureApplication.getInstance().getImageLoader().reloadMasksOnSketch();
            FlexureApplication.getInstance().getImageLoader().drawSketch();
        });
    }
}
