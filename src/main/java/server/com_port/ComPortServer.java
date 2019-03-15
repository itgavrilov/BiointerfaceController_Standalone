package server.com_port;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import server.AbstractServer;

import java.io.IOException;

/**
 * Created by Пучков Константин on 12.03.2019.
 */
public class ComPortServer extends AbstractServer<Integer[], ComPacks, SerialPort> implements SerialPortEventListener {
    private SerialPort serialPort;

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
        }
    }

    @Override
    protected void doStop() throws Exception {
        if(serialPort != null) {
            if (serialPort.isOpened()) {
                serialPort.setDTR(false);
                serialPort.setRTS(false);
                serialPort.closePort();
            }

            super.doStop();
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
        //System.out.println("Com buffer: "+readBuffer.size());
        if (event.isRXCHAR()) {
            if (serialPort.isOpened() && event.getEventValue() >= 26) {//check bytes count in the input buffer
                try {
                    byte[] buffer = serialPort.readBytes(26);

                    if (buffer[0] == -1 && buffer[1] == -1) {
                        Integer[] val = new Integer[5];
                        for (int i = 0; i < 5; i++) {
                            val[i] = buffer[5 + (i * 4)] +
                                    (buffer[4 + (i * 4)] << 8) +
                                    (buffer[3 + (i * 4)] << 16) +
                                    (buffer[2 + (i * 4)] << 24);
                        }
                        readBuffer.put(val);
                    }
                } catch (SerialPortException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
