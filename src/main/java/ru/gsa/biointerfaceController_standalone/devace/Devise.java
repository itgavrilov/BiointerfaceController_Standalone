package ru.gsa.biointerfaceController_standalone.devace;

import com.fazecast.jSerialComm.SerialPort;
import ru.gsa.biointerfaceController_standalone.devace.channel.Samples;
import ru.gsa.biointerfaceController_standalone.controllers.channel.ChannelGUI;
import ru.gsa.biointerfaceController_standalone.controllers.channel.CheckBoxOfChannelGUI;
import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.*;
import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Devise implements DeviseConfig {
    private static Set<Devise> devises = new LinkedHashSet<>();

    private Server server;
    private int serialNumber;
    private int numberOfChannels;
    private List<Boolean> enableChannels;
    private List<Samples<Integer>> samplesList;
    private boolean flagTransmission = false;
    private boolean availableSerialPort = false;

    private Devise(SerialPort serialPort) {
        if (serialPort == null)
            throw new NullPointerException("serialPort is null");

        this.serverInitialization(serialPort);
    }

    private static Stream<SerialPort> getSerialPortsWishDevises() {
        return Arrays.stream(SerialPort.getCommPorts())
                .filter(o -> "BiointerfaceController".equals(o.getPortDescription()));
    }

    public static void scanningSerialPort() {
        devises.clear();
        getSerialPortsWishDevises()
                .forEach(o -> devises.add(new Devise(o)));
    }

    public static List<String> getSerialPortNames() {
        devises = devises.stream()
                .filter(Devise::isAvailableSerialPort)
                .peek(o -> {
                    try {
                        o.server.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .collect(Collectors.toSet());

        return devises.stream()
                .map(Devise::getSerialPort)
                .map(SerialPort::getSystemPortName)
                .sorted()
                .collect(Collectors.toList());
    }

    public static Devise getInstance(String serialPortName) {
        Devise devise = devises.stream()
                .peek(o -> {
                    try {
                        o.server.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .filter(o -> o.getSerialPort().getSystemPortName().equals(serialPortName))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        devise.serverInitialization(devise.getSerialPort());

        return devise;
    }

    private SerialPort getSerialPort() {
        return server.getSerialPort();
    }

    private void serverInitialization(SerialPort serialPort) {
        if (server != null && !server.isStopped()) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        server = new Server(serialPort);
        server.handler(new Handler(this));

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        server.sendPackage(ControlMessages.GET_CONFIG);
    }

    public void buildingGUIChannels(Set<ChannelGUI<Integer>> channelGUIs) {
        if (channelGUIs == null)
            throw new NullPointerException("channelGUIs is null");
        if (numberOfChannels == 0)
            throw new NullPointerException("Device configuration empty");

        channelGUIs.clear();

        if (samplesList == null) samplesList = new LinkedList<>();
        else samplesList.clear();

        for (char i = 0; i < numberOfChannels; i++) {
            ChannelGUI<Integer> channelGUI = new ChannelGUI<>(i);
            channelGUIs.add(channelGUI);
            samplesList.add(new Samples<>(channelGUI));
        }

        setCapacity(10);
    }

    public void buildingCheckBoxOfChannelGUI(Set<CheckBoxOfChannelGUI> checkBoxOfChannel) {
        if (checkBoxOfChannel == null)
            throw new NullPointerException("channelGUIs is null");
        if (numberOfChannels == 0 || enableChannels == null)
            throw new NullPointerException("Device configuration empty");

        checkBoxOfChannel.clear();

        for (char i = 0; i < numberOfChannels; i++) {
            checkBoxOfChannel.add(new CheckBoxOfChannelGUI(i, enableChannels.get(i)));
        }

    }

    public void setCapacity(int capacity) {
        if (samplesList.size() == 0)
            throw new NullPointerException("dataList is empty");
        if (capacity == 0)
            throw new IllegalArgumentException("capacity is null");

        samplesList.forEach(o -> o.setCapacity(capacity));
    }

    public boolean isConected() {
        if (server == null)
            throw new NullPointerException("server is not initialized (null)");

        return server.isStarting() || server.isRunning();
    }

    public void stop() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startTransmission() {
        if (server == null)
            throw new NullPointerException("server is not initialized (null)");

        flagTransmission = true;
        server.sendPackage(ControlMessages.START_TRANSMISSION);
    }

    public void stopTransmission() {
        if (server == null)
            throw new NullPointerException("server is not initialized (null)");

        flagTransmission = false;
        server.sendPackage(ControlMessages.STOP_TRANSMISSION);
    }

    public void reboot() {
        if (server == null)
            throw new NullPointerException("server is not initialized (null)");

        flagTransmission = false;
        server.sendPackage(ControlMessages.REBOOT);
    }

    public boolean isTransmission() {
        return flagTransmission;
    }

    @Override
    public int getSerialNumber() {
        return serialNumber;
    }

    @Override
    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public int getNumberOfChannels() {
        return numberOfChannels;
    }

    @Override
    public void setNumberOfChannels(int numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
    }

    @Override
    public List<Samples<Integer>> getSamplesList() {
        return samplesList;
    }

    @Override
    public boolean isAvailableSerialPort() {
        return availableSerialPort;
    }

    @Override
    public void setAvailableSerialPort(boolean availableSerialPort) {
        this.availableSerialPort = availableSerialPort;
    }

    @Override
    public void setEnableChannels(List<Boolean> enableChannels) {
        this.enableChannels = enableChannels;
    }

    @Override
    public void setEnableChannel(int index, boolean enableChannel) {
        enableChannels.set(index, enableChannel);
        server.sendPackage(ConfigMessages.SET_ENABLE_CHANNELS.setEnableChannels(enableChannels));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Devise devise = (Devise) o;
        return server.equals(devise.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server);
    }
}
