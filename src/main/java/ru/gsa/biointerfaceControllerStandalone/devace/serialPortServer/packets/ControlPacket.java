package ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer.packets;

public class ControlPacket extends AbstractPacket {
    public ControlPacket(byte[] msg) {
        super(PacketType.CONTROL, msg);
    }
}
