package ru.gsa.biointerface.domain.serialPortHost;

import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.Device;

import java.util.List;

public interface DataCollector {
    boolean isAvailableDevice();

    void setDevice(Device devise);

    List<Channel> getSamplesOfChannels();
}
