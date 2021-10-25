package ru.gsa.biointerface.repository.exception;

public class UpdateException extends RepositoryException {
    public UpdateException() {
        super("Update error");
    }

    public UpdateException(Throwable cause) {
        super("Update error: ", cause);
    }
}
