package ru.gsa.biointerface.domain.serialPortHost;

import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Samples;

import java.util.List;

public interface DataCollector {
    boolean isAvailable();

    void setAvailableDevice(boolean available);

    void setDevice(Device devise);

    List<Samples<Integer>> getSamplesOfChannels();
}
