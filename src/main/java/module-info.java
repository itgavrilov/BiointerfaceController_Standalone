module ru.gsa.biointerfaceController_standalone {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fazecast.jSerialComm;
    requires java.management;

    opens ru.gsa.biointerfaceController_standalone.controllers to javafx.fxml;
    opens ru.gsa.biointerfaceController_standalone.controllers.channel to javafx.fxml;
    exports ru.gsa.biointerfaceController_standalone;
}