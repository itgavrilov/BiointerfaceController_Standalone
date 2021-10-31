package ru.gsa.biointerface.repository.exception;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
public class DeleteException extends RepositoryException {
    public DeleteException() {
        super("Delete error");
    }

    public DeleteException(Throwable cause) {
        super("Delete error: ", cause);
    }
}
