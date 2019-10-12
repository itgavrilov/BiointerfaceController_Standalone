package server;

import util.AbstractLifeCycle;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Пучков Константин on 11.07.2018.
 */
public abstract class AbstractServer<Input, Output, Interface> extends AbstractLifeCycle implements Server<Input, Output, Interface> {
    private volatile ChannelHandler<Input, Output, Interface> handler;
    protected volatile Filter<Input> filter;
    private final LinkedBlockingQueue<Output> sendBuffer = new LinkedBlockingQueue<>();
    protected final LinkedBlockingQueue<Input> readBuffer = new LinkedBlockingQueue<>();
    private final CopyOnWriteArrayList<Server.Listener<Input>> listeners = new CopyOnWriteArrayList<>();
    private volatile int writeDelay = 0; //миллисекунды

    public int getSizeSendBuffer() {

        return sendBuffer.size();
    }

    @Override
    public Server<Input, Output, Interface> filter(Filter<Input> filter) {
        if (filter == null) {
            throw new NullPointerException("filter");
        }
        this.filter = filter;
        return this;
    }

    @Override
    public Server<Input, Output, Interface> handler(ChannelHandler<Input, Output, Interface> handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        this.handler = handler;
        return this;
    }

    @Override
    public Server<Input, Output, Interface> setWriteDelay(int delay) {
        if (delay < 0) {
            this.writeDelay = 0;
        } else {
            this.writeDelay = delay;
        }
        return this;
    }

    @Override
    public void addDataListener(Server.Listener<Input> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeDataListener(Server.Listener<Input> listener) {
        listeners.remove(listener);
    }

    @Override
    public void sendPackage(Output message) {
        if (isRunning()) {
            sendBuffer.add(message);
        }
    }

    @Override
    public void sendPackages(Collection<Output> messages) {
        if (isRunning()) {
            sendBuffer.addAll(messages);
        }
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        //Запуск потоков чтения и записи
        Thread writeThread = new WriteThread();
        writeThread.setDaemon(true);
        writeThread.setName("Server write thread");
        writeThread.start();

        Thread readThread = new ReadThread();
        readThread.setDaemon(true);
        readThread.setName("Server read thread");
        readThread.start();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        sendBuffer.clear();
        readBuffer.clear();
    }

    /**
     * Дополнительный параметр
     *
     * @return - вспомогательный интерфейс
     */
    protected abstract Interface getInterface();

    /**
     * Передача пакета
     *
     * @param message - пакет
     * @throws IOException - ошибка ввода
     */
    protected abstract void send(Output message) throws IOException;

    /**
     * Поток зписи
     */
    private class WriteThread extends Thread {
        @Override
        public void run() {
            //отправка первого пакета
            if (handler != null) {
                try {
                    handler.channelActive(sendBuffer, getInterface());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                while (isRunning()) {
                    //отправка всех пакетов из буфера в CAN
                    while (!sendBuffer.isEmpty()) {
                        try {
                            //блокирующее чтение из очереди и отправка
                            send(sendBuffer.take());
                            //если не сделать паузу, то буфер CAN переполняется или устройства не успевают обрабатывать пакеты
                            sleep(writeDelay);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }

                    //снижаем нагрузку
                    sleep(500);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();

                try {
                    AbstractServer.this.stop();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Поток чтения
     */
    private class ReadThread extends Thread {
        private Input message;

        @Override
        public void run() {
            while (isRunning()) {
                //чтение всех пакетов из шины can
                //блокирующее чтение из сокета
                try {
                    while ((message = readBuffer.take()) != null) {
                        //фильтрация пакетов
                        if (filter == null || filter.validate(message)) {
                            //передача пакета слушателям
                            for (Server.Listener<Input> listener : listeners) {
                                listener.sendPackage(message);
                            }

                            //TODO можно сделать обработку пакетов в Executor, можно будет разом обрабатывать по 10 пакетов
                            //передача пакета в хендлер
                            if (handler != null) {
                                try {

                                    handler.channelRead(sendBuffer, message, getInterface());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    if (handler != null) handler.channelReadComplete(getInterface());

                    //если recv() без блокировки, это поможет сбить нагрузку
                    sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();

                    try {
                        AbstractServer.this.stop();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
