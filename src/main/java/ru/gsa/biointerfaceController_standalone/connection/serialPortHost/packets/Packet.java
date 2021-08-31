package ru.gsa.biointerfaceController_standalone.connection.serialPortHost.packets;

public interface Packet {

    PacketType getPackageType();

    byte[] getMsg();

    byte[] getBytes();
}
