package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.ChannelDAO;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class Channel implements Comparable<Channel> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Channel.class);
    private ChannelEntity entity;

    public Channel(int id, String name, String comment) {
        this(new ChannelEntity(id, name, comment));
    }

    public Channel(ChannelEntity entity) {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        if ("".equals(entity.getName()))
            throw new IllegalArgumentException("Name is empty");

        this.entity = entity;
    }

    static public Set<Channel> getAll() throws DomainException {
        try {
            Set<ChannelEntity> entities = ChannelDAO.getInstance().getAll();
            Set<Channel> channels = new TreeSet<>();
            entities.forEach(o -> channels.add(new Channel(o)));
            return channels;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getAll channels error");
        }
    }

    public void insert() throws DomainException {
        try {
            entity = ChannelDAO.getInstance().insert(entity);
            LOGGER.info("Channel is recorded in database wish id '{}'", entity.getId());
        } catch (PersistenceException e) {
            throw new DomainException("DAO insert channel error");
        }
    }

    public void update() throws DomainException {
        try {
            ChannelDAO.getInstance().update(entity);
            LOGGER.info("Channel '{}' is updated in database", entity.getId());
        } catch (PersistenceException e) {
            throw new DomainException("DAO update channel error");
        }
    }

    public void delete() throws DomainException {
        try {
            ChannelDAO.getInstance().delete(entity);
            LOGGER.info("Channel '{}' is deleted in database", entity.getId());
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

    public void setComment(String comment) {
        entity.setComment(comment);
        LOGGER.info("Comment is update in channel '{}'", entity.getId());
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
        return Objects.hash(entity);
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
