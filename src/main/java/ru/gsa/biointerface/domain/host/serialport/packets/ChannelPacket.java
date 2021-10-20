package ru.gsa.biointerface.domain.host.serialport.packets;

public class ChannelPacket extends AbstractPacket {
    public ChannelPacket(byte[] msg) {
        super(PacketType.DATA, msg);
    }

    public int getCountChannelInPacket() {
        return msg.length / 2;
    }

    // 	0 - 12 bit
    // 	1 - 14 bit >> 2
    // 	2 - 16 bit >> 4
    // 	3 - 18 bit >> 6
    // 	4 - 20 bit >> 8
    // 	5 - 22 bit >> 10
    // 	6 - 24 bit >> 12
    // 	7 - 26 bit >> 14
    // 	8 - 28 bit >> 16
    // 	9 - 30 bit >> 18
    // 10 - 32 bit >> 20
    // 11 - 34 bit >> 22
    // 12 - 36 bit >> 24
    public char getScale(char indexInPacket) {
        return (char) (msg[indexInPacket * 2] & 0xF);
    }

    public int getSample(char indexInPacket) {
        return (msg[indexInPacket * 2 + 1] << 4) | ((msg[indexInPacket * 2] >> 4) & 0x0F);
    }
}
