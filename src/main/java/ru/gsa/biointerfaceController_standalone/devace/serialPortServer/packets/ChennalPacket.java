package ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets;

import java.nio.ByteBuffer;

public class ChennalPacket extends AbstractPacket {
    public ChennalPacket(byte[] msg) {
        super(PacketType.DATA, msg);
    }

    public int getNumberInPacket() {
        return msg.length / 5;
    }

    public char getIndex(char indexInPacket) {
        return (char) msg[indexInPacket * 5];
    }

    public int getSample(char indexInPacket) {
        byte[] sample = new byte[4];

        for (int i = 4, j = 0; i > 0; i--, j++) {
            sample[j] = msg[indexInPacket * 5 + i];
        }

        return ByteBuffer.wrap(sample).getInt();
    }
}
