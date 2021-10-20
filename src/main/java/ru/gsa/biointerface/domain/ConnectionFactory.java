package ru.gsa.biointerface.domain;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.ui.window.metering.Connection;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);
    private static ConnectionFactory instance;
    private Set<ConnectionToDevice> connections = new LinkedHashSet<>();
    private ConnectionToDevice connectionsActive;

    private ConnectionFactory() {

    }

    public static ConnectionFactory getInstance() {
        if (instance == null)
            instance = new ConnectionFactory();

        return instance;
    }

    public static void disconnectScanningSerialPort() throws DomainException {
        if (getInstance().connections.size() > 0) {
            for (ConnectionToDevice connectionToDevice : getInstance().connections) {
                connectionToDevice.disconnect();
            }
            getInstance().connections.clear();
            LOGGER.info("disconnect all serial ports");
        }
    }

    private Stream<SerialPort> getSerialPortsWishDevises() {
        LOGGER.info("Get serial ports with devices");
        return Arrays.stream(SerialPort.getCommPorts())
                .filter(o -> "BiointerfaceController".equals(o.getPortDescription()));
    }

    public void scanningSerialPort() throws DomainException {
        if (connectionsActive != null) {
            connectionsActive.controllerReboot();
            connectionsActive = null;
        }

        connections.clear();
        getSerialPortsWishDevises()
                .forEach(o -> {
                    try {
                        ConnectionToDevice connection = new ConnectionToDevice(o);
                        connections.add(connection);
                    } catch (DomainException e) {
                        e.printStackTrace();
                    }
                });
        LOGGER.info("Scanning devices");
    }

    public List<Device> getListDevices() {
        connections = connections.stream()
                .filter(ConnectionToDevice::isAvailableDevice)
                .collect(Collectors.toSet());
        LOGGER.info("Get available devices");
        return connections.stream()
                .map(ConnectionToDevice::getDevice)
                .sorted()
                .collect(Collectors.toList());
    }

    public Connection getConnection(Device device) {
        connectionsActive = connections.stream()
                .peek(o -> {
                    if (!device.equals(o.getDevice())) {
                        try {
                            o.disconnect();
                        } catch (DomainException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .filter(o -> device.equals(o.getDevice()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        return connectionsActive;
    }
}
