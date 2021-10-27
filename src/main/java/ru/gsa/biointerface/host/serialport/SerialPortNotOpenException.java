package ru.gsa.biointerface.host.serialport;

public class SerialPortNotOpenException extends Exception {
    public SerialPortNotOpenException() {
        super("Serial port is not open");
    }
}
