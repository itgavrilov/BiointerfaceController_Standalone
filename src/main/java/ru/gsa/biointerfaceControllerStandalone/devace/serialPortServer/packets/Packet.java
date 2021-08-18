package ru.gsa.biointerfaceControllerStandalone.devace.serialPortServer.packets;

public interface Packet {

    PacketType getPackageType();

    byte[] getMsg();

    byte[] getBytes();
}
