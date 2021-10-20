package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.domain.entity.PatientRecordEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.ChannelDAO;
import ru.gsa.biointerface.persistence.dao.PatientRecordDAO;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class Channel implements Comparable<Channel> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Channel.class);
    private final ChannelEntity entity;

    static public Set<Channel> getAll() throws DomainException {
        try {
            List<ChannelEntity> entities = ChannelDAO.getInstance().getAll();
            Set<Channel> channels = new TreeSet<>();
            entities.forEach(o -> channels.add(new Channel(o)));
            return channels;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getAll channels error");
        }
    }

    public Channel(ChannelEntity entity) {
        if(entity == null)
            throw new NullPointerException("Entity is null");

        this.entity = entity;
    }

    public Channel(String name, String comment) throws DomainException {
        if (name == null)
            throw new NullPointerException("Name is null");
        if ("".equals(name))
            throw new IllegalArgumentException("Name is empty");

        try {
            ChannelEntity readEntity = ChannelDAO.getInstance().read(name);

            if (readEntity == null) {
                entity = new ChannelEntity(name, comment);
                ChannelDAO.getInstance().insert(entity);
                LOGGER.info("{} is recorded in database", entity);
            } else {
                entity = readEntity;
            }

        } catch (PersistenceException e) {
            throw new DomainException("DAO insert channel error");
        }
    }

    public void delete() throws DomainException {
        try {
            ChannelDAO.getInstance().delete(entity);
            LOGGER.info("{} is deleted in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO delete channel error");
        }
    }

    public ChannelEntity getEntity() {
        return entity;
    }

    public String getName() {
        return entity.getName();
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) throws DomainException {
        entity.setComment(comment);
        try {
            ChannelDAO.getInstance().update(entity);
            LOGGER.info("{} is update in {}", comment, entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO update channel error");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel device = (Channel) o;
        return entity.equals(device.entity);
    }

    @Override
    public int hashCode() {
        return entity.hashCode();
    }

    @Override
    public int compareTo(Channel o) {
        return entity.compareTo(o.entity);
    }

    @Override
    public String toString() {
        return entity.toString();
    }
}
