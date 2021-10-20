package ru.gsa.biointerface.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.persistence.PersistenceException;
import ru.gsa.biointerface.persistence.dao.IcdDAO;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
public class Icd implements Comparable<Icd> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Icd.class);
    private IcdEntity entity;

    static public Set<Icd> getAll() throws DomainException {
        try {
            List<IcdEntity> IcdEntity = IcdDAO.getInstance().getAll();
            Set<Icd> icds = new TreeSet<>();
            IcdEntity.forEach(o -> icds.add(new Icd(o)));
            return icds;
        } catch (PersistenceException e) {
            throw new DomainException("DAO getAll ICDs error");
        }
    }

    public Icd(String ICD, int version, String comment) throws DomainException {
        if (ICD == null)
            throw new NullPointerException("ICD is null");
        if ("".equals(ICD))
            throw new NullPointerException("ICD is empty");
        if (version <= 0)
            throw new IllegalArgumentException("version <= 0");

        entity = new IcdEntity(-1, ICD, version, comment);

        try {
            entity = IcdDAO.getInstance().insert(entity);
            LOGGER.info("{} is recorded in database", entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO insert ICD error");
        }
    }

    public Icd(IcdEntity entity) {
        if(entity == null)
            throw new NullPointerException("Entity is null");

        this.entity = entity;
    }

    public void delete() throws DomainException {
        try {
            IcdDAO.getInstance().delete(entity);
            LOGGER.info("{} is deleted in database", entity);
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
        return entity.getIcd();
    }

    public int getVersion() {
        return entity.getVersion();
    }

    public String getComment() {
        return entity.getComment();
    }

    public void setComment(String comment) throws DomainException {
        entity.setComment(comment);
        try {
            IcdDAO.getInstance().update(entity);
            LOGGER.info("{} is update in {}", comment, entity);
        } catch (PersistenceException e) {
            throw new DomainException("DAO update ICD error");
        }
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
        return entity.hashCode();
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
