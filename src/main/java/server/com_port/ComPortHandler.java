package server.com_port;

import channel.Channel;
import jssc.SerialPort;
import server.ChannelHandler;
import servo.Servo;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Пучков Константин on 12.03.2019.
 * Modified by Gavrilov Stepan on 07.11.2019.
 */
public class ComPortHandler<T> implements ChannelHandler<byte[], ComPacks, SerialPort> {
    private final List<Channel> channel;
    private final List<Servo> servo;

    public ComPortHandler(List<Channel> channel, List<Servo> servo) {
        this.channel = channel;
        this.servo = servo;
    }

    @Override
    public void channelActive(LinkedBlockingQueue<ComPacks> sendBuffer, SerialPort context) {

    }

    @Override
    public void channelReadComplete(SerialPort context) {

    }

    @Override
    public void channelRead(LinkedBlockingQueue<ComPacks> sendBuffer, byte[] message, SerialPort context) {
        switch (message[0]) {
            case 1: {
                for(int i = 0;i<channel.size();i++){
                    if (channel.get(i).paneIsActiv()) {
                        channel.get(i).add(
                                message[1 + i * 4] +
                                (message[1 + 1 + i * 4] << 8) +
                                (message[1 + 2 + i * 4] << 16) +
                                (message[1 + 3 + i * 4] << 24)
                        );
                    }
                }
            } break;
            case 2: {
                for(int i = 0; i< servo.size(); i++){
                    if(servo.get(i).isReady()) {
                        servo.get(i).setData(
                          message[1 + i * 4] + (message[1 + 1 + i * 4] << 8),
                          message[1 + 2 + i * 4] + (message[1 + 3 + i * 4] << 8)
                        );
                    }
                }
            } break;
            default:;
        }
    }
}
