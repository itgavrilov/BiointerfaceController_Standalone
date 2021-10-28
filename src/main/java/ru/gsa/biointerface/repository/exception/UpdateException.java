package ru.gsa.biointerface.repository.exception;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
public class UpdateException extends RepositoryException {
    public UpdateException() {
        super("Update error");
    }

    public UpdateException(Throwable cause) {
        super("Update error: ", cause);
    }
}
