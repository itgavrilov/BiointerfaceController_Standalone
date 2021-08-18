package ru.gsa.biointerfaceController_standalone.devace.channel;


import ru.gsa.biointerfaceController_standalone.controllers.channel.ChannelGUIUpdater;
import ru.gsa.biointerfaceController_standalone.devace.channel.dataCash.Cash;
import ru.gsa.biointerfaceController_standalone.devace.channel.dataCash.DataCashListener;
import ru.gsa.biointerfaceController_standalone.devace.channel.dataCash.SampleCash;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Samples<T extends Number> implements DataCashListener<T> {
    private final ChannelGUIUpdater<T> listener;
    private final Deque<T> sampleDeque = new LinkedList<>();
    private final Cash<T> cash;
    private int capacity = 0;

    public Samples(ChannelGUIUpdater<T> listener) {
        this.listener = listener;
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
