package ru.gsa.biointerface.repository.exception;

public class DeleteException extends RepositoryException {
    public DeleteException() {
        super("Delete error");
    }

    public DeleteException(Throwable cause) {
        super("Delete error: ", cause);
    }
}
