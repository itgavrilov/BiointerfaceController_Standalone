package ru.gsa.biointerfaceController_standalone.connection.serialPortHost.serverByPuchkov;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Пучков Константин on 12.03.2019.
 * Modified by Gavrilov Stepan on 16.08.2021.
 */
public interface ChannelHandler<Input, Output, Interface> {
    /**
     * Опишите логику запрос-ответ
     *
     * @param sendBuffer - буфер для отправки пакетов
     * @param message    - входящий пакет
     * @param context    - контекст для записи
     * @throws Exception
     */
    void channelRead(final Input message, final LinkedBlockingQueue<Output> sendBuffer, final Interface context) throws Exception;
}
