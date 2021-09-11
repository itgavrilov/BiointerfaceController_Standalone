package ru.gsa.biointerfaceController_standalone.businessLayer;

import ru.gsa.biointerfaceController_standalone.businessLayer.serialPortConnection.serialPortHost.DeviseConfig;

import java.util.Objects;

public class Device implements DeviseConfig, Comparable<Device> {
    private final int id;
    private final int countOfChannels;
    private String comment;

    public Device(int id, int countOfChannels, String comment) {
        if (id == 0)
            throw new IllegalArgumentException("Serial number is '0'");
        if (countOfChannels == 0)
            throw new IllegalArgumentException("Number of channels is '0'");

        this.id = id;
        this.countOfChannels = countOfChannels;
        this.comment = comment;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getCountOfChannels() {
        return countOfChannels;
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
