package ru.gsa.biointerface.repository.exception;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
public class TransactionStopException extends RepositoryException {
    public TransactionStopException() {
        super("Error closing transaction");
    }

    public TransactionStopException(Throwable cause) {
        super("Error closing transaction: ", cause);
    }

    public TransactionStopException(String message) {
        super(message);
    }
}
