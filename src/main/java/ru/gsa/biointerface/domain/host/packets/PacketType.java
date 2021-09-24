package ru.gsa.biointerface.domain.host.packets;

import java.util.Arrays;

public enum PacketType {
    CONFIG(0),
    CONTROL(1),
    DATA(2);

    private final byte id;

    PacketType(int id) {
        this.id = (byte) id;
    }

    public static PacketType findById(byte id) {
        return Arrays.stream(PacketType.values())
                .filter(o -> o.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Enuma with id=" + id + " not found"));
    }

    public byte getId() {
        return id;
    }
}
