package ru.gsa.biointerface.domain.entity;

import java.util.Objects;

public class DeviceEntity implements Comparable<DeviceEntity> {
    private final int id;
    private final int amountChannels;
    private String comment;

    public DeviceEntity(int id, int amountChannels, String comment) {
        this.id = id;
        this.amountChannels = amountChannels;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

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
        DeviceEntity device = (DeviceEntity) o;
        return id == device.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(DeviceEntity o) {
        return id - o.id;
    }
}
