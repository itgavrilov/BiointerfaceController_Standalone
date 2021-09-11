module ru.gsa.biointerfaceController_standalone {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.management;
    requires com.fazecast.jSerialComm;
    requires org.xerial.sqlitejdbc;

    exports ru.gsa.biointerfaceController_standalone;
    exports ru.gsa.biointerfaceController_standalone.daoLayer;
    exports ru.gsa.biointerfaceController_standalone.daoLayer.dao;
    exports ru.gsa.biointerfaceController_standalone.businessLayer;
    exports ru.gsa.biointerfaceController_standalone.uiLayer.channel;
    opens ru.gsa.biointerfaceController_standalone.uiLayer to javafx.fxml;
    opens ru.gsa.biointerfaceController_standalone.uiLayer.window to javafx.fxml;
    opens ru.gsa.biointerfaceController_standalone.uiLayer.channel to javafx.fxml;
    opens ru.gsa.biointerfaceController_standalone to javafx.fxml;
}