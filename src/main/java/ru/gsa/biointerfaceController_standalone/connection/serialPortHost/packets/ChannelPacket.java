package ru.gsa.biointerfaceController_standalone.connection.serialPortHost.packets;

import java.nio.ByteBuffer;

public class ChannelPacket extends AbstractPacket {
    public ChannelPacket(byte[] msg) {
        super(PacketType.DATA, msg);
    }

    public int getCountChannelInPacket() {
        return msg.length / 4;
    }

    public char getIndex(char indexInPacket) {
        return (char) msg[indexInPacket * 4];
    }

    public int getSample(char indexInPacket) {
        byte[] sample = new byte[4];

        for (int i = 3, j = 0; i > 0; i--, j++) {
            sample[j] = msg[indexInPacket * 4 + i];
        }

        return ByteBuffer.wrap(sample).getInt();
    }
}
