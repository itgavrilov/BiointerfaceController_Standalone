package ru.gsa.biointerface.domain.host.packets;

public interface Packet {

    PacketType getPackageType();

    byte[] getMsg();

    byte[] getBytes();
}
