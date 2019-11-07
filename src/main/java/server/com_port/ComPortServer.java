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
public class ComPortServer extends AbstractServer<int[], ComPacks, SerialPort> implements SerialPortEventListener {
    private final SerialPort serialPort;

    public ComPortServer(String name) {
        serialPort = new SerialPort(name);
    }

    @Override
    protected void doStart() {
        try {
            super.doStart();
            if (serialPort != null &&
                    serialPort.openPort() &&
                    serialPort.setParams(SerialPort.BAUDRATE_256000, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, true, false) &&//Выставляем параметры
                    serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT) //Включаем аппаратное управление потоком
            ) {
                serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doStop() throws Exception {
        try {
            super.doStop();
            if(serialPort != null && serialPort.isOpened()) {
                serialPort.setDTR(false);
                serialPort.setRTS(false);
                serialPort.closePort();
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected SerialPort getInterface() {
        return serialPort;
    }

    @Override
    protected void send(ComPacks message) {
        try {
            if (serialPort != null && serialPort.isOpened()) {
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
                while (serialPort.isOpened() && event.getEventValue()>0) {
                     if(serialPort.readBytes(1)[0] == -1 && serialPort.readBytes(1)[0] == -1 ) {
                        int lenMassage = serialPort.readBytes(1)[0];
                        int message[] = serialPort.readIntArray(lenMassage);

                        if(serialPort.isOpened()) {
                            readBuffer.put(message);
                        }
                    }
                }
            } catch (SerialPortException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
