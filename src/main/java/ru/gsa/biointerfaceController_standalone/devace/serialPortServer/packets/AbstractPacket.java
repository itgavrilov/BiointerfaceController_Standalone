package ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets;

public abstract class AbstractPacket implements Packet {
    private static final byte start1 = -1;
    private static final byte start2 = -1;
    protected byte[] msg;
    private PacketType packetType;

    public AbstractPacket(PacketType packetType, byte[] msg) {
        if (packetType == null)
            throw new NullPointerException("packageType is null");
        if (msg == null)
            throw new NullPointerException("msg is null");

        this.packetType = packetType;
        this.msg = msg;
    }

    public PacketType getPackageType() {
        return packetType;
    }

    public byte[] getMsg() {
        return msg;
    }

    public byte[] getBytes() {
        byte[] data = new byte[msg.length + 4];

        data[0] = start1;
        data[1] = start2;
        data[2] = packetType.getId();
        data[3] = (byte) msg.length;
        for (int i = 4, j = 0; i < data.length; i++, j++) {
            data[i] = this.msg[j];
        }

        return data;
    }
}
