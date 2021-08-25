package ru.gsa.biointerfaceController_standalone.connection.serialPortHost.packets;

public class ControlPacket extends AbstractPacket {
    public ControlPacket(byte[] msg) {
        super(PacketType.CONTROL, msg);
    }
}
