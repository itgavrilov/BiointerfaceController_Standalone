package ru.gsa.biointerface.domain.host.serialport.packets;

public interface Packet {

    PacketType getPackageType();

    byte[] getMsg();

    byte[] getBytes();
}
