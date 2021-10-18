package ru.gsa.biointerface.domain.host.dataCash;

import ru.gsa.biointerface.domain.DataListener;
import ru.gsa.biointerface.domain.DomainException;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Created  by Gavrilov Stepan on 07.11.2019.
 * Class for caching input data before output.
 */

public final class SampleCash implements Cash {
    private final List<DataListener> listeners = new LinkedList<>();
    private final Deque<Integer> data = new LinkedList<>();

    public void addListener(DataListener listener) {
        listeners.add(listener);
    }

    @Override
    public void add(int val) {
        data.add(val);
        if (data.size() > 15) {
            for(DataListener listener:listeners) {
                try {
                    listener.setNewSamples(data);
                } catch (DomainException e) {
                    e.printStackTrace();
                }
            }
            data.clear();
        }
    }
}
