package ru.gsa.biointerface.repository.exception;

import ru.gsa.biointerface.BiointerfaceExeption;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
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
