package ru.gsa.biointerface.domain.serialPortHost.dataCash;

import ru.gsa.biointerface.domain.DomainException;

import java.util.LinkedList;

/**
 * Created  by Gavrilov Stepan on 07.11.2019.
 * Class for caching input data before output.
 */

public final class SampleCash implements Cash {
    private final DataCashListener dataCashListener;
    private final LinkedList<Integer> dataInDataCash = new LinkedList<>();

    public SampleCash(DataCashListener dataCashListener) {
        this.dataCashListener = dataCashListener;
    }

    @Override
    public void add(int val) throws DomainException {
        dataInDataCash.add(val);
        if (dataInDataCash.size() >= 15) {
            dataCashListener.update(dataInDataCash);
            dataInDataCash.clear();
        }
    }
}
