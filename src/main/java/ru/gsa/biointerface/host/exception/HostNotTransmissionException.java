package ru.gsa.biointerface.host.exception;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
public class HostNotTransmissionException extends HostException {
    public HostNotTransmissionException() {
        super("Host is not transmission");
    }

    public HostNotTransmissionException(Throwable cause) {
        super("Host is not transmission: ", cause);
    }
}
