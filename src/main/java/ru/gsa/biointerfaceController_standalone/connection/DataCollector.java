package ru.gsa.biointerfaceController_standalone.connection;

import ru.gsa.biointerfaceController_standalone.connection.devace.DeviseConfig;
import ru.gsa.biointerfaceController_standalone.connection.channel.Samples;

import java.util.List;

public interface DataCollector {
    boolean isAvailable();

    void setAvailableDevice(boolean available);

    void setDevice(DeviseConfig devise);

    List<Samples<Integer>> getSamplesOfChannels();
}
