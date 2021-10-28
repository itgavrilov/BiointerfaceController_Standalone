package ru.gsa.biointerface.repository.exception;

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
