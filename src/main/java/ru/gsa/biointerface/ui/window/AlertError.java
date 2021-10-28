package ru.gsa.biointerface.ui.window;

import javafx.scene.control.Alert;

public class AlertError {

    public AlertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
