package ru.gsa.biointerfaceController_standalone.connection;

import ru.gsa.biointerfaceController_standalone.controllers.channel.Channel;

import java.util.Set;

public interface Connection {
    int getCountOfChannels();
    void setSamplesOfChannels(Set<Channel> channelGUIs);
    void setCapacity(int capacity);
    boolean isConnected();
    void disconnect();
    void startTransmission();
    void stopTransmission();
    boolean isTransmission();
    void reboot();
}
