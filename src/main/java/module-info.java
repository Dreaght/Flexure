module org.dreaght.flexure {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;
    requires aspose.cad;


    opens org.dreaght.flexure to javafx.fxml;
    exports org.dreaght.flexure;
    exports org.dreaght.flexure.loader;
    exports org.dreaght.flexure.mask;
    opens org.dreaght.flexure.loader to javafx.fxml;
}