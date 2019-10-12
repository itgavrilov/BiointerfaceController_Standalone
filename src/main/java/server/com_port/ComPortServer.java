package server.com_port;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import server.AbstractServer;

import java.io.IOException;

import static jssc.SerialPort.PURGE_RXCLEAR;
import static jssc.SerialPort.PURGE_TXCLEAR;

/**
 * Created by Пучков Константин on 12.03.2019.
 */
public class ComPortServer extends AbstractServer<Integer[], ComPacks, SerialPort> implements SerialPortEventListener {
    private final SerialPort serialPort;

    public ComPortServer(String name) {
        serialPort = new SerialPort(name);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (serialPort.openPort() &&
                serialPort.setParams(SerialPort.BAUDRATE_256000, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, true, false) &&//Выставляем параметры
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT) //Включаем аппаратное управление потоком
        ) {
            serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
        } else {
            stop();
        }
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if(serialPort != null) {
            if (serialPort.isOpened()) {
                serialPort.setDTR(false);
                serialPort.setRTS(false);
                serialPort.closePort();
            }
        }
    }

    @Override
    protected SerialPort getInterface() {
        return serialPort;
    }

    @Override
    protected void send(ComPacks message) throws IOException {
        try {
            if (serialPort.isOpened()) {
                serialPort.setDTR(message.getDTR());
                serialPort.setRTS(message.getRTS());
                serialPort.writeBytes(message.getData());
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) {
            try {
                while (serialPort.isOpened() && event.getEventValue() >= 22) {//check bytes count in the input buffer
                     if(serialPort.readBytes(1)[0] == -1 && serialPort.readBytes(1)[0] == -1 ) {
                        byte[] buffer = serialPort.readBytes(20);
                        if(serialPort.isOpened()) {
                            Integer[] val = new Integer[5];
                            for (int i = 0; i < val.length; i++) {
                                val[i] = buffer[3 + (i * 4) ] +
                                        (buffer[2 + (i * 4)] << 8) +
                                        (buffer[1 + (i * 4)] << 16) +
                                        (buffer[i * 4] << 24);
                            }
                            readBuffer.put(val);
                        }
                    }
                }
            } catch (SerialPortException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
