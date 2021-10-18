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
    private static ConnectionFactory instance;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);
    private Set<ConnectionToDevice> connections = new LinkedHashSet<>();
    private ConnectionToDevice connectionsActive;

    public static ConnectionFactory getInstance(){
        if(instance==null)
            instance = new ConnectionFactory();

        return instance;
    }

    private ConnectionFactory() {

    }

    private Stream<SerialPort> getSerialPortsWishDevises() {
        LOGGER.info("Get serial ports with devices");
        return Arrays.stream(SerialPort.getCommPorts())
                .filter(o -> "BiointerfaceController".equals(o.getPortDescription()));
    }

    public void scanningSerialPort() throws DomainException {
        if(connectionsActive != null) {
            connectionsActive.disconnect();
            connectionsActive = null;
        }

        connections.clear();
        getSerialPortsWishDevises()
                .forEach(o -> {
                    try {
                        connections.add(new ConnectionToDevice(o));
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

    public Connection getConnection(Device device) throws DomainException {
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

    public static void disconnectScanningSerialPort() throws DomainException {
        if (getInstance().connections.size() > 0) {
            for (ConnectionToDevice connectionToDevice : getInstance().connections) {
                connectionToDevice.disconnect();
            }
            getInstance().connections.clear();
            LOGGER.info("disconnect all serial ports");
        }
    }
}
