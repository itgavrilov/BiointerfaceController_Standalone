package ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer;

import ru.gsa.biointerfaceControllerStandalone.devace.channel.Samples;

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
