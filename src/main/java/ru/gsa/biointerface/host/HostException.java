package ru.gsa.biointerface.host;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class HostException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(HostException.class);

    public HostException(String message) {
        super(message);
        LOGGER.error("{}:", message, this);
    }

    public HostException(String message, Throwable cause) {
        super(message, cause);
        cause.printStackTrace();
        LOGGER.error("{}:", message, this);
    }
}
