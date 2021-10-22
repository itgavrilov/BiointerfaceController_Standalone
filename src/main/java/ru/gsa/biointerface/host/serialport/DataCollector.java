package ru.gsa.biointerface.host.serialport;

import ru.gsa.biointerface.host.HostException;
import ru.gsa.biointerface.services.ServiceException;

public interface DataCollector {
    boolean isAvailableDevice();

    void setDevice(int serialNumber, int amountChannels);

    void addInCash(int i, int value) throws HostException;

    void setFlagTransmission();
}
