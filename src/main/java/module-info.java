/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
module ru.gsa.biointerface {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.management;
    requires com.fazecast.jSerialComm;
    requires org.xerial.sqlitejdbc;

    exports ru.gsa.biointerface;
    exports ru.gsa.biointerface.domain;
    exports ru.gsa.biointerface.persistence;
    exports ru.gsa.biointerface.persistence.dao;
    exports ru.gsa.biointerface.ui;
    exports ru.gsa.biointerface.ui.window;
    exports ru.gsa.biointerface.ui.window.ExaminationNew;
    opens ru.gsa.biointerface.ui to javafx.fxml;
    opens ru.gsa.biointerface.ui.window to javafx.fxml;
    opens ru.gsa.biointerface.ui.window.ExaminationNew to javafx.fxml;
    opens ru.gsa.biointerface to javafx.fxml;
    exports ru.gsa.biointerface.domain.entity;
}