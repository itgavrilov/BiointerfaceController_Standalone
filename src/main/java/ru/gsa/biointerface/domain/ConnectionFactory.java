package ru.gsa.biointerface.domain;

import com.fazecast.jSerialComm.SerialPort;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectionFactory {
    private static Set<ConnectionToDevice> connectionsToDevice = new LinkedHashSet<>();

    private static Stream<SerialPort> getSerialPortsWishDevises() {
        return Arrays.stream(SerialPort.getCommPorts())
                .filter(o -> "BiointerfaceController".equals(o.getPortDescription()));
    }

    public static void scanningSerialPort(PatientRecord patientRecord) {
        disconnectScanningSerialPort();
        getSerialPortsWishDevises()
                .forEach(o -> {
                    try {
                        connectionsToDevice.add(new ConnectionToDevice(patientRecord, o));
                    } catch (DomainException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static List<String> getSerialNumbers() {
        connectionsToDevice = connectionsToDevice.stream()
                .filter(ConnectionToDevice::isAvailableDevice)
                .collect(Collectors.toSet());

        return connectionsToDevice.stream()
                .map(ConnectionToDevice::getDevice)
                .map(o -> String.valueOf(o.getId()))
                .sorted()
                .collect(Collectors.toList());
    }

    public static Connection getInstance(String serialNumber) {
        return connectionsToDevice.stream()
                .peek(o -> {
                    if (!String.valueOf(o.getDevice().getId()).equals(serialNumber))
                        o.disconnect();
                })
                .filter(o -> String.valueOf(o.getDevice().getId()).equals(serialNumber))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public static void disconnectScanningSerialPort() {
        if (connectionsToDevice.size() > 0) {
            connectionsToDevice.forEach(ConnectionToDevice::disconnect);
            connectionsToDevice.clear();
        }
    }
}
