package ru.gsa.biointerface.domain.host.packets;

public class ControlPacket extends AbstractPacket {
    public ControlPacket(byte[] msg) {
        super(PacketType.CONTROL, msg);
    }
}
