package ru.gsa.biointerface.domain;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);
    private static Set<ConnectionToDevice> connectionsToDevice = new LinkedHashSet<>();

    private static Stream<SerialPort> getSerialPortsWishDevises() {
        LOGGER.info("Get serial ports with devices");
        return Arrays.stream(SerialPort.getCommPorts())
                .filter(o -> "BiointerfaceController".equals(o.getPortDescription()));
    }

    public static void scanningSerialPort(PatientRecord patientRecord) throws DomainException {
        disconnectScanningSerialPort();

        getSerialPortsWishDevises()
                .forEach(o -> {
                    try {
                        connectionsToDevice.add(new ConnectionToDevice(patientRecord, o));
                    } catch (DomainException e) {
                        e.printStackTrace();
                    }
                });
        LOGGER.info("Scanning devices");
    }

    public static List<Device> getListDevices() {
        connectionsToDevice = connectionsToDevice.stream()
                .filter(ConnectionToDevice::isAvailableDevice)
                .collect(Collectors.toSet());
        LOGGER.info("Get available devices");
        return connectionsToDevice.stream()
                .map(ConnectionToDevice::getDevice)
                .sorted()
                .collect(Collectors.toList());
    }

    public static Connection getInstance(Device device) {
        return connectionsToDevice.stream()
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
    }

    public static void disconnectScanningSerialPort() throws DomainException {
        if (connectionsToDevice.size() > 0) {
            for (ConnectionToDevice connectionToDevice : connectionsToDevice) {
                connectionToDevice.disconnect();
            }
            connectionsToDevice.clear();
            LOGGER.info("disconnect all serial ports");
        }
    }
}
