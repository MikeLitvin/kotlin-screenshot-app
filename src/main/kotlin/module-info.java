module com.example.screenmaker {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires kotlinx.coroutines.core.jvm;
    requires javafx.swing;
    requires org.apache.commons.io;

    opens com.example.screenmaker to javafx.fxml;
    exports com.example.screenmaker;
}