package ru.gsa.biointerface.domain;


import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.domain.serialPortHost.dataCash.Cash;
import ru.gsa.biointerface.domain.serialPortHost.dataCash.DataCashListener;
import ru.gsa.biointerface.domain.serialPortHost.dataCash.SampleCash;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.SampleDAO;
import ru.gsa.biointerface.ui.window.channel.ChannelUpdater;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Samples implements DataCashListener {
    private int id = 0;
    private final ChannelUpdater listener;
    private final Deque<SampleEntity> sampleDeque = new LinkedList<>();
    private final Cash cash;
    private Examination examination;
    private int capacity = 1 << 10;

    public Samples(ChannelUpdater listener) {
        if (listener == null)
            throw new NullPointerException("listener is null");

        this.listener = listener;
        cash = new SampleCash(this);
    }

    public void setExamination(Examination examination) {
        this.examination = examination;
    }

    private void addAllFromCash(int y) throws DomainException {
        if(examination != null) {
            SampleEntity sampleEntity = new SampleEntity(id++, examination.getId(), listener.getId(), y);
            sampleDeque.add(sampleEntity);

            try {
                SampleDAO.getInstance().insert(sampleEntity);
            } catch (DAOException e) {
                e.printStackTrace();
                throw new DomainException("sampleEntity is null", e);
            }
        } else
            sampleDeque.add(new SampleEntity(0, -1, listener.getId(),y));

        if (sampleDeque.size() > capacity)
            sampleDeque.pollFirst();
    }

    @Override
    public void update(LinkedList<Integer> data) throws DomainException {
        for (Integer sample : data) {
            addAllFromCash(sample);
        }

        if (listener.isReady()) {
            listener.setReady(false);
            listener.update(sampleDeque.stream().map(SampleEntity::getValue).collect(Collectors.toList()));
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
            if(examination != null)
                sampleDeque.addFirst(new SampleEntity(id++, examination.getId(), listener.getId(),0));
            else
                sampleDeque.addFirst(new SampleEntity(0, -1, listener.getId(),0));
        }

        listener.setCapacity(capacity);
    }

    public void add(int val) {
        try {
            cash.add(val);
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }
}
