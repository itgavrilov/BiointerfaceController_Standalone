package ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer.packets;

import java.nio.ByteBuffer;
import java.util.List;

public class ConfigPacket extends AbstractPacket {
    public ConfigPacket(byte[] msg) {
        super(PacketType.CONFIG, msg);
    }

    public int getSerialNumber() {
        byte[] serialNumber = new byte[]{0, 0, msg[1], msg[0]};

        return ByteBuffer.wrap(serialNumber).getInt();
    }

    public void setSerialNumber(int serialNumber) {
        if (serialNumber == 0 || serialNumber > 65535)
            throw new IllegalArgumentException("serialNumber out of range [1,65535]");

        msg[0] = (byte) (serialNumber >> 8);
        msg[1] = (byte) serialNumber;
    }

    public int getNumberOfChannels() {
        return msg[2];
    }

    public boolean getEnableChannels(int Index) {
        return ((msg[3] >> Index) & 1) > 0;
    }

    public void setEnableChannels(List<Boolean> enableChannels) {
        if (enableChannels == null)
            throw new NullPointerException("enableChannels is null");
        if (enableChannels.size() == 0)
            throw new IllegalArgumentException("enableChannels must be greater than {0}");
        msg[3] = 0;

        for (int i = 0; i < enableChannels.size(); i++) {
            if (enableChannels.get(i))
                msg[3] |= 1 << i;
        }
    }
}
