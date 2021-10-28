package ru.gsa.biointerface.host.exception;

import ru.gsa.biointerface.BiointerfaceExeption;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
public class HostException extends BiointerfaceExeption {
    public HostException(String message, Throwable cause) {
        super(message, cause);
    }

    public HostException(String message) {
        super(message);
    }

    public HostException(Throwable cause) {
        super(cause);
    }

    public HostException() {
    }
}
