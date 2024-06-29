package org.dreaght.flexure.loader;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;

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
}
