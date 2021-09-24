package ru.gsa.biointerface.domain.host.packets;

import java.nio.ByteBuffer;

public class ConfigPacket extends AbstractPacket {
    public ConfigPacket(byte[] msg) {
        super(PacketType.CONFIG, msg);
    }

    public int getSerialNumber() {
        byte[] serialNumber = new byte[]{0, 0, msg[1], msg[0]};

        return ByteBuffer.wrap(serialNumber).getInt();
    }

    public int getCountOfChannels() {
        return msg[2];
    }
}
