package ru.gsa.biointerfaceController_standalone.businessLayer.serialPortConnection.serialPortHost.packets;

public interface Packet {

    PacketType getPackageType();

    byte[] getMsg();

    byte[] getBytes();
}
