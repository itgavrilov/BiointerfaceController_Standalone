package ru.gsa.biointerface.domain.serialPortHost;

import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.Samples;

import java.util.List;

public interface DataCollector {
    boolean isAvailableDevice();

    void setDevice(Device devise);

    List<Samples> getSamplesOfChannels();
}
