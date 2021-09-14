package ru.gsa.biointerface.domain.entity;

import ru.gsa.biointerface.domain.serialPortHost.DeviceConfig;

import java.util.Objects;

public class Device implements DeviceConfig, Comparable<Device> {
    private final int id;
    private final int amountChannels;
    private String comment;

    public Device(int id, int amountChannels, String comment) {
        if (id == 0)
            throw new IllegalArgumentException("Serial number is '0'");
        if (amountChannels == 0)
            throw new IllegalArgumentException("Amount channels is '0'");

        this.id = id;
        this.amountChannels = amountChannels;
        this.comment = comment;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getAmountChannels() {
        return amountChannels;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return id == device.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Device o) {
        return id - o.id;
    }
}
