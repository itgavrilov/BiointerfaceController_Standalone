package ru.gsa.biointerface.domain;

import com.fazecast.jSerialComm.SerialPort;
import ru.gsa.biointerface.domain.entity.PatientRecord;

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

    public static List<String> getSerialPortNames() {
        connectionsToDevice = connectionsToDevice.stream()
                .filter(ConnectionToDevice::isAvailable)
                .collect(Collectors.toSet());

        return connectionsToDevice.stream()
                .map(ConnectionToDevice::getSerialPort)
                .map(SerialPort::getSystemPortName)
                .sorted()
                .collect(Collectors.toList());
    }

    public static Connection getInstance(String serialPortName) {
        return connectionsToDevice.stream()
                .peek(o -> {
                    if (!o.getSerialPort().getSystemPortName().equals(serialPortName))
                        o.disconnect();
                })
                .filter(o -> o.getSerialPort().getSystemPortName().equals(serialPortName))
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
