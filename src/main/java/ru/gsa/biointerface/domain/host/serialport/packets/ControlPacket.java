package ru.gsa.biointerface.domain.host.serialport.packets;

public class ControlPacket extends AbstractPacket {
    public ControlPacket(byte[] msg) {
        super(PacketType.CONTROL, msg);
    }
}
