package ru.gsa.biointerfaceController_standalone.connection.devace;

import java.util.Objects;

public record Device(int serialNumber, int countOfChannels) implements DeviseConfig {
    public Device {
        if (serialNumber == 0)
            throw new IllegalArgumentException("Serial number is '0'");
        if (countOfChannels == 0)
            throw new IllegalArgumentException("Number of channels is '0'");
    }

    @Override
    public int getSerialNumber() {
        return serialNumber;
    }

    @Override
    public int getCountOfChannels() {
        return countOfChannels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return serialNumber == device.serialNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }
}
