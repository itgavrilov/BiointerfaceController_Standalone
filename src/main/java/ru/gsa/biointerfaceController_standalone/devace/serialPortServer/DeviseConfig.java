package ru.gsa.biointerfaceController_standalone.devace.serialPortServer;

import ru.gsa.biointerfaceController_standalone.devace.channel.Samples;

import java.util.List;

public interface DeviseConfig {
    int getSerialNumber();

    void setSerialNumber(int serialNumber);

    int getNumberOfChannels();

    void setNumberOfChannels(int numberOfChannels);

    List<Samples<Integer>> getSamplesList();

    boolean isAvailableSerialPort();

    void setAvailableSerialPort(boolean availableSerialPort);

    void setEnableChannels(List<Boolean> enableChannels);

    void setEnableChannel(int index, boolean enableChannel);
}
