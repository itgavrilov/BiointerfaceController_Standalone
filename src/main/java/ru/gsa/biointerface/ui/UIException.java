package ru.gsa.biointerface.ui;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
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
