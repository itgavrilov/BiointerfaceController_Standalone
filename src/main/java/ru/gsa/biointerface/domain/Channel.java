package ru.gsa.biointerface.domain;


import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.domain.serialPortHost.dataCash.Cash;
import ru.gsa.biointerface.domain.serialPortHost.dataCash.DataCashListener;
import ru.gsa.biointerface.domain.serialPortHost.dataCash.SampleCash;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.ChannelDAO;
import ru.gsa.biointerface.persistence.dao.SampleDAO;
import ru.gsa.biointerface.ui.window.ExaminationNew.ChannelUpdater;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Channel implements DataCashListener, Comparable<Channel> {
    private final ChannelEntity entity;
    private final Deque<SampleEntity> sampleDeque = new LinkedList<>();
    private final Cash cash;
    private int idForNewSampleEntity = 0;
    private ChannelUpdater listener;
    private int capacity = 1 << 10;

    public Channel(ChannelEntity entity) {
        if (entity == null)
            throw new NullPointerException("channelEntity is null");
        if (entity.getExaminationEntity() == null)
            throw new NullPointerException("examinationEntity in channelEntity is null");

        this.entity = entity;
        cash = new SampleCash(this);
    }

    public void update() throws DomainException {
        try {
            ChannelDAO.getInstance().update(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao update examination error");
        }
    }

    public void setListener(ChannelUpdater listener) {
        if (listener == null)
            throw new NullPointerException("listener is null");

        this.listener = listener;
    }

    public void setExaminationEntity(ExaminationEntity examinationEntity) throws DomainException {
        if (examinationEntity == null)
            throw new NullPointerException("examinationEntity is null");

        entity.setExaminationEntity(examinationEntity);

        if (examinationEntity.getId() > 0) {
            try {
                ChannelDAO.getInstance().insert(entity);
            } catch (DAOException e) {
                e.printStackTrace();
                throw new DomainException("dao insert channel error");
            }
        }
    }

    public int getId() {
        return entity.getId();
    }

    public String getName() {
        return entity.getName();
    }

    public void setName(String name) {
        entity.setName(name);
    }

    private void addAllFromCash(int y) throws DomainException {
        if (entity.getExaminationEntity().getId() > 0) {
            SampleEntity sampleEntity =
                    new SampleEntity(
                            idForNewSampleEntity++,
                            entity.getExaminationEntity().getId(),
                            entity.getId(),
                            y
                    );
            sampleDeque.add(sampleEntity);

            try {
                SampleDAO.getInstance().insert(sampleEntity);
            } catch (DAOException e) {
                e.printStackTrace();
                throw new DomainException("sampleEntity is null", e);
            }
        } else
            sampleDeque.add(new SampleEntity(0, -1, entity.getId(), y));

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
            if (entity.getExaminationEntity().getId() > 0)
                sampleDeque.addFirst(
                        new SampleEntity(
                                idForNewSampleEntity++,
                                entity.getExaminationEntity().getId(),
                                entity.getId(),
                                0
                        )
                );
            else
                sampleDeque.addFirst(new SampleEntity(0, -1, entity.getId(), 0));
        }

        if (listener != null)
            listener.setCapacity(capacity);
    }

    public void add(int val) {
        try {
            cash.add(val);
        } catch (DomainException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(Channel o) {
        return entity.compareTo(o.entity);
    }
}
