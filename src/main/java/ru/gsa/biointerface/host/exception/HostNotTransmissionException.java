package ru.gsa.biointerface.host.exception;

public class HostNotTransmissionException extends HostException {
    public HostNotTransmissionException() {
        super("Host is not transmission");
    }

    public HostNotTransmissionException(Throwable cause) {
        super("Host is not transmission: ", cause);
    }
}
