package ru.gsa.biointerface.domain;

import com.fazecast.jSerialComm.SerialPort;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
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

    public static List<Device> getListDevices() {
        connectionsToDevice = connectionsToDevice.stream()
                .filter(ConnectionToDevice::isAvailableDevice)
                .collect(Collectors.toSet());

        return connectionsToDevice.stream()
                .map(ConnectionToDevice::getDevice)
                .sorted()
                .collect(Collectors.toList());
    }

    public static Connection getInstance(Device device) {
        return connectionsToDevice.stream()
                .peek(o -> {
                    if (!device.equals(o.getDevice()))
                        o.disconnect();
                })
                .filter(o -> device.equals(o.getDevice()))
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
