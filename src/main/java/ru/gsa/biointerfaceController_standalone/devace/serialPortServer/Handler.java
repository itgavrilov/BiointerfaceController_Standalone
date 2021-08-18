package ru.gsa.biointerfaceController_standalone.devace.serialPortServer;

import com.fazecast.jSerialComm.SerialPort;
import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets.ChennalPacket;
import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets.ConfigPacket;
import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.packets.Packet;
import ru.gsa.biointerfaceController_standalone.devace.serialPortServer.serverByPuchkov.ChannelHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Пучков Константин on 12.03.2019.
 * Modified by Gavrilov Stepan on 16.08.2021.
 */
public class Handler implements ChannelHandler<Packet, Packet, SerialPort> {
    private DeviseConfig devise;

    public Handler(DeviseConfig devise) {
        this.devise = devise;
    }

    @Override
    public void channelReadComplete(SerialPort context) {

    }

    @Override
    public void channelRead(Packet message, LinkedBlockingQueue<Packet> sendBuffer, SerialPort context) {
        if (devise == null)
            throw new NullPointerException("devise is lost or null");

        devise.setAvailableSerialPort(true);

        switch (message.getPackageType()) {
            case CONFIG -> {
                ConfigPacket msg = (ConfigPacket) message;
                devise.setSerialNumber(msg.getSerialNumber());
                devise.setNumberOfChannels(msg.getNumberOfChannels());

                List<Boolean> enableChannels = new LinkedList<>();
                for (int i = 0; i < devise.getNumberOfChannels(); i++) {
                    enableChannels.add(msg.getEnableChannels(i));
                }

                devise.setEnableChannels(enableChannels);
            }
            case CONTROL -> {

            }
            case DATA -> {
                if (devise.getSamplesList() != null) {
                    ChennalPacket msg = (ChennalPacket) message;
                    for (char i = 0; i < msg.getNumberInPacket(); i++) {
                        if (devise.getSamplesList().get(msg.getIndex(i)) != null)
                            devise.getSamplesList().get(msg.getIndex(i)).add(msg.getSample(i));
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected packageType value: " + message.getPackageType());
        }
    }
}
