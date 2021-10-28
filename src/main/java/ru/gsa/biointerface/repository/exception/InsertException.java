package ru.gsa.biointerface.repository.exception;

public class InsertException extends RepositoryException {
    public InsertException() {
        super("Insert error");
    }

    public InsertException(Throwable cause) {
        super("Insert error: ", cause);
    }

    public InsertException(String message) {
        super(message);
    }
}
