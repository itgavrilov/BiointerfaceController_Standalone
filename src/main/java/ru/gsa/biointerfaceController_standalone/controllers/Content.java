package ru.gsa.biointerfaceController_standalone.controllers;

import javafx.fxml.Initializable;

import java.io.IOException;

public interface Content extends Initializable {
    void uploadContent(Class mainClass) throws IOException;
    void resizeWindow(double height, double width);
    String getTitle();
}
