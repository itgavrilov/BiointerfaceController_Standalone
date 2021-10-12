package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.domain.entity.ExaminationEntity;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.domain.host.dataCash.Cash;
import ru.gsa.biointerface.domain.host.dataCash.SampleCash;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.GraphDAO;
import ru.gsa.biointerface.persistence.dao.SampleDAO;

import java.util.Deque;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Graph implements DataListener, Comparable<Graph> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Graph.class);
    private final Cash cash;
    private GraphEntity entity;
    private DataListener listener;
    private int idForNewSampleEntity = 0;

    public Graph(int numberOfChannel, ExaminationEntity examinationEntity, ChannelEntity channelEntity) {
        this(new GraphEntity(numberOfChannel, examinationEntity, channelEntity));
    }

    public Graph(GraphEntity entity) {
        if (entity == null)
            throw new NullPointerException("GraphEntity is null");
        if (entity.getExaminationEntity() == null)
            throw new NullPointerException("ExaminationEntity in channelEntity is null");

        this.entity = entity;
        cash = new SampleCash(this);
    }

    public void insert() throws DomainException {
        try {
            entity = GraphDAO.getInstance().insert(entity);
            LOGGER.info("Graph '{}' is recorded in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO insert examination error");
        }
    }

    public void update() throws DomainException {
        try {
            GraphDAO.getInstance().update(entity);
            LOGGER.info("Graph '{}' is updated in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO update examination error");
        }
    }

    public GraphEntity getEntity() {
        return entity;
    }

    public void setExamination(Examination examination) {
        if (examination == null)
            throw new NullPointerException("Examination is null");

        LOGGER.info("Set Examination '{}' is graph '{}'", examination, entity);
        entity.setExaminationEntity(examination.getEntity());
        idForNewSampleEntity = 0;
    }

    public void setChannel(Channel channel) throws DomainException {
        if (channel == null)
            throw new NullPointerException("Channel is null");
        if (entity == null)
            throw new DomainException("Examination is null. First call setExamination()");

        LOGGER.info("Set channel '{}' of graph '{}'", channel, entity);
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
                } catch (PersistenceException e) {
                    throw new DomainException("SampleEntity is null", e);
                }
            }
        }
        listener.setNewSamples(data);
    }

    public void add(int val) throws DomainException {
        try {
            cash.add(val);
        } catch (DomainException e) {
            throw new DomainException("Error add new sample in cash", e);
        }
    }

    @Override
    public int compareTo(Graph o) {
        return entity.compareTo(o.entity);
    }

    @Override
    public String toString() {
        return entity.toString();
    }
}
