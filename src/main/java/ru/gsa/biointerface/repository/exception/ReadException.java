package ru.gsa.biointerface.repository.exception;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
public class ReadException extends RepositoryException {
    public ReadException() {
        super("Read error");
    }

    public ReadException(Throwable cause) {
        super("Read error: ", cause);
    }
}
