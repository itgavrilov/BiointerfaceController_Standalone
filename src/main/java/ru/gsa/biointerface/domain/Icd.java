package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.IcdDAO;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class Icd implements Comparable<Icd> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Icd.class);
    private IcdEntity entity;

    public Icd(int id, String ICD, int version, String comment) {
        this(new IcdEntity(id, ICD, version, comment));
    }

    public Icd(IcdEntity entity) {
        if (entity.getICD() == null)
            throw new NullPointerException("ICD is null");
        if (entity.getVersion() <= 0)
            throw new IllegalArgumentException("version is null");

        this.entity = entity;
    }

    static public Set<Icd> getAll() throws DomainException {
        try {
            Set<IcdEntity> IcdEntity = IcdDAO.getInstance().getAll();
            Set<Icd> icds = new TreeSet<>();
            IcdEntity.forEach(o -> icds.add(new Icd(o)));
            return icds;
        } catch (PersistenceException e) {
            throw new DomainException("Dao getAll ICDs error");
        }
    }

    public void insert() throws DomainException {
        try {
            entity = IcdDAO.getInstance().insert(entity);
            LOGGER.info("ICD '{}' is recorded in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO insert ICD error");
        }
    }

    public void update() throws DomainException {
        try {
            IcdDAO.getInstance().update(entity);
            LOGGER.info("ICD '{}' is updated in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO update ICD error");
        }
    }

    public void delete() throws DomainException {
        try {
            IcdDAO.getInstance().delete(entity);
            LOGGER.info("ICD '{}' is deleted in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO delete icd error");
        }
    }

    public IcdEntity getEntity() {
        return entity;
    }

    public int getId() {
        return entity.getId();
    }

    public String getICD() {
        return entity.getICD();
    }

    public int getVersion() {
        return entity.getVersion();
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) {
        LOGGER.info("Comment '{}' is update in ICD '{}'", comment, entity);
        entity.setComment(comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Icd icd = (Icd) o;
        return entity.equals(icd.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }

    @Override
    public int compareTo(Icd o) {
        return entity.compareTo(o.entity);
    }

    @Override
    public String toString() {
        return entity.toString();
    }
}
