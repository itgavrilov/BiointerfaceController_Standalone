package ru.gsa.biointerface.repository.exception;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
public class NoConnectionException extends RepositoryException {
    public NoConnectionException() {
        super("Error connection to database");
    }

    public NoConnectionException(Throwable cause) {
        super("Error connection to database: ", cause);
    }
}
