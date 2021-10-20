package ru.gsa.biointerface.domain.host.serialport;

import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.DomainException;

public interface DataCollector {
    boolean isAvailableDevice();

    void setDevice(int serialNumber, int amountChannels);

    void addInCash(int i, int value) throws DomainException;

    void setFlagTransmission();
}
