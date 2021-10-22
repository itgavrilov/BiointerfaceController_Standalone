package ru.gsa.biointerface.host;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.ui.window.metering.Connection;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class ConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);
    private static ConnectionFactory instance;
    private final List<ConnectionHandler> connections = new ArrayList<>();
    private ConnectionHandler connection;

    public static ConnectionFactory getInstance() {
        if (instance == null) {
            instance = new ConnectionFactory();
        }

        return instance;
    }

    public static void disconnectScanningSerialPort() throws HostException {
        if (getInstance().connections.size() > 0) {
            for (ConnectionHandler connectionHandler : getInstance().connections) {
                connectionHandler.disconnect();
            }
            getInstance().connections.clear();
            LOGGER.info("disconnect all serial ports");
        }
    }

    public void scanningSerialPort() throws HostException {
        if (connection != null ) {
            if(connection.isConnected()) {
                connection.disconnect();
            }
            connection = null;
        }

        connections.clear();
        List<SerialPort> serialPorts = getSerialPortsWithDevises();
        for(SerialPort serialPort: serialPorts) {
            ConnectionHandler connection = new ConnectionHandler(serialPort);
            connections.add(connection);
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
                        } catch (HostException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .filter(ConnectionHandler::isAvailableDevice)
                .map(ConnectionHandler::getDevice)
                .sorted()
                .collect(Collectors.toList());
    }

    public Connection getConnection(Device device) throws HostException {
        connection = connections.stream()
                .filter(o -> device.equals(o.getDevice()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        connection.connect();
        LOGGER.info("Get available devices");

        return connection;
    }
}
