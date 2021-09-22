package ru.gsa.biointerface.domain;

import ru.gsa.biointerface.domain.entity.IcdEntity;
import ru.gsa.biointerface.persistence.DAOException;
import ru.gsa.biointerface.persistence.dao.IcdDAO;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Icd implements Comparable<Icd> {
    private IcdEntity entity;

    public Icd(IcdEntity icdEntity) {
        if (icdEntity.getICD() == null)
            throw new NullPointerException("ICD is null");
        if (icdEntity.getVersion() <= 0)
            throw new IllegalArgumentException("version is null");

        entity = icdEntity;
    }

    public Icd(int id, String ICD, int version, String comment) {
        if (ICD == null)
            throw new NullPointerException("ICD is null");
        if (version <= 0)
            throw new IllegalArgumentException("version is null");

        entity = new IcdEntity(id, ICD, version, comment);
    }

    static public Set<Icd> getSetAll() throws DomainException {
        try {
            Set<IcdEntity> IcdEntity = IcdDAO.getInstance().getAll();
            Set<Icd> icds = new TreeSet<>();
            IcdEntity.forEach(o -> icds.add(new Icd(o)));
            return icds;
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao getAll icds error");
        }
    }

    public void insert() throws DomainException {
        try {
            entity = IcdDAO.getInstance().insert(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao insert icd error");
        }
    }

    public void update() throws DomainException {
        try {
            IcdDAO.getInstance().update(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao update icd error");
        }
    }

    public void delete() throws DomainException {
        try {
            IcdDAO.getInstance().delete(entity);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new DomainException("dao delete icd error");
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
    public String toString() {
        return getICD() + " (ICD-" + getVersion() + ")";
    }

    @Override
    public int compareTo(Icd o) {
        return entity.compareTo(o.entity);
    }
}
