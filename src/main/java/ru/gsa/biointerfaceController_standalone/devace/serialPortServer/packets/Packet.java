package ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets;

public interface Packet {

    PacketType getPackageType();

    byte[] getMsg();

    byte[] getBytes();
}
