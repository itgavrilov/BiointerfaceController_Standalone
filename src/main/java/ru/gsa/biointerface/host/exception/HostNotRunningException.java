package ru.gsa.biointerface.host.exception;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
public class HostNotRunningException extends HostException {
    public HostNotRunningException() {
        super("Host is not running");
    }

    public HostNotRunningException(Throwable cause) {
        super("Host is not running: ", cause);
    }

}
