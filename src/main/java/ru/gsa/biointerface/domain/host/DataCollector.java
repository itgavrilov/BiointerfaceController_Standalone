package ru.gsa.biointerface.domain.host;

import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.DomainException;

public interface DataCollector {
    boolean isAvailableDevice();

    void setDevice(Device devise);

    void addInCash(int i, int value) throws DomainException;

    void setFlagTransmission();
}
