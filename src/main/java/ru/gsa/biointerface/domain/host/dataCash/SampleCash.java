package ru.gsa.biointerface.domain.host.dataCash;

import ru.gsa.biointerface.domain.DataListener;
import ru.gsa.biointerface.domain.DomainException;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created  by Gavrilov Stepan on 07.11.2019.
 * Class for caching input data before output.
 */

public final class SampleCash implements Cash {
    private final DataListener dataListener;
    private final Deque<Integer> dataInDataCash = new LinkedList<>();

    public SampleCash(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    @Override
    public void add(int val) throws DomainException {
        dataInDataCash.add(val);
        if (dataInDataCash.size() > 15) {
            dataListener.setNewSamples(dataInDataCash);
            dataInDataCash.clear();
        }
    }
}
