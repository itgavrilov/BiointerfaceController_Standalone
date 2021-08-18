package ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets;

public class ControlPacket extends AbstractPacket {
    public ControlPacket(byte[] msg) {
        super(PacketType.CONTROL, msg);
    }
}
