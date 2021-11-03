package ru.gsa.biointerface.services;

import ru.gsa.biointerface.BiointerfaceExeption;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 03/11/2021
 */
public class ServiceException extends BiointerfaceExeption {
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException() {
    }
}
