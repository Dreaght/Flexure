package org.dreaght.flexure.loader;

import com.aspose.cad.Color;
import com.aspose.cad.Image;
import com.aspose.cad.imageoptions.CadRasterizationOptions;
import com.aspose.cad.imageoptions.DxfOptions;
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

        convertPngToDxf(outputFilePath, outputFilePath.replace("png", "dxf"));
    }

    private static void convertPngToDxf(String pngPath, String dxfPath) {
        try (Image image = Image.load(pngPath)) {
            DxfOptions dxfOptions = new DxfOptions() {{ setTextAsLines(true); setConvertTextBeziers(true); }};

            CadRasterizationOptions rasterizationOptions = getRasterisationOptions(800 + 300,
                    800 + 300);

            dxfOptions.setVectorRasterizationOptions(rasterizationOptions);

            image.save(dxfPath, dxfOptions);
        }
    }

    private static CadRasterizationOptions getRasterisationOptions(float width, float height) {
        CadRasterizationOptions rasterizationOptions = new CadRasterizationOptions();
        rasterizationOptions.setBackgroundColor(Color.getWhite());
        rasterizationOptions.setPageWidth(width);
        rasterizationOptions.setPageHeight(height);

        return rasterizationOptions;
    }
}
