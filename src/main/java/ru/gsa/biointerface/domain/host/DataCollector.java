package ru.gsa.biointerface.domain.host;

import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.DomainException;
import ru.gsa.biointerface.domain.Graph;
import ru.gsa.biointerface.domain.host.dataCash.Cash;

public interface DataCollector {
    boolean isAvailableDevice();

    void setDevice(Device devise);

    Cash getCash(int i) throws DomainException;
}
