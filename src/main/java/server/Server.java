package server;

import java.util.Collection;
import java.util.EventListener;

/**
 * Created by Пучков Константин on 11.07.2018.
 */
public interface Server<Input, Output, Interface> {
    /**
     * Запуск сервера
     */
    void start() throws Exception;

    /**
     * Остановка сервера
     */
    void stop() throws Exception;

    /**
     * Фильтрует приходящие пакеты
     * {@link Filter} используется для фильтрации пакетов.
     *
     * @param filter - фильтер
     * @return - объект сервера
     */
    Server<Input, Output, Interface> filter(Filter<Input> filter);

    /**
     * Обработчик приходящих пакетов
     * {@link ChannelHandler} используется для обработки запросов.
     *
     * @param handler - обработчик
     * @return - объект сервера
     */
    Server<Input, Output, Interface> handler(ChannelHandler<Input, Output, Interface> handler);

    /**
     * Устанавливает задержку для добавления пакетов в буффур
     *
     * @param delay - задержка в миллисекундах
     * @return - объект сервера
     */
    Server<Input, Output, Interface> setWriteDelay(int delay);

    /**
     * Добавляет слушателя
     * {@link Listener} используется для уведомления слушателя.
     *
     * @param listener - слушатель
     */
    void addDataListener(Listener<Input> listener);

    /**
     * Удаляет слушателя
     * {@link Listener} используется для уведомления слушателя.
     *
     * @param listener - слушатель
     */
    void removeDataListener(Listener<Input> listener);

    void sendPackage(Output pack);

    void sendPackages(Collection<Output> packs);

    interface Listener<Input> extends EventListener {
        /**
         * Отправляет пакет слушателю
         *
         * @param pack пакет
         * @return true - отправка прошла успешно, false иначе
         */
        boolean sendPackage(final Input pack);

        /**
         * Отправляет коллекцию пакетов слушателю
         *
         * @param packs - коллекция пакетов
         * @return true - отправка прошла успешно, false иначе
         */
        boolean sendPackages(final Collection<Input> packs);
    }
}
