module makao {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens makao.view to javafx.fxml;
    opens makao.controller to javafx.fxml;
    exports makao.view;
    exports makao.controller;
}