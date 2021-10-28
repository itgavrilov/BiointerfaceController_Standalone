package ru.gsa.biointerface.host.exception;

import ru.gsa.biointerface.BiointerfaceExeption;

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
