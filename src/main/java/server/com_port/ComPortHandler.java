package server.com_port;

import programms.ChannelChart;
import jssc.SerialPort;
import server.ChannelHandler;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Пучков Константин on 12.03.2019.
 */
public class ComPortHandler<T> implements ChannelHandler<Integer[], ComPacks, SerialPort> {
    private List<ChannelChart> channelCharts;

    public ComPortHandler(List<ChannelChart> channelCharts) {
        this.channelCharts = channelCharts;
    }

    @Override
    public void channelActive(LinkedBlockingQueue<ComPacks> sendBuffer, SerialPort context) throws Exception {

    }

    @Override
    public void channelReadComplete(SerialPort context) {

    }

    @Override
    public void channelRead(LinkedBlockingQueue<ComPacks> sendBuffer, Integer[] message, SerialPort context) throws Exception {
        int i=0;
        for(ChannelChart o: channelCharts){
            if(o.paneIsActiv()) {
                //o.dataCash.add(val[i]-2048);
                o.dataCash.add(message[i]);
            }
            i++;
        }
    }
}
