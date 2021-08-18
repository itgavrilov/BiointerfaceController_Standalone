package ru.gsa.biointerfaceController_standalone.devace.serialPortServer;

import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets.ConfigPacket;
import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets.Packet;

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
