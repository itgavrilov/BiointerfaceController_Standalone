package ru.gsa.biointerface.domain.host;

import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.Device;

import java.util.List;

public interface DataCollector {
    boolean isAvailableDevice();

    void setDevice(Device devise);

    List<Channel> getSamplesOfChannels();
}
