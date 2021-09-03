package ru.gsa.biointerfaceController_standalone.connection;

import com.fazecast.jSerialComm.SerialPort;
import ru.gsa.biointerfaceController_standalone.controllers.channel.Channel;
import ru.gsa.biointerfaceController_standalone.connection.devace.Device;
import ru.gsa.biointerfaceController_standalone.connection.devace.DeviseConfig;
import ru.gsa.biointerfaceController_standalone.connection.channel.Samples;
import ru.gsa.biointerfaceController_standalone.connection.serialPortHost.ControlMessages;
import ru.gsa.biointerfaceController_standalone.connection.serialPortHost.Handler;
import ru.gsa.biointerfaceController_standalone.connection.serialPortHost.SerialPortHost;

import java.util.*;

public class ConnectionToDevice implements DataCollector, Connection {
    private final SerialPortHost serialPortHost;
    private final List<Samples<Integer>> samplesOfChannels = new LinkedList<>();
    private Device device;
    private boolean flagTransmission = false;
    private boolean available = false;

    public ConnectionToDevice(SerialPort serialPort) {
        if (serialPort == null)
            throw new NullPointerException("serialPort is null");

        serialPortHost = new SerialPortHost(serialPort);
        serialPortHost.handler(new Handler(this));

        try {
            serialPortHost.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        serialPortHost.sendPackage(ControlMessages.GET_CONFIG);
    }

    public SerialPort getSerialPort() {
        return serialPortHost.getSerialPort();
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void setAvailableDevice(boolean available) {
        this.available = available;
    }

    @Override
    public void setDevice(DeviseConfig device) {
        this.device = (Device) device;
    }

    @Override
    public List<Samples<Integer>> getSamplesOfChannels() {
        return samplesOfChannels;
    }

    public int getCountOfChannels(){
        return device.getCountOfChannels();
    }

    @Override
    public void setSamplesOfChannels(Set<Channel> channelGUIs){
        if(channelGUIs == null)
            throw new NullPointerException("channelGUIs is null");
        if(channelGUIs.size() < getCountOfChannels())
            throw new RuntimeException("count of channelGUIs less than count of channels");

        samplesOfChannels.clear();
        channelGUIs.forEach(o -> samplesOfChannels.add(new Samples<>(o)));
        setCapacity(10);
    }

    @Override
    public void setCapacity(int capacity) {
        if (device == null)
            throw new NullPointerException("Device configuration empty");
        if (capacity == 0)
            throw new IllegalArgumentException("capacity is '0'");

        samplesOfChannels.forEach(o -> o.setCapacity(capacity));
    }

    @Override
    public boolean isConnected() {
        if (serialPortHost == null)
            throw new NullPointerException("server is not initialized (null)");

        return serialPortHost.isRunning();
    }

    @Override
    public void disconnect() {
        if (device != null)
            device = null;

        if (serialPortHost != null) {
            stopTransmission();

            try {
                serialPortHost.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startTransmission() {
        if (serialPortHost == null)
            throw new NullPointerException("server is not initialized (null)");
        if (!serialPortHost.isRunning())
            throw new RuntimeException("Server is not running");

        flagTransmission = true;
        serialPortHost.sendPackage(ControlMessages.START_TRANSMISSION);
    }

    @Override
    public void stopTransmission() {
        if (serialPortHost == null)
            throw new NullPointerException("server is not initialized (null)");

        flagTransmission = false;
        serialPortHost.sendPackage(ControlMessages.STOP_TRANSMISSION);
    }

    @Override
    public boolean isTransmission() {
        return flagTransmission;
    }

    @Override
    public void reboot() {
        if (serialPortHost == null)
            throw new NullPointerException("server is not initialized (null)");

        flagTransmission = false;
        serialPortHost.sendPackage(ControlMessages.REBOOT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionToDevice that = (ConnectionToDevice) o;
        return Objects.equals(serialPortHost, that.serialPortHost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialPortHost);
    }
}
