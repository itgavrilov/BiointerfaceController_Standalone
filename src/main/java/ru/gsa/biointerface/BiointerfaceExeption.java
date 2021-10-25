package ru.gsa.biointerface;

public class BiointerfaceExeption extends Exception{
    public BiointerfaceExeption(String message, Throwable cause) {
        super(message, cause);
    }

    public BiointerfaceExeption(Throwable cause) {
        super(cause);
    }

    public BiointerfaceExeption(String message) {
        super(message);
    }

    public BiointerfaceExeption() {
    }
}
