package ru.gsa.biointerface.repository.exception;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 27.10.2021.
 */
public class TransactionNotOpenException extends RepositoryException {
    public TransactionNotOpenException() {
        super("Transaction opening error");
    }

    public TransactionNotOpenException(Throwable cause) {
        super("Transaction opening error: ", cause);
    }

    public TransactionNotOpenException(String message) {
        super(message);
    }
}
