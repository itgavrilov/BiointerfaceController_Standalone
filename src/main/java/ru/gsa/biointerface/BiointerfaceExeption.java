package ru.gsa.biointerface;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class BiointerfaceExeption extends Exception {
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
