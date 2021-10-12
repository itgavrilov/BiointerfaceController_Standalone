package ru.gsa.biointerface.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class PersistenceException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceException.class);

    public PersistenceException(String message) {
        super(message);
        LOGGER.error("{}", message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
        LOGGER.error("{}:", message, cause);
    }
}
