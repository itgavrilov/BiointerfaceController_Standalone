package ru.gsa.biointerface.repository.exception;

public class ReadException extends RepositoryException {
    public ReadException() {
        super("Read error");
    }

    public ReadException(Throwable cause) {
        super("Read error: ", cause);
    }
}
