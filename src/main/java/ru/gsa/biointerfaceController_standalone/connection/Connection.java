package ru.gsa.biointerfaceController_standalone.connection;

import ru.gsa.biointerfaceController_standalone.controllers.channel.ChannelGUI;

import java.util.Set;

public interface Connection {
    int getCountOfChannels();
    boolean isEnableChannel(int index);
    void setSamplesOfChannels(Set<ChannelGUI<Integer>> channelGUIs);
    void setEnableChannel(int index, boolean enableChannel);
    void setCapacity(int capacity);
    boolean isConnected();
    void disconnect();
    void startTransmission();
    void stopTransmission();
    boolean isTransmission();
    void reboot();
}
