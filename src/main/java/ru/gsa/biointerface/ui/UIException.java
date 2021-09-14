package ru.gsa.biointerface.ui;

public class UIException extends Exception {

    public UIException() {
        super();
    }

    public UIException(String message) {
        super(message);
    }

    public UIException(String message, Throwable cause) {
        super(message, cause);
    }
}
