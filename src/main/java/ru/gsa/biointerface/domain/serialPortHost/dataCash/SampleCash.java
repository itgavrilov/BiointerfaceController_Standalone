package ru.gsa.biointerface.domain.serialPortHost.dataCash;

import java.util.LinkedList;

/**
 * Created  by Gavrilov Stepan on 07.11.2019.
 * Class for caching input data before output.
 */

public final class SampleCash<T extends Number> implements Cash<T> {
    private final DataCashListener<T> dataCashListener;
    private final LinkedList<T> dataInDataCash = new LinkedList<>();

    public SampleCash(DataCashListener<T> dataCashListener) {
        this.dataCashListener = dataCashListener;
    }

    @Override
    public void add(T val) {
        dataInDataCash.add(val);
        if (dataInDataCash.size() >= 15) {
            dataCashListener.update(dataInDataCash);
            dataInDataCash.clear();
        }
    }
}
