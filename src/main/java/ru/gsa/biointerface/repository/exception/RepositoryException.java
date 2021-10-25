package ru.gsa.biointerface.repository.exception;

import ru.gsa.biointerface.BiointerfaceExeption;

public class RepositoryException extends BiointerfaceExeption {
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(Throwable cause) {
        super(cause);
    }

    public RepositoryException() {
    }
}
