package ru.gsa.biointerface.domain.entity;


import ru.gsa.biointerface.domain.serialPortHost.dataCash.Cash;
import ru.gsa.biointerface.domain.serialPortHost.dataCash.DataCashListener;
import ru.gsa.biointerface.domain.serialPortHost.dataCash.SampleCash;
import ru.gsa.biointerface.ui.window.channel.ChannelUpdater;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Samples<T extends Number> implements DataCashListener<T> {
    private final int id;
    private final ChannelUpdater<T> listener;
    private final Deque<T> sampleDeque = new LinkedList<>();
    private final Cash<T> cash;
    private int capacity = 0;

    public Samples(ChannelUpdater<T> listener) {
        if (listener == null)
            throw new NullPointerException("listener is null");

        this.listener = listener;
        id = listener.getId();
        cash = new SampleCash<>(this);
    }

    private void addAllInCash(T y) {
        if (capacity == 0)
            throw new IllegalStateException("capacity uninitialized");

        sampleDeque.add(y);
        if (sampleDeque.size() > capacity)
            sampleDeque.pollFirst();
    }

    @Override
    public void update(LinkedList<T> data) {
        data.forEach(this::addAllInCash);
        if (listener.isReady()) {
            listener.setReady(false);
            listener.update(new ArrayList<>(sampleDeque));
        }
    }

    public void setCapacity(int capacity) {
        if (capacity < 7)
            capacity = 7;

        this.capacity = 1 << capacity;
        if (sampleDeque.size() > this.capacity) {
            while (sampleDeque.size() > this.capacity) {
                sampleDeque.poll();
            }
        } else while (sampleDeque.size() < this.capacity) {
            sampleDeque.addFirst((T) Integer.valueOf(0));
        }

        listener.setCapacity(capacity);
    }

    public void add(T val) {
        cash.add(val);
    }
}
