package ru.gsa.biointerfaceController_standalone.businessLayer.serialPortConnection.serialPortHost;

import ru.gsa.biointerfaceController_standalone.businessLayer.Samples;

import java.util.List;

public interface DataCollector {
    boolean isAvailable();

    void setAvailableDevice(boolean available);

    void setDevice(DeviseConfig devise);

    List<Samples<Integer>> getSamplesOfChannels();
}
