package ru.gsa.biointerfaceController_standalone.devace.serialPortServer.serverByPuchkov;

/**
 * Created by Пучков Константин on 11.07.2018.
 */
public interface Filter<Package> {
    /**
     * Проверяет валидность пакета
     *
     * @param message - пакет
     * @return true - валидин, false в противном случае
     */
    boolean validate(Package message);
}
