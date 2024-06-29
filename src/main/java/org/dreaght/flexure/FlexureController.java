package org.dreaght.flexure;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import org.dreaght.flexure.loader.ImageLoader;
import org.dreaght.flexure.mask.GaussianMask;
import org.dreaght.flexure.mask.VectorisationMask;
import org.dreaght.flexure.util.ValidatorUtil;

public class FlexureController {

    @FXML @Getter
    private ImageView imageView;

    @FXML
    private TextField radialCoefficient;

    @FXML
    private TextField centralCoefficient;

    @FXML
    private TextField radius;

    @FXML
    private TextField sigma;

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

        System.out.println("onGaussianCoefficientUpdate");
    }

    public double getRadialCoefficient() {
        return Double.parseDouble(radialCoefficient.getText());
    }

    public double getCentralCoefficient() {
        return Double.parseDouble(centralCoefficient.getText());
    }
}
