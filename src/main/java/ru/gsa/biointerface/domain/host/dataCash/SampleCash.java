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
    private final DataListener listener;
    private final Deque<Integer> data = new LinkedList<>();

    public SampleCash(DataListener listener) {
        this.listener = listener;
    }

    @Override
    public void add(int val) throws DomainException {
        data.add(val);
        if (listener.isReady() && data.size() > 15) {
            listener.setNewSamples(data);
            data.clear();
        }
    }
}
