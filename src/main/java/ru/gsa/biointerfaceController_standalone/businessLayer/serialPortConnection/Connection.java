package ru.gsa.biointerfaceController_standalone.businessLayer.serialPortConnection;

import ru.gsa.biointerfaceController_standalone.uiLayer.channel.Channel;

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
