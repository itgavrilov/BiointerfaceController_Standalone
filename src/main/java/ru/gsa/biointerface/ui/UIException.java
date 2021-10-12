package ru.gsa.biointerface.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class UIException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIException.class);

    public UIException(String message) {
        super(message);
        LOGGER.error("{}", message);
    }

    public UIException(String message, Throwable cause) {
        super(message, cause);
        LOGGER.error("{}:", message, cause);
    }
}
