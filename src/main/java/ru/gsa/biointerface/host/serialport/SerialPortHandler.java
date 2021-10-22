package ru.gsa.biointerface.host.serialport;

import com.fazecast.jSerialComm.SerialPort;
import ru.gsa.biointerface.host.HostException;
import ru.gsa.biointerface.services.ServiceException;
import ru.gsa.biointerface.host.serialport.packets.ChannelPacket;
import ru.gsa.biointerface.host.serialport.packets.ConfigPacket;
import ru.gsa.biointerface.host.serialport.packets.Packet;
import ru.gsa.biointerface.host.serialport.serverByPuchkov.ChannelHandler;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Пучков Константин on 12.03.2019.
 * Modified by Gavrilov Stepan on 16.08.2021.
 */
public class SerialPortHandler implements ChannelHandler<Packet, Packet, SerialPort> {
    private final DataCollector dataCollector;

    public SerialPortHandler(DataCollector dataCollector) {
        if (dataCollector == null)
            throw new NullPointerException("Devise is null");

        this.dataCollector = dataCollector;
    }

    @Override
    public void channelRead(Packet message, LinkedBlockingQueue<Packet> sendBuffer, SerialPort context) {
        switch (message.getPackageType()) {
            case CONFIG -> {
                ConfigPacket msg = (ConfigPacket) message;
                dataCollector.setDevice(msg.getSerialNumber(), msg.getAmountChannels());
            }
            case CONTROL -> {

            }
            case DATA -> {
                ChannelPacket msg = (ChannelPacket) message;

                dataCollector.setFlagTransmission();

                for (char i = 0; i < msg.getCountChannelInPacket(); i++) {
                    int scale = msg.getScale(i);
                    int simple = msg.getSample(i);

                    try {
                        dataCollector.addInCash(i, simple);
                    } catch (HostException e) {
                        e.printStackTrace();
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected packageType value: " + message.getPackageType());
        }
    }
}
