package ru.gsa.biointerface.repository.exception;

public class NoConnectionException extends RepositoryException {
    public NoConnectionException() {
        super("Error connection to database");
    }

    public NoConnectionException(Throwable cause) {
        super("Error connection to database: ", cause);
    }
}
