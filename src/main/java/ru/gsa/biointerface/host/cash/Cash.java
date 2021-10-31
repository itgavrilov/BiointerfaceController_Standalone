package ru.gsa.biointerface.host.cash;

/**
 * Created  by Gavrilov Stepan on 07.11.2019.
 * Class for caching input data before output.
 */
public interface Cash {
    void setListener(DataListener listener);

    void add(int val);
}
