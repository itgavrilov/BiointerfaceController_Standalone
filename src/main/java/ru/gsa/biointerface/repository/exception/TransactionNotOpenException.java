package ru.gsa.biointerface.repository.exception;

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
