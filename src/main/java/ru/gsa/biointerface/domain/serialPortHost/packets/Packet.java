package ru.gsa.biointerface.domain.serialPortHost.packets;

public interface Packet {

    PacketType getPackageType();

    byte[] getMsg();

    byte[] getBytes();
}
