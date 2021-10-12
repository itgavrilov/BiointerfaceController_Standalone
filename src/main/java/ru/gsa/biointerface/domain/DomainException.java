package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class DomainException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainException.class);

    public DomainException(String message) {
        super(message);
        LOGGER.error("{}", message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
        LOGGER.error("{}:", message, cause);
    }
}
