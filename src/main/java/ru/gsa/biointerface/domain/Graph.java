package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.domain.host.dataCash.Cash;
import ru.gsa.biointerface.domain.host.dataCash.DataCashListener;
import ru.gsa.biointerface.domain.host.dataCash.SampleCash;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.GraphDAO;
import ru.gsa.biointerface.persistence.dao.SampleDAO;
import ru.gsa.biointerface.ui.window.metering.GraphUpdater;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Graph implements DataCashListener, Comparable<Graph> {
    private final Deque<SampleEntity> sampleDeque = new LinkedList<>();
    private final Cash cash;
    private GraphEntity entity;
    private int idForNewSampleEntity = 0;
    private GraphUpdater listener;
    private int capacity = 1 << 10;

    public Graph(int numberOfChannel, ExaminationEntity examinationEntity, ChannelEntity channelEntity) {

        this(new GraphEntity(numberOfChannel, examinationEntity, channelEntity));
    }

    public Graph(GraphEntity entity) {
        if (entity == null)
            throw new NullPointerException("graphEntity is null");
        if (entity.getExaminationEntity() == null)
            throw new NullPointerException("examinationEntity in channelEntity is null");

        this.entity = entity;
        cash = new SampleCash(this);
    }

    public void insert() throws DomainException {
        try {
            entity = GraphDAO.getInstance().insert(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao insert examination error");
        }
    }

    public void update() throws DomainException {
        try {
            GraphDAO.getInstance().update(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao update examination error");
        }
    }

    public GraphEntity getEntity() {
        return entity;
    }

    public void setExamination(Examination examination) {
        if (examination == null)
            throw new NullPointerException("examinationEntity is null");

        entity.setExaminationEntity(examination.getEntity());
    }

    public void setChannel(Channel channel) {
        if (channel == null)
            throw new NullPointerException("channel is null");

        entity.setChannelEntity(channel.getEntity());
    }

    public int getNumberOfChannel() {
        return entity.getNumberOfChannel();
    }

    public String getName() {
        String name = "Channel " + (entity.getNumberOfChannel() + 1);

        if (entity.getChannelEntity() != null)
            name = entity.getChannelEntity().getName();

        return name;
    }

    public void setListener(GraphUpdater listener) {
        if (listener == null)
            throw new NullPointerException("listener is null");

        this.listener = listener;
    }

    private void addAllFromCash(int y) throws DomainException {
        if (entity.getExaminationEntity().getId() > 0) {
            SampleEntity sampleEntity =
                    new SampleEntity(
                            idForNewSampleEntity++,
                            entity,
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
            sampleDeque.add(new SampleEntity(0, entity, y));

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
                                entity,
                                0
                        )
                );
            else
                sampleDeque.addFirst(new SampleEntity(0, entity, 0));
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
    public int compareTo(Graph o) {
        return entity.compareTo(o.entity);
    }
}
