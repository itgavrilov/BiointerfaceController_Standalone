package ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer;

import ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer.packets.ConfigPacket;
import ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer.packets.Packet;

import java.util.List;

public enum ConfigMessages {
    SET_ENABLE_CHANNELS();
    private final Packet data;

    ConfigMessages() {
        data = new ConfigPacket(new byte[4]);
    }

    public Packet setEnableChannels(List<Boolean> enableChannels) {
        ((ConfigPacket) data).setEnableChannels(enableChannels);
        return data;
    }
}
