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
    requires java.xml.bind;
    requires net.bytebuddy;
    requires com.fasterxml.classmate;
    requires org.hibernate.orm.core;
    requires org.hibernate.commons.annotations;

    requires org.hibernate.validator;
    requires java.validation;

    requires org.slf4j;
    requires org.apache.logging.log4j;

    requires com.fazecast.jSerialComm;

    exports ru.gsa.biointerface;
    exports ru.gsa.biointerface.services;
    exports ru.gsa.biointerface.domain.entity;
    exports ru.gsa.biointerface.host;
    exports ru.gsa.biointerface.host.cash;
    exports ru.gsa.biointerface.host.exception;
    exports ru.gsa.biointerface.repository;
    exports ru.gsa.biointerface.repository.database;
    exports ru.gsa.biointerface.repository.exception;
    exports ru.gsa.biointerface.ui;
    exports ru.gsa.biointerface.ui.window;
    exports ru.gsa.biointerface.ui.window.channel;
    exports ru.gsa.biointerface.ui.window.metering;
    exports ru.gsa.biointerface.ui.window.examination;

    opens ru.gsa.biointerface.ui to javafx.fxml;
    opens ru.gsa.biointerface.ui.window to javafx.fxml;
    opens ru.gsa.biointerface.ui.window.channel to javafx.fxml;
    opens ru.gsa.biointerface.ui.window.metering to javafx.fxml;
    opens ru.gsa.biointerface.ui.window.examination to javafx.fxml;
    opens ru.gsa.biointerface to javafx.fxml;
    opens ru.gsa.biointerface.host to javafx.fxml;
    opens ru.gsa.biointerface.host.exception to javafx.fxml;
    opens ru.gsa.biointerface.domain.entity to org.hibernate.orm.core;
    exports ru.gsa.biointerface.repository.impl;
}