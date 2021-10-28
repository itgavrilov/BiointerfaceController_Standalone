package ru.gsa.biointerface.host.exception;


public class HostNotRunningException extends HostException {
    public HostNotRunningException() {
        super("Host is not running");
    }

    public HostNotRunningException(Throwable cause) {
        super("Host is not running: ", cause);
    }

}
