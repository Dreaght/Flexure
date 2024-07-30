package org.dreaght.flexure.loader;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileLoader {
    @Getter private static File file;

    public static void loadFile(Stage stageWhereToOpenDialog) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("DXF/PNG Files", "*.dxf", "*.png")
        );

        file = fileChooser.showOpenDialog(stageWhereToOpenDialog);

        if (file == null) {
            System.out.println("You've no file chosen, exiting the program...");
            stageWhereToOpenDialog.close();
            System.exit(0);
        }
    }

    public static void exportFinalImage(BufferedImage bufferedImage) {
        String outputFilePath = file.getParent()
                + "/" + "FXout/output_"
                + file.getName().replace("dxf", "png");

        try {
            File outPutFile = new File(outputFilePath);
            outPutFile.mkdirs();

            ImageIO.write(bufferedImage, "png", outPutFile);
        } catch (IOException e) {
            System.out.println("Failed to save an output image to path: " + outputFilePath);
            e.printStackTrace();
        }
    }
}
