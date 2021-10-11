package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.domain.host.dataCash.Cash;
import ru.gsa.biointerface.domain.host.dataCash.SampleCash;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.GraphDAO;
import ru.gsa.biointerface.persistence.dao.SampleDAO;

import java.util.Deque;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Graph implements DataListener, Comparable<Graph> {
    private final Cash cash;
    private GraphEntity entity;
    private DataListener listener;
    private int idForNewSampleEntity = 0;

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
            throw new NullPointerException("examination is null");

        entity.setExaminationEntity(examination.getEntity());
        idForNewSampleEntity = 0;
    }

    public void setChannel(Channel channel) throws DomainException {
        if (channel == null)
            throw new NullPointerException("channel is null");
        if (entity == null)
            throw new DomainException("examination is null");

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

    public void setListener(DataListener listener) {
        if (listener == null)
            throw new NullPointerException("listener is null");

        this.listener = listener;
    }

    @Override
    public boolean isReady() {
        return listener.isReady();
    }

    @Override
    public void setNewSamples(Deque<Integer> data) throws DomainException {
        if (listener == null)
            throw new DomainException("listener is null");

        if (entity.getExaminationEntity().getId() > 0) {
            for (Integer sample : data) {
                SampleEntity sampleEntity =
                        new SampleEntity(
                                idForNewSampleEntity++,
                                entity,
                                sample
                        );
                try {
                    SampleDAO.getInstance().insert(sampleEntity);
                } catch (DAOException e) {
                    e.printStackTrace();
                    throw new DomainException("sampleEntity is null", e);
                }
            }
        }
        listener.setNewSamples(data);
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
