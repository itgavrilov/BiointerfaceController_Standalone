package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.GraphEntity;
import ru.gsa.biointerface.domain.entity.SampleEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.GraphDAO;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 07.11.2019.
 */
public class Graph implements DataListener, Comparable<Graph>{
    private static final Logger LOGGER = LoggerFactory.getLogger(Graph.class);
    private final GraphEntity entity;

    public Graph(int numberOfChannel) {
        if (numberOfChannel < 0)
            throw new NullPointerException("numberOfChannel is less than 0");

        entity = new GraphEntity(numberOfChannel, null,null, new LinkedList<>());
    }

    public Graph(GraphEntity entity) {
        if (entity == null)
            throw new NullPointerException("GraphEntity is null");
        if (entity.getExaminationEntity() == null)
            throw new NullPointerException("ExaminationEntity in GraphEntity is null");

        this.entity = entity;
    }

    public void update() throws DomainException {
        try {
            GraphDAO.getInstance().update(entity);
            LOGGER.info("{} is updated in database", entity);
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

        LOGGER.info("Set {} in {}", examination, entity);
        entity.setExaminationEntity(examination.getEntity());
    }

    public void setChannel(Channel channel) throws DomainException {
        if (channel == null)
            throw new NullPointerException("Channel is null");
        if (entity == null)
            throw new DomainException("Examination is null. First call setExamination()");

        LOGGER.info("Set {} in {}", channel, entity);
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

    @Override
    public void setNewSamples(Deque<Integer> data) {
        for (Integer sample : data) {
            SampleEntity sampleEntity =
                    new SampleEntity(
                            entity.getSampleEntities().size(),
                            entity,
                            sample
                    );
            entity.getSampleEntities().add(sampleEntity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return Objects.equals(entity, graph.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }

    @Override
    public int compareTo(Graph o) {
        long result = entity.getExaminationEntity().getId() - o.entity.getExaminationEntity().getId();

        if(result == 0)
            result = entity.getNumberOfChannel() - o.entity.getNumberOfChannel();

        return (int) result;
    }

    @Override
    public String toString() {
        return entity.toString();
    }
}
