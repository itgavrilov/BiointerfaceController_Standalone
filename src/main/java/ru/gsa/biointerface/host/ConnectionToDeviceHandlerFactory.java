package ru.gsa.biointerface.host;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ConnectionToDeviceHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionToDeviceHandlerFactory.class);
    private static ConnectionToDeviceHandlerFactory instance;
    private final List<SerialPortHostHandler> connections = new ArrayList<>();
    private SerialPortHostHandler connection;

    public static ConnectionToDeviceHandlerFactory getInstance() {
        if (instance == null) {
            instance = new ConnectionToDeviceHandlerFactory();
        }

        return instance;
    }

    public static void disconnectScanningSerialPort() {
        if (getInstance().connections.size() > 0) {
            for (HostHandler hostHandler : getInstance().connections) {
                try {
                    hostHandler.disconnect();
                } catch (Exception e) {
                    LOGGER.error("Device disconnect error", e);
                }
            }
            getInstance().connections.clear();
            LOGGER.info("disconnect all serial ports");
        }
    }

    public void scanningSerialPort() {
        if (connection != null) {
            if (connection.isConnected()) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    LOGGER.error("Device disconnect error", e);
                }
            }
            connection = null;
        }

        connections.clear();
        List<SerialPort> serialPorts = getSerialPortsWithDevises();
        for (SerialPort serialPort : serialPorts) {
            try {
                SerialPortHostHandler connection = new SerialPortHostHandler(serialPort);
                connections.add(connection);
            } catch (Exception e) {
                LOGGER.error("Error connection to serialPort(SystemPortName={})", serialPort.getSystemPortName(), e);
            }
        }
        LOGGER.info("Scanning devices");
    }

    private List<SerialPort> getSerialPortsWithDevises() {
        return Arrays.stream(SerialPort.getCommPorts())
                .filter(o -> "BiointerfaceController".equals(o.getPortDescription()))
                .collect(Collectors.toList());
    }

    public List<Device> getDevices() {
        return connections.stream()
                .peek(o -> {
                    if (o.isConnected()) {
                        try {
                            o.disconnect();
                        } catch (Exception e) {
                            LOGGER.error("Device disconnect error", e);
                        }
                    }
                })
                .filter(SerialPortHostHandler::isAvailableDevice)
                .map(SerialPortHostHandler::getDevice)
                .sorted()
                .collect(Collectors.toList());
    }

    public HostHandler getConnection(Device device) {
        connection = connections.stream()
                .filter(o -> device.equals(o.getDevice()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        LOGGER.info("Get available devices");

        return connection;
    }
}
