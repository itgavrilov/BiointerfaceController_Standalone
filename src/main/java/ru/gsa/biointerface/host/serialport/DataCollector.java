package ru.gsa.biointerface.host.serialport;

public interface DataCollector {
    boolean isAvailableDevice();

    void setDevice(int serialNumber, int amountChannels);

    void setSampleInChannel(int i, int value);

    void setFlagTransmission();
}
