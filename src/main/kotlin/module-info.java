module com.example.screenmaker {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.screenmaker to javafx.fxml;
    exports com.example.screenmaker;
}