module com.mycompany.inventariofrontfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.base;

    opens com.mycompany.inventariofrontfx to javafx.fxml;
    exports com.mycompany.inventariofrontfx;
}
