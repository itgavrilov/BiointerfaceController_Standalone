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
    requires java.naming;
    requires java.persistence;
    requires java.compiler;
    requires org.apache.logging.log4j;
    requires com.fazecast.jSerialComm;
    requires org.hibernate.orm.core;
    requires org.hibernate.commons.annotations;
    requires net.bytebuddy;
    requires java.xml.bind;
    requires com.fasterxml.classmate;
    //requires org.hibernate.orm.jpamodelgen;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;
    exports ru.gsa.biointerface;
    exports ru.gsa.biointerface.domain.entity;
    exports ru.gsa.biointerface.host.cash;
    exports ru.gsa.biointerface.persistence;
    exports ru.gsa.biointerface.persistence.dao;
    exports ru.gsa.biointerface.ui;
    exports ru.gsa.biointerface.ui.window;
    exports ru.gsa.biointerface.ui.window.graph;
    exports ru.gsa.biointerface.ui.window.metering;
    exports ru.gsa.biointerface.ui.window.examination;

    opens ru.gsa.biointerface.ui to javafx.fxml;
    opens ru.gsa.biointerface.ui.window to javafx.fxml;
    opens ru.gsa.biointerface.ui.window.graph to javafx.fxml;
    opens ru.gsa.biointerface.ui.window.metering to javafx.fxml;
    opens ru.gsa.biointerface.ui.window.examination to javafx.fxml;
    opens ru.gsa.biointerface to javafx.fxml;
    opens ru.gsa.biointerface.domain.entity to org.hibernate.orm.core;
    exports ru.gsa.biointerface.services;
    exports ru.gsa.biointerface.host;
}