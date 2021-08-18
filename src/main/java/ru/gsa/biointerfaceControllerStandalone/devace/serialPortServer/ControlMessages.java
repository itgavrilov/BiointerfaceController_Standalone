package ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer;

import ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer.packets.ControlPacket;
import ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer.packets.Packet;
import ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer.packets.PacketType;

public enum ControlMessages implements Packet {
    GET_CONFIG((byte) 0x00),
    START_TRANSMISSION((byte) 0x01),
    STOP_TRANSMISSION((byte) 0x02),
    REBOOT((byte) 0x03);

    private final Packet data;

    ControlMessages(byte data) {
        this.data = new ControlPacket(new byte[]{data});
    }

    @Override
    public PacketType getPackageType() {
        return data.getPackageType();
    }

    @Override
    public byte[] getMsg() {
        return data.getMsg();
    }

    @Override
    public byte[] getBytes() {
        return data.getBytes();
    }
}
