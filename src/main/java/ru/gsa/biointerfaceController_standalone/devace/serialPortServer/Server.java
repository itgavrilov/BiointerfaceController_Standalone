package ru.gsa.biointerfaceController_standalone.devace.serialPortServer;

import com.fazecast.jSerialComm.*;
import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets.*;
import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.serverByPuchkov.AbstractServer;
import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets.*;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Пучков Константин on 12.03.2019.
 * Modified by Gavrilov Stepan on 16.08.2021.
 */
public class Server extends AbstractServer<Packet, Packet, SerialPort> implements SerialPortDataListener {
    private final SerialPort serialPort;

    public Server(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    @Override
    protected void doStart() {
        if (serialPort == null)
            throw new NullPointerException("serialPort is null");

        try {
            super.doStart();
        } catch (Exception e) {
            e.printStackTrace();
        }

        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setNumDataBits(8);
        serialPort.setBaudRate(256000);
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);

        serialPort.openPort();
        serialPort.addDataListener(this);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (serialPort != null && serialPort.isOpen())
            serialPort.closePort();
    }

    @Override
    protected SerialPort getInterface() {
        return serialPort;
    }

    @Override
    protected void send(Packet message) {
        if (serialPort == null)
            throw new NullPointerException("serialPort is lost or null");

        if (!serialPort.isOpen())
            serialPort.openPort();

        serialPort.writeBytes(message.getBytes(), message.getBytes().length);
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;

        while (serialPort.isOpen() && serialPort.bytesAvailable() > 0) {
            int bytesAvailable = serialPort.bytesAvailable();
            byte[] tmp = new byte[bytesAvailable];
            serialPort.readBytes(tmp, bytesAvailable);

            while (tmp.length > 0) {
                if (tmp[0] == -1 && tmp[1] == -1) {
                    PacketType packetType = PacketType.findById(tmp[2]);
                    Packet packet;
                    int endOfPackage = tmp[3] + 4;
                    byte[] msg = Arrays.copyOfRange(tmp, 4, endOfPackage);
                    tmp = Arrays.copyOfRange(tmp, endOfPackage, tmp.length);

                    switch (packetType) {
                        case CONFIG -> packet = new ConfigPacket(msg);
                        case CONTROL -> packet = new ControlPacket(msg);
                        case DATA -> packet = new ChennalPacket(msg);
                        default -> throw new IllegalStateException("Unexpected value: " + packetType);
                    }
                    try {
                        readBuffer.put(packet);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else tmp = Arrays.copyOfRange(tmp, 1, tmp.length);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return serialPort.equals(server.serialPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialPort);
    }
}


