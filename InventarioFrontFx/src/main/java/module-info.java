module com.mycompany.inventariofrontfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.base;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.mycompany.inventariofrontfx to javafx.fxml;
    exports com.mycompany.inventariofrontfx;
}
