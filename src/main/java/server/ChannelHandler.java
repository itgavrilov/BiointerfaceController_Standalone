package server;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Пучков Константин on 11.07.2018.
 */
public interface ChannelHandler<Input, Output, Interface> {
    /**
     * Опишите первый пакет который будет отправлен при старте
     *
     * @param sendBuffer - буфер для отправки пакетов
     * @throws Exception
     */
    void channelActive(final LinkedBlockingQueue<Output> sendBuffer, final Interface context) throws Exception;

    /**
     * Опишите окончание чтения
     * @param context - контекст для записи
     */
    void channelReadComplete(Interface context);

    /**
     * Опишите логику запрос-ответ
     *
     * @param sendBuffer - буфер для отправки пакетов
     * @param message - входящий пакет
     * @param context - контекст для записи
     * @throws Exception
     */
    void channelRead(final LinkedBlockingQueue<Output> sendBuffer, final Input message, final Interface context) throws Exception;
}
