package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.ChannelEntity;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.ChannelDAO;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class Channel implements Comparable<Channel> {
    private ChannelEntity entity;

    public Channel(int id, String name, String comment) {
        this(new ChannelEntity(id, name, comment));
    }

    public Channel(ChannelEntity entity) {
        if (entity == null)
            throw new NullPointerException("entity is null");
        if ("".equals(entity.getName()))
            throw new IllegalArgumentException("name is empty");

        this.entity = entity;
    }

    static public Set<Channel> getAll() throws DomainException {
        try {
            Set<ChannelEntity> entities = ChannelDAO.getInstance().getAll();
            Set<Channel> channels = new TreeSet<>();
            entities.forEach(o -> channels.add(new Channel(o)));
            return channels;
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao getAll icds error");
        }
    }

    public void insert() throws DomainException {
        try {
            entity = ChannelDAO.getInstance().insert(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao insert icd error");
        }
    }

    public void update() throws DomainException {
        try {
            ChannelDAO.getInstance().update(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao update icd error");
        }
    }

    public void delete() throws DomainException {
        try {
            ChannelDAO.getInstance().delete(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao delete icd error");
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
    }

    @Override
    public String toString() {
        return entity.getName();
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
}
