package ru.gsa.biointerface.repository.exception;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
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
